package org.xubin.game.faction.message.s2c;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Getter;
import lombok.Setter;
import org.xubin.game.faction.cmd.FactionCmd;
import org.xubin.game.faction.message.vo.FactionMemberVo;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

import java.util.List;

@Getter
@Setter
@ProtobufClass
@MessageMeta(cmd = FactionCmd.FACTION_MEMBER_LIST_S2C)
public class FactionMemberListS2C implements Message {
    List<FactionMemberVo> members;
}
