package org.xubin.game.equip.message;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Getter;
import lombok.Setter;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

@Getter
@Setter
@ProtobufClass
@MessageMeta(cmd = 3)
public class EquipWearC2S implements Message {
    private long itemUid;
}
