package org.xubin.game.attribute.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xubin.game.attribute.AttrChange;
import org.xubin.game.attribute.AttrFrom;
import org.xubin.game.attribute.AttrUtils;
import org.xubin.game.attribute.BaseAttr;
import org.xubin.game.base.GameContext;
import org.xubin.game.player.message.s2c.PlayerAttrInfoS2C;
import org.xubin.game.player.message.s2c.PlayerAttrUpdateS2C;
import org.xubin.game.player.message.vo.AttrVo;
import org.xubin.game.player.module.PlayerLevel;
import xbgame.socket.share.IdSession;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家属性服务
 */

@Service
@Slf4j
public class PlayerAttrService {
    private static Map<Long,Map<AttrFrom, BaseAttr>> playerAttrMap = new ConcurrentHashMap<>();

    public void initPlayerAttr(long playerId) {
        Map<AttrFrom, BaseAttr> attrMap = playerAttrMap.get(playerId);
        if (attrMap == null) {
            attrMap = doInitPlayerAttr(playerId);
            playerAttrMap.put(playerId, attrMap);
        }

        sendPlayerAttrInfo(playerId);
    }

    // 重新计算玩家属性
    // 只计算变动的系统的属性和总属性，其他系统的属性不变
    public void reCalcPlayerAttr(long playerId, AttrFrom attrFrom) {
        Map<AttrFrom, BaseAttr> attrMap = playerAttrMap.get(playerId);
        if (attrMap == null) {
            initPlayerAttr(playerId);
        } else {
            BaseAttr totalAttr = attrMap.get(AttrFrom.TOTAL);
            BaseAttr oldBaseAttr = attrMap.get(attrFrom);
            BaseAttr newBaseAttr = getAttrByFrom(playerId, attrFrom);
            List<AttrChange> changes = AttrUtils.calcAttrChange(oldBaseAttr, newBaseAttr);
            if (changes.isEmpty()) {
                return;
            }
            for(AttrChange change : changes) {
                try {
                    Field field = BaseAttr.class.getDeclaredField(change.getName());
                    field.setAccessible(true);
                    long oldTotalValue = field.getLong(totalAttr);
                    field.set(totalAttr, oldTotalValue + change.getChange());
                } catch (Exception e) {
                    log.error("reCalcPlayerAttr error", e);
                }
            }
            attrMap.put(attrFrom, newBaseAttr);
            sendPlayerAttrChange(playerId, changes);
        }
    }

    // 发送属性变化
    public void sendPlayerAttrChange(long playerId, List<AttrChange> changes) {
        if(changes == null || changes.isEmpty()) {
            return;
        }
        IdSession session = GameContext.getSessionManager().getSessionBy(playerId);

        if (session == null) {
            return;
        }

        List<AttrVo> list = new ArrayList<>();

        for(AttrChange change : changes) {
            AttrVo attrVo = new AttrVo();
            attrVo.setName(change.getName());
            attrVo.setValue(change.getValue());
            list.add(attrVo);
        }

        PlayerAttrUpdateS2C msg = new PlayerAttrUpdateS2C();
        msg.setAttrs(list);
        session.send(msg);
    }

    public Map<AttrFrom, BaseAttr> doInitPlayerAttr(long playerId) {
        Map<AttrFrom, BaseAttr> attrMap = new HashMap<>();
        BaseAttr totalAttr = new BaseAttr();

        for (AttrFrom attrFrom : AttrFrom.values()) {
            if (attrFrom == AttrFrom.TOTAL) {
                continue;
            }
            BaseAttr baseAttr = getAttrByFrom(playerId, attrFrom);
            attrMap.put(attrFrom, baseAttr);
            totalAttr = AttrUtils.mergeBaseAttr(totalAttr, baseAttr);
        }

        attrMap.put(AttrFrom.TOTAL, totalAttr);

        return attrMap;
    }

    public BaseAttr getAttrByFrom(long playerId, AttrFrom attrFrom) {
        BaseAttr baseAttr = new BaseAttr();
        switch (attrFrom) {
            case TOTAL -> {}
            case EQUIP ->
                baseAttr = GameContext.getEquipService().getEquipAttr(playerId);
            case LEVEL ->
                baseAttr = PlayerLevel.getLevelAttr(playerId);
            default ->
                log.error("attrFrom {} not support", attrFrom);
        }
        return baseAttr;
    }

    public static void sendPlayerAttrInfo(long playerId) {
        Map<AttrFrom, BaseAttr> attrMap = playerAttrMap.get(playerId);
        if (attrMap == null) {
            log.error("player {} attrMap is null", playerId);
            return;
        }
        BaseAttr totalAttr = attrMap.get(AttrFrom.TOTAL);
        if (totalAttr == null) {
            log.error("player {} totalAttr is null", playerId);
            return;
        }

        IdSession session = GameContext.getSessionManager().getSessionBy(playerId);
        if(session == null) {
            log.error("player {} session is null", playerId);
            return;
        }

        List<AttrVo> attrVos = packAttrVo(totalAttr);
        PlayerAttrInfoS2C msg = new PlayerAttrInfoS2C();
        msg.setAttrs(attrVos);
        session.send(msg);
    }

    public static List<AttrVo> packAttrVo(BaseAttr baseAttr) {
        List<AttrVo> result = new ArrayList<>();
        Field[] fields = BaseAttr.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (AttrUtils.isAttrField(field)) {
                    long value = field.getLong(baseAttr);
                    AttrVo attrVo = new AttrVo();
                    attrVo.setName(field.getName());
                    attrVo.setValue(value);
                    result.add(attrVo);
                }
            } catch (IllegalAccessException e) {
                log.error("packAttrVo error", e);
            }
        }
        return result;
    }
}
