package org.xubin.game.item;

import java.util.HashMap;
import java.util.Map;

public enum ItemColor {
    // 白色
    WHITE(1),
    // 绿色
    GREEN(2),
    // 蓝色
    BLUE(3),
    // 紫色
    PURPLE(4),
    // 橙色
    ORANGE(5),
    // 红色
    RED(6),
    // 粉色
    PINK(7),
    ;

    private final int value;
    private final static Map<Integer, ItemColor> map = new HashMap<>();

    static {
        for (ItemColor color : values()) {
            map.put(color.value, color);
        }
    }

    ItemColor(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ItemColor valueOf(int value) {
        return map.get(value);
    }
}
