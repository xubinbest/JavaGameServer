package org.xubin.game.bag.controller;

import org.xubin.game.base.GameContext;
import org.xubin.game.listener.EventType;
import org.xubin.game.listener.annotation.EventHandler;
import org.xubin.game.listener.annotation.Listener;
import org.xubin.game.login.events.PlayerLoginEvent;

@Listener
public class BagListener {
    @EventHandler(value = EventType.LOGIN)
    public void onPlayerLogin(PlayerLoginEvent event) {
        GameContext.getBagService().checkLoadPlayerItem(event.getPlayerId());

    }
}
