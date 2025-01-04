package org.xubin.game.database.log.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xubin.game.database.log.entity.NewPlayerEnt;

public interface NewPlayerDao extends JpaRepository<NewPlayerEnt, Long> {
}
