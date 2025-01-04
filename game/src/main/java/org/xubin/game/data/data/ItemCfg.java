package org.xubin.game.data.data;

import lombok.Data;

@Data
public class ItemCfg {

    private int id;
    private Object name;
    private int type;
    private int subtype;
    private int color;
    private int stack;

}