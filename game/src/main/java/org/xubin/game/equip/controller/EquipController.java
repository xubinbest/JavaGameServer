package org.xubin.game.equip.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.xubin.game.base.GameContext;
import org.xubin.game.commons.Modules;
import org.xubin.game.equip.message.EquipInfoC2S;
import org.xubin.game.equip.message.EquipWearC2S;
import xbgame.socket.share.IdSession;
import xbgame.socket.share.annotation.MessageRoute;
import xbgame.socket.share.annotation.RequestHandler;

@Controller
@Slf4j
@MessageRoute(module = Modules.EQUIP)
public class EquipController {
    @RequestHandler
    public void equipInfoC2S(IdSession session, EquipInfoC2S msg) {
        GameContext.getEquipService().sendEquipInfo(session);
    }

    @RequestHandler
    public void equipWearC2S(IdSession session, EquipWearC2S msg) {
        GameContext.getEquipService().wearEquip(session, msg.getItemUid());
    }
}
