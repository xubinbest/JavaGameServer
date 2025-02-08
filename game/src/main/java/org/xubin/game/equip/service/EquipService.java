package org.xubin.game.equip.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xubin.game.attribute.AttrUtils;
import org.xubin.game.bag.service.BagService;
import org.xubin.game.base.GameContext;
import org.xubin.game.commons.utils.IdGenerator;
import org.xubin.game.data.DataCfgManager;
import org.xubin.game.data.data.EquipCfg;
import org.xubin.game.data.data.ItemCfg;
import org.xubin.game.database.game.item.entity.Equip;
import org.xubin.game.equip.EquipSlotEnum;
import org.xubin.game.equip.events.EquipChangeEvent;
import org.xubin.game.equip.message.EquipInfoS2C;
import org.xubin.game.equip.message.EquipUpdateS2C;
import org.xubin.game.equip.message.vo.EquipInfoVo;
import org.xubin.game.database.game.item.entity.Item;
import org.xubin.game.item.ItemType;
import org.xubin.game.attribute.BaseAttr;
import org.xubin.game.listener.EventDispatcher;
import org.xubin.game.listener.EventType;
import xbgame.socket.share.IdSession;

import java.util.*;

@Service
@Slf4j
public class EquipService {
    @Autowired
    private BagService bagService;
    @Autowired
    private EquipCacheService equipCache;

    @Autowired
    private DataCfgManager dataCfgManager;

    /**
     * 玩家物品加载成功后，处理装备信息
     * @param playerId 玩家ID
     */
    public void loadPlayerEquip(long playerId) {
        equipCache.loadPlayerEquips(playerId);
    }

    /**
     * 穿装备
     * @param session session
     * @param itemUid 物品唯一id
     */
    public void wearEquip(IdSession session, long itemUid) {
        long playerId = GameContext.getSessionManager().getPlayerIdBy(session);
        Item item = bagService.getItemById(playerId, itemUid);
        // 检查背包中是否有该物品 或者 是否已经穿戴 或者 是否是装备
        if (item == null || item.isInUse() || !item.isEquip()) {
            return;
        }

        long itemId = item.getItemId();
        ItemCfg itemCfg = (ItemCfg) dataCfgManager.getById("ItemCfg", itemId);
        if (itemCfg == null || itemCfg.getType() != ItemType.EQUIP.getValue()) {
            return;
        }

        // 通过装备类型获取装备槽位
        EquipSlotEnum slot = EquipSlotEnum.valueOf(itemCfg.getSubtype());

        Equip equip = equipCache.getEquipByPlayerIdAndSlot(playerId, slot.getValue());

        // 更新物品信息
        List<Item> updateItems = new ArrayList<>();

        // 如果槽位已有装备，先卸下
        if (equip != null) {
            long oldItemUid = equip.getItemUid();
            if (oldItemUid != 0) {
                Item oldItem = bagService.getItemById(playerId, oldItemUid);
                if (oldItem != null) {
                    oldItem.setNotInUse();
                    bagService.updateItem(oldItem);
                    updateItems.add(oldItem);
                }
            }
            equip.setItemUid(itemUid);
            equipCache.putEquip(equip);
        } else {
            long id = IdGenerator.nextId();
            equip = new Equip(id, playerId, slot.getValue(), itemUid);
            equipCache.insertEquip(equip);
        }

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
        List<Equip> list = equipCache.listByPlayerId(playerId);
        if (list == null) {
            return Collections.emptyList();
        }

        List<Item> result = new LinkedList<>();
        for (Equip equip : list) {
            long itemUid = equip.getItemUid();
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
        List<Equip> equipList = equipCache.listByPlayerId(playerId);

        if (equipList != null) {
            for (Equip equip : equipList) {
                EquipInfoVo equipInfoVo = new EquipInfoVo();
                equipInfoVo.setSlot(equip.getSlot());
                long itemUid = equip.getItemUid();
                Item item = bagService.getItemById(playerId, itemUid);
                ItemCfg itemCfg = (ItemCfg) dataCfgManager.getById("ItemCfg", item.getItemId());
                equipInfoVo.setItemId(itemCfg.getId());
                equipInfoVos.add(equipInfoVo);
            }
        }

        EquipInfoS2C equipInfoS2C = new EquipInfoS2C();
        equipInfoS2C.setEquips(equipInfoVos);
        session.send(equipInfoS2C);
    }
}