package org.xubin.game.database.game.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.xubin.game.database.game.user.entity.Player;

import java.util.List;

/**
 * 玩家DAO
 * @author xubin
 */
public interface PlayerDao extends JpaRepository<Player, Long> {
    @Query("SELECT new Player(id,accountId,name,exp,level,money) FROM Player WHERE accountId = ?1")
    List<Player> listByAccountId(long accountId);

}
