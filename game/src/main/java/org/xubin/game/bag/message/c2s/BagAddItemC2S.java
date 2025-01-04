package org.xubin.game.bag.message.c2s;

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
    private int itemId;
    private int num;
}
