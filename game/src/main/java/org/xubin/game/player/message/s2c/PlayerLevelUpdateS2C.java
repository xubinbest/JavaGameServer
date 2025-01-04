package org.xubin.game.player.message.s2c;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Getter;
import lombok.Setter;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

@Getter
@Setter
@ProtobufClass
@MessageMeta(cmd = 4)
public class PlayerLevelUpdateS2C implements Message {
    private int level;
    private long exp;
}
