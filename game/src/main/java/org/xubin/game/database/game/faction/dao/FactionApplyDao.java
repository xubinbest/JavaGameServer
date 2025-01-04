package org.xubin.game.database.game.faction.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xubin.game.database.game.faction.entity.FactionApply;

public interface FactionApplyDao extends JpaRepository<FactionApply, Long> {
}
