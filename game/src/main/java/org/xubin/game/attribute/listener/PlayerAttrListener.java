package org.xubin.game.attribute.listener;

import lombok.extern.slf4j.Slf4j;
import org.xubin.game.attribute.AttrFrom;
import org.xubin.game.base.GameContext;
import org.xubin.game.equip.events.EquipChangeEvent;
import org.xubin.game.listener.EventType;
import org.xubin.game.listener.annotation.EventHandler;
import org.xubin.game.listener.annotation.Listener;
import org.xubin.game.player.events.PlayerLevelUpEvent;

@Listener
@Slf4j
public class PlayerAttrListener {
    @EventHandler(value = EventType.EQUIP_CHANGE)
    public void onEquipChange(EquipChangeEvent event) {
        log.info("Player {} equip change", event.getPlayerId());
        GameContext.getPlayerAttrService().reCalcPlayerAttr(event.getPlayerId(), AttrFrom.EQUIP);
    }

    @EventHandler(value = EventType.PLAYER_LEVEL_UP)
    public void onPlayerLevelUp(PlayerLevelUpEvent event) {
        log.info("Player {} level up", event.getPlayerId());
        GameContext.getPlayerAttrService().reCalcPlayerAttr(event.getPlayerId(), AttrFrom.LEVEL);
    }
}
