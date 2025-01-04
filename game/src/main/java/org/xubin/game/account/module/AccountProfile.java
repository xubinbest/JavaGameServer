package org.xubin.game.account.module;

import lombok.Getter;
import lombok.Setter;
import org.xubin.game.player.module.PlayerProfile;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class AccountProfile {
    // 账号ID
    private long accountId;
    // 所有角色
    private List<PlayerProfile> players = new ArrayList<>();
    // 最近登录角色
    private PlayerProfile recentPlayer;

    public void addPlayerProfile(PlayerProfile player) {
        this.players.add(player);
    }

    @Override
    public String toString() {
        return "AccountProfile [accountId=" + accountId + ", players=" + players + ", recentPlayer=" + recentPlayer + "]";
    }
}
