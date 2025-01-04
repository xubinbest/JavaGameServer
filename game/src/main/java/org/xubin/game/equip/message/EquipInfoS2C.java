package org.xubin.game.equip.message;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Getter;
import lombok.Setter;
import org.xubin.game.equip.message.vo.EquipInfoVo;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

import java.util.List;

@Setter
@Getter
@ProtobufClass
@MessageMeta(cmd = 2)
public class EquipInfoS2C implements Message {
    List<EquipInfoVo> equips;
}
