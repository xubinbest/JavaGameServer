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

@Entity
@Table(name = "item")
@Proxy(lazy = false)
@Getter
@Setter
public class ItemEnt implements BaseEntity<Long> {
    @Id
    @Column
    private long id;

    @Column
    private long itemId;
    @Column
    private long playerId;
    @Column
    private int num;
    @Column
    private int color;
    @Column
    private int type;
    @Column
    private byte inUse;

    @Override
    public CrudRepository<ItemEnt, Long> getCrudRepository() {
        return GameContext.getBean(ItemDao.class);
    }

    @Override
    public Long getId() {
        return id;
    }

    public ItemEnt() {

    }
}
