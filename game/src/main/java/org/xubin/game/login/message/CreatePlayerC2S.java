package org.xubin.game.login.message;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Getter;
import lombok.Setter;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

@Setter
@Getter
@ProtobufClass
@MessageMeta(cmd = 3)
public class CreatePlayerC2S implements Message {
    @Protobuf
    private String name;
}
