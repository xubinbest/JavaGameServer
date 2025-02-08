package org.xubin.game.chat.controller;

import lombok.extern.slf4j.Slf4j;
import org.xubin.game.listener.EventType;
import org.xubin.game.listener.annotation.EventHandler;
import org.xubin.game.listener.annotation.Listener;
import org.xubin.game.login.events.PlayerLoginEvent;

@Listener
@Slf4j
public class ChatListener {
    @EventHandler(value = EventType.LOGIN)
    public void onPlayerLogin(PlayerLoginEvent event) {
//        long playerId = event.getPlayerId();
//        Player player = GameContext.getPlayerService().getPlayer(playerId);
//        log.info("Player {} login", player.getName());
    }
}
