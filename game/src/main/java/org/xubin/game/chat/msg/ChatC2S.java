package org.xubin.game.chat.msg;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Getter;
import lombok.Setter;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

@Getter
@Setter
@ProtobufClass
@MessageMeta(cmd = 1)
public class ChatC2S implements Message {
    @Protobuf
    private int channelType;
    @Protobuf
    private long receiverId;
    @Protobuf
    private String text;
}
