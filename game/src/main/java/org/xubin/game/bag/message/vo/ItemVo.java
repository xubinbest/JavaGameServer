package org.xubin.game.bag.message.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemVo {
    @Protobuf
    private long id;
    @Protobuf
    private long itemId;
    @Protobuf
    private int num;
    @Protobuf
    private int color;
    @Protobuf
    private int inUse;
}
