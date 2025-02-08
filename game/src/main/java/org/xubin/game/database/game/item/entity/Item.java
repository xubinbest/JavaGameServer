package org.xubin.game.database.game.item.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Proxy;
import org.springframework.data.repository.CrudRepository;
import org.xubin.game.base.GameContext;
import org.xubin.game.database.game.BaseEntity;
import org.xubin.game.database.game.item.dao.ItemDao;
import org.xubin.game.item.ItemColor;
import org.xubin.game.item.ItemType;
import org.xubin.game.redis.CacheIndex;
import org.xubin.game.redis.CacheIndexType;


/**
 * 物品
 * @author xubin
 * @date 2020/11/20 15:00
 */
@Entity
@Table(name="item")
@Proxy(lazy = false)
@Getter
@Setter
public class Item implements BaseEntity<Long> {
    @Id
    private long id;
    @Column
    private long itemId;
    @Column
    @CacheIndex(type = CacheIndexType.MULTIPLE)
    private long playerId;
    @Column
    private int num;
    @Column
    private int color;
    @Column
    private int type;
    @Column
    private byte inUse;

    private boolean isStackFull;

    public Item() {}

    public Item(long id, long itemId, long playerId, int num, int color, int type, byte inUse) {
        this.id = id;
        this.itemId = itemId;
        this.playerId = playerId;
        this.num = num;
        this.color = color;
        this.type = type;
        this.inUse = inUse;
    }

    public void setInUse() {
        this.inUse = 1;
    }

    public void setNotInUse() {
        this.inUse = 0;
    }

    public boolean isInUse() {
        return this.inUse == 1;
    }

    public void setStackFull() {
        this.isStackFull = true;
    }

    public void setStackNotFull() {
        this.isStackFull = false;
    }

    public boolean isStackFull() {
        return this.isStackFull;
    }

    public void addNum(int num) {
        this.num += num;
    }

    public void reduceNum(int num) {
        this.num -= num;
    }

    public boolean isEquip() {
        return this.type == ItemType.EQUIP.getValue();
    }

    @Override
    public CrudRepository<Item, Long> getCrudRepository() {
        return GameContext.getBean(ItemDao.class);
    }

    @Override
    public Long getId() {
        return id;
    }
}
