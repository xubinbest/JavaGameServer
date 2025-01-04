package org.xubin.game.attribute;

import lombok.Getter;

@Getter
public class AttrChange {
    // 变化的属性名
    private String name;
    // 变化值
    private long change;
    // 最新值(用于给客户端同步)
    private long value;

    public AttrChange(String name, long change, long value) {
        this.name = name;
        this.change = change;
        this.value = value;
    }

    @Override
    public String toString() {
        return "AttrChange{" +
                "name='" + name +
                ", change=" + change +
                ", value=" + value +
                '}';
    }
}
