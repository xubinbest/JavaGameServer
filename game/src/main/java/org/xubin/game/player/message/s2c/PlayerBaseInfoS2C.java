package org.xubin.game.player.message.s2c;

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
public class PlayerBaseInfoS2C implements Message {

    @Protobuf
    private long playerId;

    @Protobuf
    private String name;

    @Protobuf
    private int level;

    @Protobuf
    private long exp;

    @Protobuf
    private long money;
}
