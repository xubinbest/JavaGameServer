package org.xubin.game.chat.channel;

import org.xubin.game.chat.module.ChatMessage;

public interface IChatChannel {
    void send(ChatMessage message);
}
