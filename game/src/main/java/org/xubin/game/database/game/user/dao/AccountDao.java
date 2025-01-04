package org.xubin.game.database.game.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xubin.game.database.game.user.entity.AccountEnt;

public interface AccountDao extends JpaRepository<AccountEnt, Long> {
}
