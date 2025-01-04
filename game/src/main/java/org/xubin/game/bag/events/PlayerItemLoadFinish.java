package org.xubin.game.bag.events;

import org.xubin.game.listener.BasePlayerEvent;
import org.xubin.game.listener.EventType;

public class PlayerItemLoadFinish extends BasePlayerEvent {
    public PlayerItemLoadFinish(EventType eventType, long playerId) {
        super(eventType, playerId);
    }

    @Override
    public boolean isSynchronized() {
        return false;
    }
}
