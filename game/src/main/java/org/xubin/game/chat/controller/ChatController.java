package org.xubin.game.chat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.xubin.game.chat.ChatService;
import org.xubin.game.chat.msg.ChatC2S;
import xbgame.socket.share.IdSession;
import xbgame.socket.share.annotation.MessageRoute;
import org.xubin.game.commons.Module;
import xbgame.socket.share.annotation.RequestHandler;

@Controller
@Slf4j
@MessageRoute(module = Module.CHAT)
public class ChatController {

    @Autowired
    private ChatService chatService;

    @RequestHandler
    public void chat(IdSession session, ChatC2S msg) {
        chatService.chatC2S(session, msg);
    }
}
