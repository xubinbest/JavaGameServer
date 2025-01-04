package org.xubin.game.item;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Item {
    private long id;
    private long itemId;
    private long playerId;
    private int num;
    private ItemColor color;
    private ItemType type;
    private byte inUse;

    private boolean isStackFull;

    public Item(long id, long itemId, long playerId, int num, int color, int type, byte inUse) {
        this.id = id;
        this.itemId = itemId;
        this.playerId = playerId;
        this.num = num;
        this.color = ItemColor.valueOf(color);
        this.type = ItemType.valueOf(type);
        this.inUse = inUse;
    }

    public void setInUse() {
        this.inUse = 1;
    }

    public void setNotInUse() {
        this.inUse = 0;
    }

    public void setStackFull() {
        this.isStackFull = true;
    }

    public void setStackNotFull() {
        this.isStackFull = false;
    }

    public boolean isStackFull() {
        return this.isStackFull;
    }

    public void addNum(int num) {
        this.num += num;
    }

    public void reduceNum(int num) {
        this.num -= num;
    }


}
