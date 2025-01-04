package org.xubin.game.faction.message.s2c;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Getter;
import lombok.Setter;
import org.xubin.game.faction.cmd.FactionCmd;
import org.xubin.game.faction.message.vo.FactionApplyVo;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

@Setter
@Getter
@ProtobufClass
@MessageMeta(cmd = FactionCmd.FACTION_NEW_APPLY_S2C)
public class FactionNewApplyS2C implements Message {
    private FactionApplyVo apply;
}
