package org.xubin.game.login.events;

import org.xubin.game.listener.BasePlayerEvent;
import org.xubin.game.listener.EventType;

public class PlayerLoginEvent extends BasePlayerEvent {
    public PlayerLoginEvent(EventType eventType, long playerId) {
        super(eventType, playerId);
    }

}
