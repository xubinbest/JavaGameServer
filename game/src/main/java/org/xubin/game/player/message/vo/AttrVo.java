package org.xubin.game.player.message.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AttrVo {
    @Protobuf
    private String name;
    @Protobuf
    private long value;

}
