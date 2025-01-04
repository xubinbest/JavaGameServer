package org.xubin.game.database.game.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.xubin.game.database.game.item.entity.ItemEnt;
import org.xubin.game.item.Item;

import java.util.List;

public interface ItemDao extends JpaRepository<ItemEnt, Long> {
    @Query("SELECT new org.xubin.game.item.Item(id,itemId,playerId,num,color,type,inUse) FROM ItemEnt WHERE playerId = ?1")
    List<Item> getItemListByPlayerId(long playerId);
}
