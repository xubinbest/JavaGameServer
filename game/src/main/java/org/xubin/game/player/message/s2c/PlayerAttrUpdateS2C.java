package org.xubin.game.player.message.s2c;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Getter;
import lombok.Setter;
import org.xubin.game.player.message.vo.AttrVo;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

import java.util.List;

@Getter
@Setter
@ProtobufClass
@MessageMeta(cmd = 3)
public class PlayerAttrUpdateS2C implements Message {
    List<AttrVo> attrs;
}
