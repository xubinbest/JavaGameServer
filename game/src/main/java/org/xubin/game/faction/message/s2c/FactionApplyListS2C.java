package org.xubin.game.faction.message.s2c;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Getter;
import lombok.Setter;
import org.xubin.game.faction.cmd.FactionCmd;
import org.xubin.game.faction.message.vo.FactionApplyVo;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

import java.util.List;

@Getter
@Setter
@ProtobufClass
@MessageMeta(cmd = FactionCmd.FACTION_APPLY_LIST_S2C)
public class FactionApplyListS2C implements Message {
    private int code;
    private List<FactionApplyVo> applyList;
}
