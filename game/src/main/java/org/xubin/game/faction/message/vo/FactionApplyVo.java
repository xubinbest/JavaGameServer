package org.xubin.game.faction.message.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FactionApplyVo {
    @Protobuf
    private long playerId;
    @Protobuf
    private String playerName;
    @Protobuf
    private int level;
}
