package org.xubin.game.faction;

public enum FactionPosition {
    // 帮主
    LEADER(1),
    // 副帮主
    DEPUTY_LEADER(2),
    // 精英
    ELITE(3),
    // 成员
    MEMBER(4),
    // 学徒
    TRAINEE(5);

    private int value;

    FactionPosition(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static FactionPosition valueOf(int value) {
        for (FactionPosition position : FactionPosition.values()) {
            if (position.getValue() == value) {
                return position;
            }
        }
        return null;
    }
}
