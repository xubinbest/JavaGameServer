package org.xubin.game.player.controller;

import lombok.extern.slf4j.Slf4j;
import org.xubin.game.base.GameContext;
import org.xubin.game.database.game.user.entity.PlayerEnt;
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
        PlayerEnt playerEnt = GameContext.getPlayerService().getPlayer(playerId);
        if (playerEnt == null) {
            log.error("player not found:{}", playerId);
            return;
        }

        PlayerBaseInfoS2C msg = new PlayerBaseInfoS2C();
        msg.setPlayerId(playerEnt.getId());
        msg.setName(playerEnt.getName());
        msg.setLevel(playerEnt.getLevel());
        msg.setExp(playerEnt.getExp());
        msg.setMoney(playerEnt.getMoney());
        GameContext.getSessionManager().getSessionBy(playerId).send(msg);
    }

}
