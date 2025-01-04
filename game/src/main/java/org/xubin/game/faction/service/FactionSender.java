package org.xubin.game.faction.service;

import org.xubin.game.base.GameContext;
import org.xubin.game.database.game.faction.entity.Faction;
import org.xubin.game.database.game.faction.entity.FactionApply;
import org.xubin.game.database.game.faction.entity.FactionMember;
import org.xubin.game.database.game.user.entity.PlayerEnt;
import org.xubin.game.faction.FactionUtils;
import org.xubin.game.faction.message.s2c.*;
import org.xubin.game.faction.message.vo.FactionApplyVo;
import org.xubin.game.faction.message.vo.FactionInfoVo;
import org.xubin.game.faction.message.vo.FactionMemberVo;
import org.xubin.game.player.service.PlayerService;
import xbgame.socket.share.IdSession;
import xbgame.socket.share.message.Message;

import java.util.ArrayList;
import java.util.List;

public class FactionSender {

    public static void sendApplyFactionS2C(long playerId, int code, long factionId) {
        FactionApplyS2C msg = new FactionApplyS2C();
        msg.setCode(code);
        msg.setFactionId(factionId);
        send(playerId, msg);
    }

    public static void sendFactionInfoS2C(long playerId, Faction faction) {
        FactionInfoVo factionInfoVo = FactionUtils.faction2FactionVo(faction);
        FactionInfoS2C msg = new FactionInfoS2C();
        msg.setFactionInfo(factionInfoVo);
        send(playerId, msg);
    }

    public static void sendFactionMemberListS2C(long playerId, List<FactionMember> factionMemberList) {
        List<FactionMemberVo> factionInfoVoList = new ArrayList<>();
        PlayerService playerService = GameContext.getPlayerService();
        for (FactionMember factionMember : factionMemberList) {
            FactionMemberVo factionInfoVo = new FactionMemberVo();
            factionInfoVo.setId(factionMember.getId());
            PlayerEnt playerEnt = playerService.getPlayer(factionMember.getId());
            if (playerEnt != null) {
                factionInfoVo.setName(playerEnt.getName());
                factionInfoVo.setLevel(playerEnt.getLevel());
            }
            factionInfoVoList.add(factionInfoVo);
        }

        FactionMemberListS2C msg = new FactionMemberListS2C();
        msg.setMembers(factionInfoVoList);
        send(playerId, msg);
    }

    public static void sendCreateFactionS2C(long playerId, int code, Faction faction) {
        FactionInfoVo factionInfoVo = FactionUtils.faction2FactionVo(faction);
        FactionCreateS2C msg = new FactionCreateS2C();
        msg.setCode(code);
        msg.setFaction(factionInfoVo);
        send(playerId, msg);
    }

    public static void sendFactionListS2C(long playerId, List<Faction> factions) {
        List<FactionInfoVo> factionInfoVoList = new ArrayList<>();
        for (Faction faction : factions) {
            FactionInfoVo factionInfoVo = FactionUtils.faction2FactionVo(faction);
            factionInfoVoList.add(factionInfoVo);
        }

        FactionListS2C msg = new FactionListS2C();
        msg.setFactions(factionInfoVoList);
        send(playerId, msg);
    }

    public static void broadcastNewApply(List<Long> ids, FactionApply factionApply) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        FactionNewApplyS2C msg = new FactionNewApplyS2C();
        FactionApplyVo factionApplyVo = new FactionApplyVo();

        factionApplyVo.setPlayerId(factionApply.getPlayerId());
        PlayerEnt playerEnt = GameContext.getPlayerService().getPlayer(factionApply.getPlayerId());
        if (playerEnt != null) {
            factionApplyVo.setPlayerName(playerEnt.getName());
            factionApplyVo.setLevel(playerEnt.getLevel());
        }
        msg.setApply(factionApplyVo);

        for (long id : ids) {
            send(id, msg);
        }
    }

    public static void sendDealApplyS2C(long playerId, int code, long applyId) {
        FactionDealApplyS2C msg = new FactionDealApplyS2C();
        msg.setCode(code);
        msg.setPlayerId(applyId);
        send(playerId, msg);
    }

    public static void broadcastNewMember(List<Long> ids, FactionMember factionMember) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        FactionNewMemberS2C msg = new FactionNewMemberS2C();
        FactionMemberVo factionMemberVo = new FactionMemberVo();

        factionMemberVo.setId(factionMember.getId());
        PlayerEnt playerEnt = GameContext.getPlayerService().getPlayer(factionMember.getId());
        if (playerEnt != null) {
            factionMemberVo.setName(playerEnt.getName());
            factionMemberVo.setLevel(playerEnt.getLevel());
        }
        msg.setMember(factionMemberVo);

        broadcastMsg(ids, msg);
    }

    public static void broadcastManagerRemoveApply(List<Long> ids, long applyId) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        FactionRemoveApplyS2C msg = new FactionRemoveApplyS2C();
        msg.setApplyId(applyId);
        broadcastMsg(ids, msg);
    }

    public static void sendFactionApplyListS2C(long playerId, List<FactionApply> factionApplyList) {
        List<FactionApplyVo> factionApplyVoList = new ArrayList<>();
        PlayerService playerService = GameContext.getPlayerService();
        for (FactionApply factionApply : factionApplyList) {
            FactionApplyVo factionApplyVo = new FactionApplyVo();
            factionApplyVo.setPlayerId(factionApply.getPlayerId());
            PlayerEnt playerEnt = playerService.getPlayer(factionApply.getPlayerId());
            if (playerEnt != null) {
                factionApplyVo.setPlayerName(playerEnt.getName());
                factionApplyVo.setLevel(playerEnt.getLevel());
            }
            factionApplyVoList.add(factionApplyVo);
        }

        FactionApplyListS2C msg = new FactionApplyListS2C();
        msg.setApplyList(factionApplyVoList);
        send(playerId, msg);
    }

    public static void broadcastRemoveMember(List<Long> ids, long playerId) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        FactionRemoveMemberS2C msg = new FactionRemoveMemberS2C();
        msg.setMemberId(playerId);
        broadcastMsg(ids, msg);
    }

    public static void broadcastMsg(Iterable<Long> ids, Message msg) {
        for (long id : ids) {
            send(id, msg);
        }
    }

    public static void send(long playerId, Message msg) {
        IdSession session = GameContext.getSessionManager().getSessionBy(playerId);
        if (session == null) {
            return;
        }
        session.send(msg);
    }
}
