package org.xubin.game.faction.message.c2s;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Getter;
import lombok.Setter;
import org.xubin.game.faction.cmd.FactionCmd;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

@Getter
@Setter
@ProtobufClass
@MessageMeta(cmd = FactionCmd.FACTION_KICK_C2S)
public class FactionKickC2S implements Message {
    private long memberId;
}
