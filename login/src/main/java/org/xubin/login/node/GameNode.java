package org.xubin.login.node;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import xbgame.socket.share.IdSession;

@Data
public class GameNode {
    private int id;
    private String ip;
    private int port;
    private String name;
    private int recommend;

    @JSONField(serialize = false)
    private IdSession session;

    @Override
    public String toString() {
        return "GameNode{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", name='" + name + '\'' +
                ", session=" + session +
                '}';
    }
}
