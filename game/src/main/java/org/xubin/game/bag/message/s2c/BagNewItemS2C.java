package org.xubin.game.bag.message.s2c;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Getter;
import lombok.Setter;
import org.xubin.game.bag.cmd.BagCmd;
import org.xubin.game.bag.message.vo.ItemVo;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

import java.util.List;

@Getter
@Setter
@ProtobufClass
@MessageMeta(cmd = BagCmd.BAG_NEW_ITEM_S2C)
public class BagNewItemS2C implements Message {
    private List<ItemVo> items;
}
