package org.xubin.game.equip;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xubin.game.attribute.AttrUtils;
import org.xubin.game.bag.service.BagService;
import org.xubin.game.base.GameContext;
import org.xubin.game.data.DataCfgManager;
import org.xubin.game.data.data.EquipCfg;
import org.xubin.game.data.data.ItemCfg;
import org.xubin.game.equip.events.EquipChangeEvent;
import org.xubin.game.equip.message.EquipInfoS2C;
import org.xubin.game.equip.message.EquipUpdateS2C;
import org.xubin.game.equip.message.vo.EquipInfoVo;
import org.xubin.game.item.Item;
import org.xubin.game.item.ItemType;
import org.xubin.game.attribute.BaseAttr;
import org.xubin.game.listener.EventDispatcher;
import org.xubin.game.listener.EventType;
import xbgame.commons.NumberUtil;
import xbgame.socket.share.IdSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class EquipService {
    @Autowired
    private BagService bagService;

    @Autowired
    private DataCfgManager dataCfgManager;

    // 存储玩家装备信息 playerId -> slot -> item
    private final Map<Long, Map<EquipSlot, Long>> playerEquips = new ConcurrentHashMap<>();

    // 玩家物品加载成功后，处理装备信息
    public void initPlayerEquip(long playerId) {
        Map<EquipSlot, Long> equips = new ConcurrentHashMap<>();
        List<Item> itemList = bagService.getAllItems(playerId);
        for (Item item : itemList) {
            ItemCfg itemCfg = (ItemCfg) dataCfgManager.getById("ItemCfg", item.getItemId());
            if (itemCfg == null || itemCfg.getType() != ItemType.EQUIP.getValue() || item.getInUse() == 0) {
                continue;
            }

            EquipSlot slot = EquipSlot.valueOf(itemCfg.getSubtype());
            log.info("slot {}, item {}", slot, item.getId());
            equips.put(slot, item.getId());
        }
        playerEquips.put(playerId, equips);
    }

    /**
     * 穿装备
     */
    public void wearEquip(IdSession session, long itemUid) {
        long playerId = GameContext.getSessionManager().getPlayerIdBy(session);
        Item item = bagService.getItemById(playerId, itemUid);
        // 检查背包中是否有该物品 或者 是否已经穿戴
        if (item == null || item.getInUse() == 1) {
            return;
        }

        long itemId = item.getItemId();
        ItemCfg itemCfg = (ItemCfg) dataCfgManager.getById("ItemCfg", itemId);
        if (itemCfg == null || itemCfg.getType() != ItemType.EQUIP.getValue()) {
            return;
        }

        // 通过装备类型获取装备槽位
        EquipSlot slot = EquipSlot.valueOf(itemCfg.getSubtype());

        // 获取玩家装备槽
        Map<EquipSlot, Long> equips = playerEquips.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());

        // 更新物品信息
        List<Item> updateItems = new ArrayList<>();

        // 如果槽位已有装备，先卸下
        Long oldItemUid = equips.get(slot);
        if (oldItemUid != null && oldItemUid != 0) {
            Item oldItem = bagService.getItemById(playerId, oldItemUid);
            if (oldItem != null) {
                oldItem.setNotInUse();
                bagService.updateItem(oldItem);
                updateItems.add(oldItem);
            }
        }

        // 穿上新装备
        equips.put(slot, itemUid);
        item.setInUse();
        bagService.updateItem(item);
        updateItems.add(item);
        // 发送更新物品消息
        BagService.sendItemUpdate(session, updateItems);

        sendEquipUpdate(session, slot.getValue(), itemId);

        EquipChangeEvent equipChangeEvent = new EquipChangeEvent(EventType.EQUIP_CHANGE, playerId);
        EventDispatcher.getInstance().triggerEvent(equipChangeEvent);
    }

    public void sendEquipUpdate(IdSession session, int slot, long itemId) {
        EquipInfoVo equipInfoVo = new EquipInfoVo();
        equipInfoVo.setSlot(slot);
        equipInfoVo.setItemId((int) itemId);
        EquipUpdateS2C msg = new EquipUpdateS2C();
        msg.setEquip(equipInfoVo);
        session.send(msg);
    }

    public BaseAttr getEquipAttr(long playerId) {
        List<Item> equips = getEquipList(playerId);
        BaseAttr totalAttr = new BaseAttr();
        for (Item equip : equips) {
            EquipCfg equipCfg = (EquipCfg) dataCfgManager.getById("EquipCfg", equip.getItemId());
            if (equipCfg == null) {
                log.error("itemCfg not found, itemId:{}", equip.getItemId());
                continue;
            }
            Map<String, Long> attrMap = (Map<String, Long>) equipCfg.getAttr();
            BaseAttr baseAttr = AttrUtils.attrMap2BaseAttr(attrMap);
            totalAttr = AttrUtils.mergeBaseAttr(totalAttr, baseAttr);
        }

        return totalAttr;
    }

    /**
     * 获取装备列表
     */
    public List<Item> getEquipList(long playerId) {
        Map<EquipSlot, Long> equips = playerEquips.get(playerId);
        if (equips == null) {
            return Collections.emptyList();
        }

        List<Item> result = new LinkedList<>();
        for (Map.Entry<EquipSlot, Long> entry : equips.entrySet()) {
            long itemUid = entry.getValue();
            if (itemUid == 0) {
                continue;
            }

            Item item = bagService.getItemById(playerId, itemUid);
            if (item == null) {
                log.error("playerId:{} itemUid:{} not found", playerId, itemUid);
                continue;
            }
            result.add(item);
        }

        return result;
    }

    public void sendEquipInfo(IdSession session) {
        long playerId = GameContext.getSessionManager().getPlayerIdBy(session);
        List<EquipInfoVo> equipInfoVos = new ArrayList<>();
        Map<EquipSlot, Long> equps = playerEquips.get(playerId);

        for (Map.Entry<EquipSlot, Long> entry : equps.entrySet()) {
            EquipInfoVo equipInfoVo = new EquipInfoVo();
            equipInfoVo.setSlot(entry.getKey().getValue());
            long itemUid = entry.getValue();
            Item item = bagService.getItemById(playerId, itemUid);
            ItemCfg itemCfg = (ItemCfg) dataCfgManager.getById("ItemCfg", item.getItemId());
            equipInfoVo.setItemId(itemCfg.getId());
            equipInfoVos.add(equipInfoVo);
        }
        EquipInfoS2C equipInfoS2C = new EquipInfoS2C();
        equipInfoS2C.setEquips(equipInfoVos);
        session.send(equipInfoS2C);
    }
}