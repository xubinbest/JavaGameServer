package org.xubin.game.faction.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.xubin.game.base.SessionManager;
import org.xubin.game.commons.Modules;
import org.xubin.game.faction.message.c2s.*;
import org.xubin.game.faction.service.FactionService;
import xbgame.socket.share.IdSession;
import xbgame.socket.share.annotation.MessageRoute;
import xbgame.socket.share.annotation.RequestHandler;

@Controller
@MessageRoute(module = Modules.FACTION)
public class FactionController {

    @Autowired
    private FactionService factionService;
    @Autowired
    private SessionManager sessionManager;

    @RequestHandler
    public void factionInfoC2S(IdSession session, FactionInfoC2S msg) {
        long playerId = sessionManager.getPlayerIdBy(session);
        factionService.sendFactionInfo(playerId);
    }

    @RequestHandler
    public void factionMemberListC2S(IdSession session, FactionMemberListC2S msg) {
        long playerId = sessionManager.getPlayerIdBy(session);
        factionService.sendFactionMemberList(playerId);
    }

    @RequestHandler
    public void factionListS2C(IdSession session, FactionListC2S msg) {
        long playerId = sessionManager.getPlayerIdBy(session);
        factionService.sendFactionList(playerId);
    }

    @RequestHandler
    public void factionCreateC2S(IdSession session, FactionCreateC2S msg) {
        long playerId = sessionManager.getPlayerIdBy(session);
        factionService.createFaction(playerId, msg.getName());
    }

    @RequestHandler
    public void factionApplyListC2S(IdSession session, FactionApplyListC2S msg) {
        long playerId = sessionManager.getPlayerIdBy(session);
        factionService.sendFactionApplyList(playerId);
    }

    @RequestHandler
    public void factionApplyC2S(IdSession session, FactionApplyC2S msg) {
        long playerId = sessionManager.getPlayerIdBy(session);
        factionService.applyFaction(playerId, msg.getFactionId());
    }

    @RequestHandler
    public void factionDealApplyC2S(IdSession session, FactionDealApplyC2S msg) {
        long playerId = sessionManager.getPlayerIdBy(session);
        factionService.dealApply(playerId, msg.getPlayerId(), msg.getOpt());
    }

    @RequestHandler
    public void factionKickC2S(IdSession session, FactionKickC2S msg) {
        long playerId = sessionManager.getPlayerIdBy(session);
        factionService.kickMember(playerId, msg.getMemberId());
    }

    @RequestHandler
    public void factionLeaveC2S(IdSession session, FactionLeaveC2S msg) {
        long playerId = sessionManager.getPlayerIdBy(session);
        factionService.leaveFaction(playerId);
    }

}
