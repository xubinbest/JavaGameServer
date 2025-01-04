package org.xubin.game.equip.message.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EquipInfoVo {
    @Protobuf
    private int slot;
    @Protobuf
    private int itemId;
}
