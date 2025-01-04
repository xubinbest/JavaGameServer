package org.xubin.game.bag.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.xubin.game.bag.message.c2s.BagAddItemC2S;
import org.xubin.game.bag.message.c2s.BagInfoC2S;
import org.xubin.game.base.GameContext;
import xbgame.socket.share.IdSession;
import xbgame.socket.share.annotation.MessageRoute;
import org.xubin.game.commons.Module;
import xbgame.socket.share.annotation.RequestHandler;

@Controller
@Slf4j
@MessageRoute(module = Module.BAG)
public class BagController {

    @RequestHandler
    public void bagInfoC2S(IdSession session, BagInfoC2S msg) {
        GameContext.getBagService().sendBagInfo(session);
    }

    @RequestHandler
    public void bagAddItemC2S(IdSession session, BagAddItemC2S msg) {
        long playerId = GameContext.getSessionManager().getPlayerIdBy(session);
        GameContext.getBagService().addItemByItemId(playerId, msg.getItemId(), msg.getNum());
    }
}
