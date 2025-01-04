package org.xubin.game.database.game.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.xubin.game.database.game.user.entity.PlayerEnt;
import org.xubin.game.player.module.PlayerProfile;

import java.util.List;

public interface PlayerDao extends JpaRepository<PlayerEnt, Long> {
    @Query("SELECT new org.xubin.game.player.module.PlayerProfile(id,accountId,name,exp,level,money) FROM PlayerEnt")
    List<PlayerProfile> queryAllPlayers();

    @Query("SELECT new org.xubin.game.player.module.PlayerProfile(id,accountId,name,exp,level,money) FROM PlayerEnt WHERE accountId = ?1")
    List<PlayerProfile> getByAccountId(long accountId);

}
