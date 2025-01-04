package org.xubin.game.player.message.c2s;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Getter;
import lombok.Setter;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

@ProtobufClass
@Getter
@Setter
@MessageMeta(cmd = 0)
public class PlayerTestC2S implements Message {
    @Protobuf
    private int test;
}
