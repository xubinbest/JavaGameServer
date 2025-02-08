package org.xubin.game.bag.message.c2s;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Getter;
import lombok.Setter;
import org.xubin.game.bag.cmd.BagCmd;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

@Getter
@Setter
@ProtobufClass
@MessageMeta(cmd = BagCmd.BAG_ADD_ITEM_C2S)
public class BagAddItemC2S implements Message {
//    @Protobuf(fieldType = FieldType.INT32, order = 1)
    private int itemId;
//    @Protobuf(fieldType = FieldType.INT32, order = 2)
    private int num;
}
