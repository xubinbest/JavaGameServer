package org.xubin.game.database.game.user.entity;

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
import org.xubin.game.database.game.user.dao.PlayerDao;
import org.xubin.game.redis.CacheIndex;
import org.xubin.game.redis.CacheIndexType;

/**
 * 玩家实体类
 * @author xubin
 */
@Entity
@Table(name = "player")
@Proxy(lazy = false)
@Getter
@Setter
public class Player implements BaseEntity<Long> {
    @Id
    private long id;
    @Column
    @CacheIndex(type = CacheIndexType.MULTIPLE)
    private long accountId;
    @Column
    private String name;
    @Column
    private long exp;
    @Column
    private int level;
    @Column
    private long money;

    public Player(long id, long accountId, String name, long exp, int level, long money) {
        this.id = id;
        this.accountId = accountId;
        this.name = name;
        this.exp = exp;
        this.level = level;
        this.money = money;
    }

    @Override
    public CrudRepository<Player, Long> getCrudRepository() {
        return GameContext.getBean(PlayerDao.class);
    }

    @Override
    public Long getId() {
        return id;
    }

    public Player() {

    }
}
