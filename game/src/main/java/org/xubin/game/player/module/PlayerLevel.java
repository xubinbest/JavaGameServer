package org.xubin.game.player.module;

import lombok.extern.slf4j.Slf4j;
import org.xubin.game.attribute.AttrUtils;
import org.xubin.game.attribute.BaseAttr;
import org.xubin.game.base.GameContext;
import org.xubin.game.data.DataCfgManager;
import org.xubin.game.data.data.LevelCfg;
import org.xubin.game.database.game.user.entity.Player;
import org.xubin.game.listener.EventDispatcher;
import org.xubin.game.listener.EventType;
import org.xubin.game.player.events.PlayerLevelUpEvent;
import org.xubin.game.player.message.s2c.PlayerLevelUpdateS2C;
import xbgame.socket.share.IdSession;

import java.util.Map;

@Slf4j
public class PlayerLevel {

    public static void addExp(long playerId, long exp) {
        Player player = GameContext.getPlayerService().getPlayer(playerId);
        long curExp = player.getExp();
        long newExp = curExp + exp;
        player.setExp(newExp);
        int level = player.getLevel();
        log.info("Player {} add exp, curExp={}, newExp={}, level={}", playerId, curExp, newExp, level);
        checkLevelUp(player);
        GameContext.getPlayerService().savePlayer(player);
        sendLevelUpdate(player);
        if (level != player.getLevel()) {
            PlayerLevelUpEvent event = new PlayerLevelUpEvent(EventType.PLAYER_LEVEL_UP, playerId);
            EventDispatcher.getInstance().triggerEvent(event);
        }
    }

    public static void sendLevelUpdate(Player player) {
        long playerId = player.getId();
        IdSession session = GameContext.getSessionManager().getSessionBy(playerId);
        if (session == null) {
            return;
        }
        PlayerLevelUpdateS2C msg = new PlayerLevelUpdateS2C();
        msg.setLevel(player.getLevel());
        msg.setExp(player.getExp());
        session.send(msg);
    }

    public static void checkLevelUp(Player player) {
        int level = player.getLevel();
        long exp = player.getExp();

        Map<Long, Object> dataCfgMap = GameContext.getDataCfgManager().getDataCfg("LevelCfg");

        // 获取最大等级
        long maxLevel = dataCfgMap.keySet().stream().mapToLong(Long::longValue).max().orElse(0);

        LevelCfg levelCfg = (LevelCfg) dataCfgMap.get((long) level);

        if (levelCfg == null) {
            log.error("LevelCfg not found, level={}", level);
            return;
        }

        long needExp = levelCfg.getExp();
        log.info("needExp {}", needExp);

        if (exp < needExp) {
            return;
        }

        while (exp >= needExp && level < maxLevel) {
            log.info("exp={}, needExp={}, level={}", exp, needExp, level);
            level++;
            exp -= needExp;
            levelCfg = (LevelCfg) dataCfgMap.get((long) level);
            if (levelCfg == null) {
                log.error("LevelCfg not found, level={}", level);
                break;
            }
            needExp = levelCfg.getExp();
        }
        player.setLevel(level);
        player.setExp(exp);
    }

    // 获取玩家等级属性
    public static BaseAttr getLevelAttr(long playerId) {
        Player player = GameContext.getPlayerService().getPlayer(playerId);
        int level = player.getLevel();
        return calcLevelAttr(level);
    }

    // 通过表格读取等级属性
    public static BaseAttr calcLevelAttr(int level) {
        DataCfgManager dataCfgManager = GameContext.getDataCfgManager();
        LevelCfg levelCfg = (LevelCfg) dataCfgManager.getById("LevelCfg", level);
        BaseAttr attr = new BaseAttr();
        if(levelCfg == null) {
            log.error("LevelCfg not found, level={}", level);
            return attr;
        }

        Map<String, Long> attrMap = (Map<String, Long>) levelCfg.getAttr();

        return AttrUtils.attrMap2BaseAttr(attrMap);
    }

}
