package org.xubin.game.listener;

public class BasePlayerEvent extends BaseEvent {
    private final long playerId;

    public BasePlayerEvent(EventType eventType, long playerId) {
        super(eventType);
        this.playerId = playerId;
    }

    public long getPlayerId() {
        return this.playerId;
    }
}
