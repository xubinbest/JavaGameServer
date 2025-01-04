package org.xubin.game.player.module;

import lombok.Getter;
import lombok.Setter;

public class PlayerProfile {

    @Getter
    @Setter
    private long playerId;

    @Getter
    @Setter
    private long accountId;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private long exp;

    @Getter
    @Setter
    private int level;

    @Getter
    @Setter
    private long money;

    public PlayerProfile(long playerId, long accountId, String name, long exp, int level, long money) {
        super();
        this.playerId = playerId;
        this.accountId = accountId;
        this.name = name;
        this.exp = exp;
        this.level = level;
        this.money = money;
    }

    @Override
    public String toString() {
        return "PlayerBaseInfo [id=" + playerId + ", accountId=" + accountId + ", name=" + name + ", level=" + level + "]";
    }
}
