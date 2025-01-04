package org.xubin.game.database.game.faction.entity;

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
import org.xubin.game.database.game.faction.dao.FactionApplyDao;

@Entity
@Table(name = "faction_apply")
@Proxy(lazy = false)
@Getter
@Setter
public class FactionApply implements BaseEntity<Long> {
    @Id
    private long id;
    @Column
    private long playerId;
    @Column
    private long factionId;
    @Column
    private long applyTime;

    private boolean isDelete = false;

    public FactionApply() {
    }

    public FactionApply(long id, long playerId, long factionId) {
        this.id = id;
        this.playerId = playerId;
        this.factionId = factionId;
        this.applyTime = System.currentTimeMillis() / 1000;
    }

    public FactionApply(long id, long playerId, long factionId, long applyTime) {
        this.id = id;
        this.playerId = playerId;
        this.factionId = factionId;
        this.applyTime = applyTime;
    }

    @Override
    public CrudRepository<FactionApply, Long> getCrudRepository() {
        return GameContext.getBean(FactionApplyDao.class);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isDelete() {
        return isDelete;
    }
}
