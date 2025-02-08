package org.xubin.game.database.game.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xubin.game.database.game.user.entity.Account;

public interface AccountDao extends JpaRepository<Account, Long> {
}
