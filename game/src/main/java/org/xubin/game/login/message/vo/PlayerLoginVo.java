package org.xubin.game.login.message.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerLoginVo {
    @Protobuf
    private long id;
    @Protobuf
    private String name;
    @Protobuf
    private int level;
    @Protobuf
    private long fight;
}
