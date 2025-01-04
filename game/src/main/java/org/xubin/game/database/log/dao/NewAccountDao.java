package org.xubin.game.database.log.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xubin.game.database.log.entity.NewAccountEnt;

public interface NewAccountDao extends JpaRepository<NewAccountEnt, Long> {
}
