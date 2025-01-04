package org.xubin.game.faction.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xubin.game.asyncdb.AsyncDbService;
import org.xubin.game.database.game.BaseEntity;
import org.xubin.game.database.game.faction.dao.FactionApplyDao;
import org.xubin.game.database.game.faction.dao.FactionDao;
import org.xubin.game.database.game.faction.dao.FactionMemberDao;
import org.xubin.game.database.game.faction.entity.Faction;
import org.xubin.game.database.game.faction.entity.FactionApply;
import org.xubin.game.database.game.faction.entity.FactionMember;
import org.xubin.game.faction.FactionPosition;
import org.xubin.game.faction.FactionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 公会服务
 * @author xubin
 */
@Service
@Slf4j
public class FactionService {

    private final Map<Long, Faction> factionMap = new ConcurrentHashMap<>();
    private final Map<Long, FactionMember> factionMemberMap = new ConcurrentHashMap<>();
    private final Map<Long, Map<Long, FactionMember>> factionMemberListMap = new ConcurrentHashMap<>();
    private final Map<Long, Map<Long, FactionApply>> factionApplyMap = new ConcurrentHashMap<>();

    @Autowired
    private FactionDao factionDao;
    @Autowired
    private FactionMemberDao factionMemberDao;
    @Autowired
    private FactionApplyDao factionApplyDao;
    @Autowired
    private AsyncDbService asyncDbService;

    @PostConstruct
    public void init() {
        loadFaction();
    }

    /**
     * 推送公会信息
     * @param playerId 玩家id
     */
    public void sendFactionInfo(long playerId) {
        FactionMember factionMember = getFactionMember(playerId);
        if (factionMember == null) {
            sendNotInFaction(playerId);
            return;
        }

        Faction faction = getFaction(factionMember.getFactionId());
        if (faction == null) {
            sendNotInFaction(playerId);
            return;
        }

        FactionSender.sendFactionInfoS2C(playerId, faction);
    }

    /**
     * 推送公会成员列表
     * @param playerId 玩家id
     */
    public void sendFactionMemberList(long playerId) {
        FactionMember factionMember = getFactionMember(playerId);
        if (factionMember == null) {
            FactionSender.sendFactionMemberListS2C(playerId, new ArrayList<>());
            return;
        }
        List<FactionMember> factionMemberList = getFactionMemberList(factionMember.getFactionId());
        if (factionMemberList == null) {
            FactionSender.sendFactionMemberListS2C(playerId, new ArrayList<>());
            return;
        }

        FactionSender.sendFactionMemberListS2C(playerId, factionMemberList);
    }

    /**
     * 推送公会列表
     * @param playerId 玩家id
     */
    public void sendFactionList(long playerId) {
        List<Faction> factions = getFactionList();
        FactionSender.sendFactionListS2C(playerId, factions);
    }

    /**
     * 创建公会
     * @param playerId 玩家id
     * @param factionName 公会名
     */
    public void createFaction(long playerId, String factionName) {
        Faction faction = FactionUtils.newFaction(playerId, factionName);
        saveDb(faction);
        factionMap.put(faction.getId(), faction);
        createFactionMember(playerId, faction.getId(), FactionPosition.LEADER);
        FactionSender.sendCreateFactionS2C(playerId, 0, faction);
    }

    public void sendFactionApplyList(long playerId) {
        FactionMember factionMember = getFactionMember(playerId);
        if (factionMember == null || !factionMember.isManager()) {
            return;
        }

        List<FactionApply> factionApplyList = getFactionApplyList(factionMember.getFactionId());
        if (factionApplyList == null) {
            return;
        }

        FactionSender.sendFactionApplyListS2C(playerId, factionApplyList);
    }

    /**
     * 申请加入公会
     * @param playerId 玩家id
     * @param factionId 公会id
     */
    public void applyFaction(long playerId, long factionId) {
        FactionMember memberInfo = getFactionMember(playerId);
        if (memberInfo != null) {
            FactionSender.sendApplyFactionS2C(playerId, 1, factionId);
            return;
        }

        Faction faction = getFaction(factionId);
        if (faction == null) {
            FactionSender.sendApplyFactionS2C(playerId, 2, factionId);
            return;
        }

        Map<Long, FactionApply> applyList = factionApplyMap.computeIfAbsent(factionId, k -> new ConcurrentHashMap<>());
        if (applyList.containsKey(playerId)) {
            FactionSender.sendApplyFactionS2C(playerId, 3, factionId);
            return;
        }

        FactionApply factionApply = FactionUtils.newFactionApply(playerId, factionId);
        saveDb(factionApply);
        applyList.put(playerId, factionApply);
        FactionSender.sendApplyFactionS2C(playerId, 0, factionId);
        broadcastNewApply(factionApply);
    }

    /**
     * 处理申请
     * @param playerId 玩家id
     * @param applyPlayerId 申请玩家id
     * @param opt 操作 1同意 2拒绝
     */
    public void dealApply(long playerId, long applyPlayerId, int opt) {
        FactionMember factionMember = getFactionMember(playerId);
        if (factionMember == null || !factionMember.isManager()) {
            // 权限不足
            FactionSender.sendDealApplyS2C(playerId, 1, applyPlayerId);
            return;
        }

        long factionId = factionMember.getFactionId();
        Faction faction = getFaction(factionId);
        if (faction == null) {
            // 公会不存在, 正常不会走到这
            FactionSender.sendDealApplyS2C(playerId, 1, applyPlayerId);
            return;
        }

        FactionApply factionApply = getFactionApply(factionId, applyPlayerId);
        if (factionApply == null) {
            // 申请不存在(可能已经被处理)
            FactionSender.sendDealApplyS2C(playerId, 2, applyPlayerId);
            return;
        }

        if (opt == 1) {
            agreeApply(faction, factionApply);
        } else
        {
            refuseApply(faction, factionApply);
        }

        removeFactionApply(factionApply);

        FactionSender.sendDealApplyS2C(playerId, 0, applyPlayerId);
    }

    private void removeFactionApply(FactionApply apply) {
        apply.setDelete(true);
        saveDb(apply);
        long factionId = apply.getFactionId();
        long playerId = apply.getPlayerId();
        Map<Long, FactionApply> applyList = factionApplyMap.get(factionId);
        if (applyList == null) {
            return;
        }
        applyList.remove(playerId);
    }

    // 同意申请
    private void agreeApply(Faction faction, FactionApply apply) {
        long applyPlayerId = apply.getPlayerId();
        long factionId = faction.getId();
        FactionMember factionMember = createFactionMember(applyPlayerId, factionId, FactionPosition.MEMBER);
        faction.increaseMemberNum();
        broadcastNewMember(factionMember);
        broadcastManagerRemoveApply(apply);
    }

    // 推送管理员移除申请
    private void broadcastManagerRemoveApply(FactionApply apply) {
        long factionId = apply.getFactionId();
        long applyId = apply.getPlayerId();
        List<Long> managerIdList = getFactionManagerIdList(factionId);
        FactionSender.broadcastManagerRemoveApply(managerIdList, applyId);
    }

    // 推送新成员
    private void broadcastNewMember(FactionMember factionMember) {
        long factionId = factionMember.getFactionId();
        List<Long> managerIdList = getFactionManagerIdList(factionId);
        FactionSender.broadcastNewMember(managerIdList, factionMember);
    }

    // 拒绝申请
    // 当前什么都不做，后续可以怎加提示申请者被拒绝
    private void refuseApply(Faction faction, FactionApply apply) {
        broadcastManagerRemoveApply(apply);
    }

    /**
     * 公会踢人
     * @param playerId 玩家id
     * @param kickPlayerId 踢出玩家id
     */
    public void kickMember(long playerId, long kickPlayerId) {
        FactionMember factionMember = getFactionMember(playerId);
        if (factionMember == null || !factionMember.isManager()) {
            // 权限不足
            return;
        }

        FactionMember kickMember = getFactionMember(kickPlayerId);
        if (kickMember == null) {
            // 踢出的玩家不在公会
            return;
        }

        long factionId = factionMember.getFactionId();
        Faction faction = getFaction(factionId);
        if (faction == null) {
            // 公会不存在, 正常不会走到这
            return;
        }

        if (kickMember.isManager()) {
            // 不能踢出管理员
            return;
        }

        dealMemberLeave(kickMember);
    }

    /**
     * 离开公会
     * @param playerId 玩家id
     */
    public void leaveFaction(long playerId) {
        FactionMember factionMember = getFactionMember(playerId);
        if (factionMember == null) {
            return;
        }

        dealMemberLeave(factionMember);
    }

    private void broadcastRemoverMember(long factionId, long kickPlayerId) {
        List<Long> memberIdList = getFactionMemberIdList(factionId);
        FactionSender.broadcastRemoveMember(memberIdList, kickPlayerId);
    }

    private void removeFactionMember(FactionMember member) {
        long factionId = member.getFactionId();
        long playerId = member.getId();
        factionMemberListMap.get(factionId).remove(playerId);
        member.setDelete();
        saveDb(member);
    }

    private void dealMemberLeave(FactionMember member) {
        long factionId = member.getFactionId();
        long playerId = member.getId();

        removeFactionMember(member);

        Faction faction = getFaction(factionId);

        if (faction != null) {
            faction.decreaseMemberNum();
        }

        broadcastRemoverMember(factionId, playerId);
        switch (Objects.requireNonNull(FactionPosition.valueOf(member.getPosition()))) {
            case LEADER:
                dealLeaderLeave(member);
                break;
        }
    }

    private void dealLeaderLeave(FactionMember member) {
        long factionId = member.getFactionId();
        Map<Long, FactionMember> memberList = factionMemberListMap.get(factionId);
        if (memberList == null) {
            return;
        }

        FactionMember newLeader = null;
        for (FactionMember factionMember : memberList.values()) {
            if (!Objects.equals(factionMember.getId(), member.getId())) {
                newLeader = factionMember;
                break;
            }
        }

        if (newLeader == null) {
            factionMap.remove(factionId);
        } else {
            factionMap.get(factionId).setLeaderId(newLeader.getId());
            newLeader.setPosition(FactionPosition.LEADER.getValue());
        }
    }


    /**
     * 获取公会列表
     * @return 公会列表
     */
    public List<Faction> getFactionList() {
        return new ArrayList<>(factionMap.values());
    }

    /**
     * 获取公会
     * @param factionId 公会id
     * @return 公会
     */
    public Faction getFaction(long factionId) {
        return factionMap.get(factionId);
    }

    /**
     * 获取公会成员列表
     * @param factionId 公会id
     * @return 公会成员列表
     */
    public List<FactionMember> getFactionMemberList(long factionId) {
        return factionMemberListMap.get(factionId).values().stream().toList();
    }

    public List<Long> getFactionMemberIdList(long factionId) {
        Map<Long, FactionMember> memberList = factionMemberListMap.get(factionId);
        if (memberList == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(memberList.keySet());
    }

    /**
     * 获取公会成员
     * @param factionMemberId 公会成员id
     * @return 公会成员
     */
    public FactionMember getFactionMember(long factionMemberId) {
        return factionMemberMap.get(factionMemberId);
    }

    /**
     * 获取公会申请列表
     * @param factionId 公会id
     * @return 公会申请列表
     */
    public List<FactionApply> getFactionApplyList(long factionId) {
        Map<Long, FactionApply> applyMap = factionApplyMap.get(factionId);
        if(applyMap == null) {
            return new ArrayList<>();
        }
        return applyMap.values().stream().toList();
    }


    // 加载公会
    private void loadFaction() {
        List<Faction> factionList = factionDao.findAll();
        for(Faction faction : factionList) {
            factionMap.put(faction.getId(), faction);
        }
        loadFactionMember();
        loadFactionApply();
    }

    // 加载公会成员
    private void loadFactionMember() {
        List<FactionMember> factionMemberList = factionMemberDao.findAll();
        for (FactionMember factionMember : factionMemberList) {
            factionMemberMap.put(factionMember.getId(), factionMember);
            Map<Long, FactionMember> memberList = factionMemberListMap.computeIfAbsent(factionMember.getFactionId(), k -> new ConcurrentHashMap<>());
            memberList.put(factionMember.getId(), factionMember);
        }
    }

    // 加载公会申请
    private void loadFactionApply() {
        List<FactionApply> factionApplieList = factionApplyDao.findAll();
        for (FactionApply factionApply : factionApplieList) {
            long factionId = factionApply.getFactionId();
            Map<Long, FactionApply> applyList = factionApplyMap.computeIfAbsent(factionId, k -> new ConcurrentHashMap<>());
            applyList.put(factionApply.getPlayerId(), factionApply);
        }
    }

    // 给管理员推送新的申请 当前只有帮主
    private void broadcastNewApply(FactionApply factionApply) {
        List<Long> managerIdList = getFactionManagerIdList(factionApply.getFactionId());
        FactionSender.broadcastNewApply(managerIdList, factionApply);
    }

    // 获取公会管理员列表
    private List<Long> getFactionManagerIdList(long factionId) {
        List<Long> managerList = new ArrayList<>();
        Map<Long, FactionMember> memberList = factionMemberListMap.get(factionId);

        if (memberList == null) {
            return managerList;
        }
        for (FactionMember factionMember : memberList.values()) {
            if (factionMember.isManager()) {
                managerList.add(factionMember.getId());
            }
        }
        return managerList;
    }

    // 发送不在公会
    private void sendNotInFaction(long playerId) {
        FactionSender.sendFactionInfoS2C(playerId, new Faction());
    }

    // 创建公会成员
    private FactionMember createFactionMember(long playerId, long factionId, FactionPosition position) {
        FactionMember factionMember = FactionUtils.newFactionMember(playerId, factionId, position);
        saveDb(factionMember);
        factionMemberMap.put(factionMember.getId(), factionMember);
        Map<Long, FactionMember> memberList = factionMemberListMap.computeIfAbsent(factionId, k -> new ConcurrentHashMap<>());
        memberList.put(factionMember.getId(), factionMember);
        return factionMember;
    }

    // 获取公会一个申请
    private FactionApply getFactionApply(long factionId, long playerId) {
        Map<Long, FactionApply> applyList = factionApplyMap.get(factionId);
        if (applyList == null) {
            return null;
        }
        return applyList.get(playerId);
    }

    private void saveDb(BaseEntity<? extends Serializable> data) {
        if (isOpenDb()) {
            asyncDbService.saveToDb(data);
        }
    }

    private boolean isOpenDb() {
        return false;
    }
}
