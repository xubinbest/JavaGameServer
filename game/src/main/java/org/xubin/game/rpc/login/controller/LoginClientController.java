package org.xubin.game.rpc.login.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.xubin.game.commons.Modules;
import org.xubin.game.rpc.login.LoginClient;
import org.xubin.game.rpc.login.message.l2g.GameNodeRegisterL2G;
import xbgame.socket.share.IdSession;
import xbgame.socket.share.annotation.MessageRoute;
import xbgame.socket.share.annotation.RequestHandler;

@Controller
@MessageRoute(module = Modules.LOGIN_SERVER)
public class LoginClientController {
    @Autowired
    private LoginClient loginClient;

    @RequestHandler
    public void gameNodeRegisterL2G(IdSession session, GameNodeRegisterL2G msg) {
        loginClient.loginResult(msg);
    }
}
