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
    // 是否为推荐服
    private int recommend;
    // 后台状态. 0:关闭 1:开启 2:维护 3:繁忙 4:火爆
    private int adminStatus;
    // 服务器状态. 0:关闭 1:开启
    private int gameStatus;

    @JSONField(serialize = false)
    private IdSession session;

    @Override
    public String toString() {
        return "GameNode{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", name='" + name + '\'' +
                ", recommend=" + recommend +
                ", adminStatus=" + adminStatus +
                ", gameStatus=" + gameStatus +
                '}';
    }
}
