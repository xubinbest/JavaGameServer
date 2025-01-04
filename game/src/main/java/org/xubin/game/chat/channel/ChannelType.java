package org.xubin.game.chat.channel;

public enum ChannelType {

    /**
     * 世界聊天
     */
    WORLD((short) 0),
    TEAM((short) 1),

    ;

    short type;

    ChannelType(short type) {
        this.type = type;
    }

    public short getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }
}
