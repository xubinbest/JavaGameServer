package org.xubin.game.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xubin.game.base.GameContext;
import org.xubin.game.chat.channel.ChannelType;
import org.xubin.game.chat.channel.IChatChannel;
import org.xubin.game.chat.channel.WorldChatChannel;
import org.xubin.game.chat.module.ChatMessage;
import org.xubin.game.chat.msg.ChatC2S;
import xbgame.commons.NumberUtil;
import xbgame.socket.share.IdSession;

@Service
@Slf4j
public class ChatService {
    private IChatChannel worldChatChannel = new WorldChatChannel();

    public void chatC2S(IdSession session, ChatC2S msg) {
        switch (ChannelType.values()[msg.getChannelType()]) {
            case WORLD:
                worldChat(session, msg);
                break;
            case TEAM:
                teamChat(session, msg);
                break;
            default:
                log.info("unknown channel type:{}", msg.getChannelType());
        }
    }

    public void worldChat(IdSession session, ChatC2S msg) {
        long senderId = GameContext.getSessionManager().getPlayerIdBy(session);
        ChatMessage chatMsg = new ChatMessage(senderId, msg);
        worldChatChannel.send(chatMsg);

    }

    public void teamChat(IdSession session, ChatC2S msg) {
        log.info("组队聊天");
        long senderId = GameContext.getSessionManager().getPlayerIdBy(session);
        ChatMessage chatMsg = new ChatMessage(senderId, msg);
    }
}
