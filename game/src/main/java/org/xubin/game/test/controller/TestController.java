package org.xubin.game.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.xubin.game.test.message.TestHeartC2S;
import org.xubin.game.test.message.TestHeartS2C;
import xbgame.socket.share.IdSession;
import xbgame.socket.share.annotation.MessageRoute;
import org.xubin.game.commons.Modules;
import xbgame.socket.share.annotation.RequestHandler;

@Controller
@MessageRoute(module = Modules.TEST)
@Slf4j
public class TestController {

    @RequestHandler
    public void testHeartC2S(IdSession session, TestHeartC2S request) {
        log.info("收到心跳消息");
        TestHeartS2C res = new TestHeartS2C();
        session.send(res);
    }
}
