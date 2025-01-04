package org.xubin.game.faction;

import org.xubin.game.base.GameContext;
import org.xubin.game.commons.utils.IdGenerator;
import org.xubin.game.database.game.faction.entity.Faction;
import org.xubin.game.database.game.faction.entity.FactionApply;
import org.xubin.game.database.game.faction.entity.FactionMember;
import org.xubin.game.database.game.user.entity.PlayerEnt;
import org.xubin.game.faction.message.vo.FactionInfoVo;

public class FactionUtils {
    private static final String DEFAULT_FACTION_NOTICE = "欢迎加入我们的公会";

    public static FactionInfoVo faction2FactionVo(Faction faction) {
        FactionInfoVo factionInfoVo = new FactionInfoVo();
        factionInfoVo.setId(faction.getId());
        factionInfoVo.setName(faction.getName());
        factionInfoVo.setLevel(faction.getLevel());
        PlayerEnt playerEnt = GameContext.getPlayerService().getPlayer(faction.getLeaderId());
        if (playerEnt != null) {
            factionInfoVo.setLeaderName(playerEnt.getName());
        }
        factionInfoVo.setNotice(faction.getNotice());
        factionInfoVo.setMemberNum(faction.getMemberNum());
        return  factionInfoVo;
    }

    public static Faction newFaction(long playerId, String factionName) {
        Faction faction = new Faction();
        faction.setId(IdGenerator.nextId());
        faction.setName(factionName);
        faction.setLevel(1);
        faction.setLeaderId(playerId);
        faction.setCreateTime(System.currentTimeMillis() / 1000);
        faction.setNotice(DEFAULT_FACTION_NOTICE);
        faction.setMemberNum(1);
        return faction;
    }

    public static FactionMember newFactionMember(long playerId, long factionId, FactionPosition position) {
        FactionMember factionMember = new FactionMember();
        factionMember.setId(playerId);
        factionMember.setFactionId(factionId);
        factionMember.setTime(System.currentTimeMillis() / 1000);
        factionMember.setPosition(position.getValue());
        return factionMember;
    }


    public static FactionApply newFactionApply(long playerId, long factionId) {
        FactionApply factionApply = new FactionApply();
        long id = IdGenerator.nextId();
        factionApply.setId(id);
        factionApply.setFactionId(factionId);
        factionApply.setPlayerId(playerId);
        factionApply.setApplyTime(System.currentTimeMillis() / 1000);
        // TODO: save to db
//        GameContext.getAsyncDbService().saveToDb(factionApply);
        return factionApply;
    }


}
