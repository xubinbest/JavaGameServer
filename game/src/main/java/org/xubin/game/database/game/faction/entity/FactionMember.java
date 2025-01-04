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
import org.xubin.game.database.game.faction.dao.FactionMemberDao;
import org.xubin.game.faction.FactionPosition;

/**
 * 公会成员
 */
@Entity
@Table(name = "faction_member")
@Proxy(lazy = false)
@Getter
@Setter
public class FactionMember implements BaseEntity<Long> {

    @Id
    private long id;
    @Column
    private long factionId;
    @Column
    private long time;
    @Column
    private int position;

    private boolean isDelete = false;

    public FactionMember() {
    }

    public FactionMember(long id, long factionId) {
        this.id = id;
        this.factionId = factionId;
        this.time = java.lang.System.currentTimeMillis() / 1000;
    }

    public FactionMember(long id, long factionId, long time) {
        this.id = id;
        this.factionId = factionId;
        this.time = time;
    }

    public boolean isManager() {
        return position == FactionPosition.LEADER.getValue() ||
                position == FactionPosition.DEPUTY_LEADER.getValue();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public CrudRepository<FactionMember, Long> getCrudRepository() {
        return GameContext.getBean(FactionMemberDao.class);
    }

    public void setDelete() {
        isDelete = true;
    }

    @Override
    public boolean isDelete() {
        return isDelete;
    }
}
