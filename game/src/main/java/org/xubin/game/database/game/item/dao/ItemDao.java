package org.xubin.game.database.game.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.xubin.game.database.game.item.entity.Item;

import java.util.List;

/**
 * 物品DAO
 * @author xubin
 */
public interface ItemDao extends JpaRepository<Item, Long> {
    @Query("SELECT new Item(id,itemId,playerId,num,color,type,inUse) FROM Item WHERE playerId = ?1")
    List<Item> listByPlayerId(long playerId);
}
