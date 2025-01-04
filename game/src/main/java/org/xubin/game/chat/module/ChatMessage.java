package org.xubin.game.chat.module;

import lombok.Getter;
import lombok.Setter;
import org.xubin.game.base.GameContext;
import org.xubin.game.chat.msg.ChatC2S;
import org.xubin.game.database.game.user.entity.PlayerEnt;

@Getter
@Setter
public class ChatMessage {
    private short channelType;
    private long senderId;
    private String senderName;
    private long receiverId;
    private String text;
    private long timestamp;

    public ChatMessage(long senderId, ChatC2S msg) {
        PlayerEnt player = GameContext.getPlayerService().getPlayer(senderId);
        this.channelType = (short)msg.getChannelType();
        this.senderId = senderId;
        this.senderName = player.getName();
        this.receiverId = msg.getReceiverId();
        this.text = msg.getText();
        this.timestamp = System.currentTimeMillis() / 1000;
    }
}
