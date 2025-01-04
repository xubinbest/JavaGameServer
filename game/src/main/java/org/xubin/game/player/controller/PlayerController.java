package org.xubin.game.player.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.xubin.game.base.GameContext;
import org.xubin.game.player.message.c2s.PlayerAddExpC2S;
import org.xubin.game.player.message.c2s.PlayerTestC2S;
import org.xubin.game.player.module.PlayerLevel;
import xbgame.socket.share.IdSession;
import xbgame.socket.share.annotation.MessageRoute;
import org.xubin.game.commons.Module;
import xbgame.socket.share.annotation.RequestHandler;

@Controller
@Slf4j
@MessageRoute(module = Module.PLAYER)
public class PlayerController {

    @RequestHandler
    public void test(IdSession session, PlayerTestC2S msg) {
        log.info("test:{}", msg.getTest());
    }

    @RequestHandler
    public void addExp(IdSession session, PlayerAddExpC2S msg) {
        long playerId = GameContext.getSessionManager().getPlayerIdBy(session);
        PlayerLevel.addExp(playerId, msg.getExp());
    }

}