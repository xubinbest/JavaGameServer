package org.xubin.game.faction.message.s2c;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Getter;
import lombok.Setter;
import org.xubin.game.faction.cmd.FactionCmd;
import org.xubin.game.faction.message.vo.FactionInfoVo;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

@Getter
@Setter
@ProtobufClass
@MessageMeta(cmd = FactionCmd.FACTION_INFO_S2C)
public class FactionInfoS2C implements Message {
    private FactionInfoVo factionInfo;
}
