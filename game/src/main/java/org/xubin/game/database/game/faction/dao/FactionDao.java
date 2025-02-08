package org.xubin.game.database.game.faction.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.xubin.game.database.game.faction.entity.Faction;

import java.util.List;

public interface FactionDao extends JpaRepository<Faction, Long> {

    @Query("Select id from Faction")
    List<Long> getFactionIds();
}
