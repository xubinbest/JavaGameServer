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
import org.xubin.game.database.game.faction.dao.FactionDao;
import org.xubin.game.redis.CacheIndex;
import org.xubin.game.redis.CacheIndexType;

/**
 * 公会
 */
@Entity
@Table(name = "faction")
@Proxy(lazy = false)
@Getter
@Setter
public class Faction implements BaseEntity<Long> {
    @Id
    @CacheIndex(type = CacheIndexType.SINGLE)
    private long id;
    @Column
    private String name;
    @Column
    private int level;
    @Column
    private long leaderId;
    @Column
    private long createTime;
    @Column
    private String notice;
    @Column
    private int memberNum;

    public Faction() {
    }

    public Faction(long id, String name, int level, long leaderId, long createTime, String notice, int memberNum) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.leaderId = leaderId;
        this.createTime = createTime;
        this.notice = notice;
        this.memberNum = memberNum;
    }

    public synchronized void increaseMemberNum() {
        memberNum++;
    }

    public synchronized void decreaseMemberNum() {
        memberNum--;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public CrudRepository<Faction, Long> getCrudRepository() {
        return GameContext.getBean(FactionDao.class);
    }

    @Override
    public String toString() {
        return "Faction{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", leaderId=" + leaderId +
                ", createTime=" + createTime +
                ", notice='" + notice + '\'' +
                ", memberNum=" + memberNum +
                '}';
    }
}
