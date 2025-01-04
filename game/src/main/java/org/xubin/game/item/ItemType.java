package org.xubin.game.item;

import java.util.HashMap;
import java.util.Map;

public enum ItemType {
    // 无
    NONE(0),
    // 装备
    EQUIP(1),
    // 消耗品
    CONSUMABLE(2),
    // 材料
    MATERIAL(3),
    ;

    private final int value;
    private static final Map<Integer, ItemType> map = new HashMap<>();

    static {
        for (ItemType type : values()) {
            map.put(type.value, type);
        }
    }

    ItemType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ItemType valueOf(int value) {
        return map.get(value);
    }
}
