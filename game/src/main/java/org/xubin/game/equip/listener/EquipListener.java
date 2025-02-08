package org.xubin.game.equip.listener;

import org.xubin.game.base.GameContext;
import org.xubin.game.listener.EventType;
import org.xubin.game.listener.annotation.EventHandler;
import org.xubin.game.listener.annotation.Listener;
import org.xubin.game.login.events.PlayerLoginEvent;

@Listener
public class EquipListener {
    @EventHandler(value = EventType.LOGIN)
    public void onPlayerLogin(PlayerLoginEvent event) {
        GameContext.getEquipService().loadPlayerEquip(event.getPlayerId());
    }
}
