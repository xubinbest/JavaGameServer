package org.xubin.game.equip;

import java.util.HashMap;
import java.util.Map;

public enum EquipSlot {
    // 武器
    WEAPON(1),
    // 项链
    NECKLACE(2),
    // 戒指
    RING(3),
    // 头盔
    HELMET(4),
    // 盔甲
    ARMOR(5),
    // 鞋子
    SHOES(6),
    ;

    private final int value;
    private static final Map<Integer, EquipSlot> map = new HashMap<>();

    static {
        for (EquipSlot slot : values()) {
            map.put(slot.value, slot);
        }
    }

    EquipSlot(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static EquipSlot valueOf(int value) {
        return map.get(value);
    }
}
