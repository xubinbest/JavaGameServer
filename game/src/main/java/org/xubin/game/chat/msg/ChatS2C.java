package org.xubin.game.chat.msg;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Getter;
import lombok.Setter;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

@Setter
@Getter
@ProtobufClass
@MessageMeta(cmd = 2)
public class ChatS2C implements Message {
    @Protobuf
    private int channelType;
    @Protobuf
    private long senderId;
    @Protobuf
    private String senderName;
    @Protobuf
    private long receiverId;
    @Protobuf
    private String text;
    @Protobuf
    private long timestamp;
}
