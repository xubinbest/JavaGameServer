package org.xubin.game.listener;

import lombok.Getter;

public abstract class BaseEvent {
    @Getter
    private long createTime;
    @Getter
    private final EventType eventType;

    protected BaseEvent(EventType eventType) {
        this.createTime = System.currentTimeMillis();
        this.eventType = eventType;
    }

    public boolean isSynchronized() {
        return true;
    }
}
