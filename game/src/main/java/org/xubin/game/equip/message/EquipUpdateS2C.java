package org.xubin.game.equip.message;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Getter;
import lombok.Setter;
import org.xubin.game.equip.message.vo.EquipInfoVo;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

@Getter
@Setter
@ProtobufClass
@MessageMeta(cmd = 4)
public class EquipUpdateS2C implements Message {
    private EquipInfoVo equip;
}
