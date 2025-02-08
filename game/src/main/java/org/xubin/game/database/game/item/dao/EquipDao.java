package org.xubin.game.database.game.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.xubin.game.database.game.item.entity.Equip;

import java.util.List;

public interface EquipDao extends JpaRepository<Equip, Long> {
    @Query("SELECT new Equip(id,playerId,slot,itemUid) FROM Equip WHERE playerId = ?1")
    List<Equip> listByPlayerId(long playerId);
}
