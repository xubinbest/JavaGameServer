package org.xubin.game.login.message;

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
public class LoginC2S implements Message {
    @Protobuf
    public long accountId;
}
