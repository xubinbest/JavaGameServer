package org.xubin.game.faction.message.s2c;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Getter;
import lombok.Setter;
import org.xubin.game.database.game.faction.entity.Faction;
import org.xubin.game.faction.cmd.FactionCmd;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

@Getter
@Setter
@ProtobufClass
@MessageMeta(cmd = FactionCmd.FACTION_REMOVE_APPLY_S2C)
public class FactionRemoveApplyS2C implements Message {
    private long applyId;
}
