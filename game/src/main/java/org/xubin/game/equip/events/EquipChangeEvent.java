package org.xubin.game.equip.events;

import org.xubin.game.listener.BasePlayerEvent;
import org.xubin.game.listener.EventType;

public class EquipChangeEvent extends BasePlayerEvent {
    public EquipChangeEvent(EventType eventType, long playerId) {
        super(eventType, playerId);
    }
}
