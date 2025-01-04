package org.xubin.game.config;

public enum ServerType {
    GATE(0, "网关服"),
    GAME(1, "游戏服"),
    FIGHT(2, "战斗服"),
    CENTRE(10, "中心服");

    public int type;
    public String name;

    ServerType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static ServerType of(int type) {
        for (ServerType value : ServerType.values()) {
            if (value.type == type) {
                return value;
            }
        }
        return null;
    }
}
