package org.xubin.game.chat.channel;

import lombok.extern.slf4j.Slf4j;
import org.xubin.game.base.GameContext;
import org.xubin.game.chat.module.ChatMessage;
import org.xubin.game.chat.msg.ChatS2C;
import xbgame.socket.share.IdSession;

import java.util.Set;

@Slf4j
public class WorldChatChannel implements IChatChannel {
    @Override
    public void send(ChatMessage message) {
        ChatS2C chatS2C = new ChatS2C();
        chatS2C.setChannelType(message.getChannelType());
        chatS2C.setSenderId(message.getSenderId());
        chatS2C.setText(message.getText());
        chatS2C.setTimestamp(message.getTimestamp());
        chatS2C.setSenderName(message.getSenderName());

        Set<Long> onPlayerIds = GameContext.getPlayerService().getOnlinePlayers();
        for (Long playerId : onPlayerIds) {
            IdSession session = GameContext.getSessionManager().getSessionBy(playerId);
            if (session != null) {
                session.send(chatS2C);
            }
        }


    }
}
