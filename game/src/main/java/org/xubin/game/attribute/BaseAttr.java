package org.xubin.game.attribute;

import lombok.extern.slf4j.Slf4j;

// 属性
@Slf4j
public class BaseAttr {
    private long hpMax;
    private long atk;
    private long def;
    private long arp;

    @Override
    public String toString() {
        return "BaseAttr{" +
                "hpMax=" + hpMax +
                ", atk=" + atk +
                ", def=" + def +
                ", arp=" + arp +
                '}';
    }
}
