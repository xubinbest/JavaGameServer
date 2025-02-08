package org.xubin.game.database.game.faction.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.xubin.game.database.game.faction.entity.FactionApply;

import java.util.List;

/**
 * 帮派申请表DAO
 * @author xubin
 * @date 2020/11/20 15:00
 */
public interface FactionApplyDao extends JpaRepository<FactionApply, Long> {
    @Query("SELECT new FactionApply(id,playerId,factionId,applyTime) FROM FactionApply WHERE factionId = ?1")
    List<FactionApply> listByFactionId(Long factionId);
}
