package org.xubin.login.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.xubin.login.client.PlayerService;
import org.xubin.login.commons.Modules;
import org.xubin.login.node.GameNodeService;
import org.xubin.login.server.message.g2l.GameNodeRegisterG2L;
import org.xubin.login.server.message.g2l.UpdatePlayerLastLoginServerG2L;
import xbgame.socket.share.IdSession;
import xbgame.socket.share.annotation.MessageRoute;
import xbgame.socket.share.annotation.RequestHandler;

@Controller
@MessageRoute(module = Modules.LOGIN_SERVER)
public class ServerController {
    @Autowired
    private GameNodeService gameNodeService;

    @Autowired
    private PlayerService playerService;

    @RequestHandler
    public void gameNodeRegister(IdSession session, GameNodeRegisterG2L message) {
        gameNodeService.gameNodeRegister(session, message);
    }

    @RequestHandler
    public void updatePlayerLastLoginServer(IdSession session, UpdatePlayerLastLoginServerG2L msg) {
        playerService.updatePlayerLastLoginServer(msg.getAccountId(), msg.getServerId());
    }
}
