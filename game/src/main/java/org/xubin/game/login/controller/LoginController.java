package org.xubin.game.login.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.xubin.game.commons.Modules;
import org.xubin.game.login.message.CreatePlayerC2S;
import org.xubin.game.login.message.LoginC2S;
import org.xubin.game.login.message.SelectPlayerC2S;
import org.xubin.game.login.service.LoginService;
import org.xubin.game.player.service.PlayerService;
import xbgame.socket.share.IdSession;
import xbgame.socket.share.annotation.MessageRoute;
import xbgame.socket.share.annotation.RequestHandler;

@Controller
@Slf4j
@MessageRoute(module = Modules.LOGIN)
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private PlayerService playerService;

    @RequestHandler
    public void login(IdSession session, LoginC2S msg) {
        long accountId = msg.getAccountId();
        log.info("login accountId:{}", accountId);
        loginService.login(session, accountId);
    }

    @RequestHandler
    public void createPlayer(IdSession session, CreatePlayerC2S msg) {
        String name = msg.getName();
        playerService.createPlayer(session, name);
    }

    @RequestHandler
    public void selectPlayer(IdSession session, SelectPlayerC2S msg) {
        log.info("selectPlayer playerId:{}", msg.getPlayerId());
        long playerId = msg.getPlayerId();
        loginService.selectPlayer(session, playerId);
    }

}
