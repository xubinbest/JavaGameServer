package org.xubin.game.player.events;

import org.xubin.game.listener.BasePlayerEvent;
import org.xubin.game.listener.EventType;

public class PlayerLevelUpEvent extends BasePlayerEvent {
    public PlayerLevelUpEvent(EventType eventType, long playerId) {
        super(eventType, playerId);
    }
}
