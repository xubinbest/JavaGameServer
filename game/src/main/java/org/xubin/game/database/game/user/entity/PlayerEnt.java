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

@Entity
@Table(name = "playerent")
@Proxy(lazy = false)
@Getter
@Setter
public class PlayerEnt implements BaseEntity<Long> {

    @Id
    @Column
    private long id;

    @Column
    private long accountId;

    @Column
    private String name;

    @Column
    private long exp;

    @Column
    private int level;

    @Column
    private long money;

    @Override
    public CrudRepository<PlayerEnt, Long> getCrudRepository() {
        return GameContext.getBean(PlayerDao.class);
    }

    @Override
    public Long getId() {
        return id;
    }

    public PlayerEnt() {

    }
}
