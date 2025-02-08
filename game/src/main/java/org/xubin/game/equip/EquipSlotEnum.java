package org.xubin.game.equip;

import java.util.HashMap;
import java.util.Map;

/**
 * 装备槽位枚举
 * @author xubin
 */

public enum EquipSlotEnum {
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
    private static final Map<Integer, EquipSlotEnum> map = new HashMap<>();

    static {
        for (EquipSlotEnum slot : values()) {
            map.put(slot.value, slot);
        }
    }

    EquipSlotEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static EquipSlotEnum valueOf(int value) {
        return map.get(value);
    }
}
