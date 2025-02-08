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
import org.xubin.game.database.game.item.dao.EquipDao;
import org.xubin.game.redis.CacheIndex;
import org.xubin.game.redis.CacheIndexType;

/**
 * 装备实体类
 * @author xubin
 */
@Entity
@Table(name = "equip")
@Proxy(lazy = false)
@Setter
@Getter
public class Equip implements BaseEntity<Long> {
    @Id
    private Long id;
    @Column
    @CacheIndex(type = CacheIndexType.MULTIPLE)
    private Long playerId;
    @Column
    private int slot;
    @Column
    private long itemUid;

    public Equip() {}

    public Equip(Long id, Long playerId, int slot, long itemUid) {
        this.id = id;
        this.playerId = playerId;
        this.slot = slot;
        this.itemUid = itemUid;
    }

    @Override
    public CrudRepository<Equip, Long> getCrudRepository() {
        return GameContext.getBean(EquipDao.class);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Equip{" +
                "id=" + id +
                ", playerId=" + playerId +
                ", slot=" + slot +
                ", itemUid=" + itemUid +
                '}';
    }
}
