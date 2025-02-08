package org.xubin.game.player.controller;

import lombok.extern.slf4j.Slf4j;
import org.xubin.game.base.GameContext;
import org.xubin.game.database.game.user.entity.Player;
import org.xubin.game.listener.EventType;
import org.xubin.game.listener.annotation.EventHandler;
import org.xubin.game.listener.annotation.Listener;
import org.xubin.game.login.events.PlayerLoginEvent;
import org.xubin.game.player.message.s2c.PlayerBaseInfoS2C;

@Listener
@Slf4j
public class PlayerListener {
    @EventHandler(value = EventType.LOGIN)
    public void onPlayerLogin(PlayerLoginEvent event) {
        sendPlayerBaseInfo(event.getPlayerId());

    }

    private void sendPlayerBaseInfo(long playerId) {
        Player player = GameContext.getPlayerService().getPlayer(playerId);
        if (player == null) {
            log.error("player not found:{}", playerId);
            return;
        }

        PlayerBaseInfoS2C msg = new PlayerBaseInfoS2C();
        msg.setPlayerId(player.getId());
        msg.setName(player.getName());
        msg.setLevel(player.getLevel());
        msg.setExp(player.getExp());
        msg.setMoney(player.getMoney());
        GameContext.getSessionManager().getSessionBy(playerId).send(msg);
    }

}
