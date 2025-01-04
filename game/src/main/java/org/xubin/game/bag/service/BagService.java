package org.xubin.game.bag.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.stereotype.Service;
import org.xubin.game.bag.message.s2c.BagInfoS2C;
import org.xubin.game.bag.message.s2c.BagItemUpdateS2C;
import org.xubin.game.bag.message.s2c.BagNewItemS2C;
import org.xubin.game.bag.message.vo.ItemVo;
import org.xubin.game.base.GameContext;
import org.xubin.game.commons.utils.IdGenerator;
import org.xubin.game.data.DataCfgManager;
import org.xubin.game.data.data.ItemCfg;
import org.xubin.game.database.game.item.dao.ItemDao;
import org.xubin.game.database.game.item.entity.ItemEnt;
import org.xubin.game.item.Item;
import xbgame.commons.ds.ConcurrentHashSet;
import xbgame.socket.share.IdSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 背包
 * @author xubin
 */
@Service
@Slf4j
public class BagService {
    @Autowired
    private ItemDao itemDao;

    private Set<Long> hadLoadPlayerIds = new ConcurrentHashSet<>();
    private Map<Long, Map<Long, Item>> bagMap = new ConcurrentHashMap<>();
    @Autowired
    private DataCfgManager dataCfgManager;

    public boolean isHadLoad(long playerId) {
        return hadLoadPlayerIds.contains(playerId);
    }

    public void loadItemList(long playerId) {
        List<Item> itemList = itemDao.getItemListByPlayerId(playerId);
        Map<Long, Item> itemMap = new HashMap<>();

        for(Item item : itemList) {
            itemMap.put(item.getId(), item);
        }

        bagMap.put(playerId, itemMap);
        hadLoadPlayerIds.add(playerId);
    }

    public void checkLoadPlayerItem(long playerId) {
        if (!isHadLoad(playerId)) {
            loadItemList(playerId);
            GameContext.getEquipService().initPlayerEquip(playerId);
        }
    }

    public void addItemByItemId(long playerId, long itemId, int num) {
        ItemCfg itemCfg = (ItemCfg) dataCfgManager.getById("ItemCfg", itemId);
        if (itemCfg == null) {
            log.error("itemCfg is null, itemId:{}", itemId);
            return;
        }

        int stack = itemCfg.getStack();
        if(stack <= 0) {
            log.error("item stack is 0, itemId:{}", itemId);
            return;
        }

        if (stack == 1) {
            // 不可叠加,直接创建新的
            addNewItem(playerId, itemId, num);
        } else {
            List<Item> items = getItemByItemId(playerId, itemId);
            if (items.isEmpty()) {
                // 没有该物品,直接创建新的
                addNewItem(playerId, itemId, num);
            } else {
                // 有该物品,计算叠加，都叠满了之后再创建新的
                addItemStack(playerId, itemId, num, items);
            }
        }
    }

    private void addNewItem(long playerId, long itemId, int num) {
        List<Item> addItems = new ArrayList<>();
        ItemCfg itemCfg = (ItemCfg) dataCfgManager.getById("ItemCfg", itemId);
        if (itemCfg == null) {
            log.error("itemCfg is null, itemId:{}", itemId);
            return;
        }

        int stack = itemCfg.getStack();

        while (num > 0) {
            long id = IdGenerator.nextId();
            int addNum = Math.min(num, stack);
            Item item = new Item(id, itemId, playerId, addNum, itemCfg.getColor(), itemCfg.getType(), (byte) 0);
            if(addNum == stack) {
                item.setStackFull();
            }
            addItem(item);
            addItems.add(item);
            num -= addNum;
        }

        sendNewItems(playerId, addItems);
    }

    private void addItemStack(long playerId, long itemId, int num, List<Item> items) {
        ItemCfg itemCfg = (ItemCfg) dataCfgManager.getById("ItemCfg", itemId);
        if (itemCfg == null) {
            log.error("itemCfg is null, itemId:{}", itemId);
            return;
        }

        List<Item> updateItems = new ArrayList<>();

        int stack = itemCfg.getStack();
        // 处理叠加
        for (Item item : items) {
            if(item.isStackFull()) {
                continue;
            }

            if (item.getNum() + num <= stack) {
                item.addNum(num);
                if (item.getNum() == stack) {
                    item.setStackFull();
                }
                updateItems.add(item);
                updateItem(item);
                num = 0;
                break;
            } else {
                int needNum = stack - item.getNum();
                item.setNum(stack);
                item.setStackFull();
                updateItem(item);
                updateItems.add(item);
                num -= needNum;
            }
        }

        IdSession session = GameContext.getSessionManager().getSessionBy(playerId);
        if (session != null) {
            sendItemUpdate(session, updateItems);
        }

        if(num > 0) {
            addNewItem(playerId, itemId, num);
        }
    }

    private void addItem(Item item) {
        long playerId = item.getPlayerId();
        Map<Long, Item> itemMap = bagMap.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
        itemMap.put(item.getId(), item);
        saveDb(item);
    }

    public void saveDb(Item item) {
        ItemEnt itemEnt = new ItemEnt();
        itemEnt.setId(item.getId());
        itemEnt.setItemId(item.getItemId());
        itemEnt.setPlayerId(item.getPlayerId());
        itemEnt.setNum(item.getNum());
        itemEnt.setColor(item.getColor().getValue());
        itemEnt.setType(item.getType().getValue());
        itemEnt.setInUse(item.getInUse());
        GameContext.getAsyncDbService().saveToDb(itemEnt);
    }

    public void updateItem(Item item) {
        long playerId = item.getPlayerId();
        Map<Long, Item> itemMap = bagMap.get(playerId);
        if (itemMap == null) {
            log.error("updateItem error, itemMap is null, playerId:{}", playerId);
            return;
        }
        itemMap.put(item.getId(), item);
        saveDb(item);
    }

    public void sendNewItems(long playerId, List<Item> items) {
        if (items.isEmpty()) {
            return;
        }

        List<ItemVo> list = new ArrayList<>();
        for (Item item : items) {
            ItemVo itemVo = packItemVo(item);
            list.add(itemVo);
        }

        BagNewItemS2C msg = new BagNewItemS2C();
        msg.setItems(list);
        IdSession session = GameContext.getSessionManager().getSessionBy(playerId);
        if(session != null) {
            session.send(msg);
        }
    }

    public static void sendItemUpdate(IdSession session, List<Item> items) {
        List<ItemVo> list = new ArrayList<>();
        for (Item item : items) {
            ItemVo itemVo = packItemVo(item);
            list.add(itemVo);
        }
        BagItemUpdateS2C bagItemUpdateS2C = new BagItemUpdateS2C();
        bagItemUpdateS2C.setItem(list);
        session.send(bagItemUpdateS2C);
    }

    public Item getItemById(long playerId, long id) {
        Map<Long, Item> itemMap = bagMap.get(playerId);
        if (itemMap == null) {
            return null;
        }

        return itemMap.get(id);
    }

    public List<Item> getAllItems(long playerId) {
        Map<Long, Item> itemMap = bagMap.get(playerId);
        if (itemMap == null) {
            return null;
        }
        return List.copyOf(itemMap.values());
    }

    public List<Item> getItemByItemId(long playerId, long itemId) {
        Map<Long, Item> itemMap = bagMap.get(playerId);
        List<Item> items = new ArrayList<>();
        if (itemMap == null) {
            return items;
        }

        for(Item item : itemMap.values()) {
            if (item.getItemId() == itemId) {
                items.add(item);
            }
        }
        return items;
    }

    public void removeItemByItemUid(long playerId, long itemUid) {
        Map<Long, Item> itemMap = bagMap.get(playerId);
        if (itemMap == null) {
            return;
        }
        itemMap.remove(itemUid);
    }


    // 发送背包信息
    public void sendBagInfo(IdSession session) {
        long playerId = GameContext.getSessionManager().getPlayerIdBy(session);
        checkLoadPlayerItem(playerId);
        List<Item> itemList = getAllItems(playerId);
        List<ItemVo> list = new ArrayList<>();
        for (Item item : itemList) {
            ItemVo itemVo = packItemVo(item);
            list.add(itemVo);
        }
        BagInfoS2C bagInfoS2C = new BagInfoS2C();
        bagInfoS2C.setItems(list);
        session.send(bagInfoS2C);
    }

    public static ItemVo packItemVo(Item item) {
        ItemVo itemVo = new ItemVo();
        itemVo.setId(item.getId());
        itemVo.setItemId(item.getItemId());
        itemVo.setNum(item.getNum());
        itemVo.setColor(item.getColor().getValue());
        itemVo.setInUse(item.getInUse());
        return itemVo;
    }

}
