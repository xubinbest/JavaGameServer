package org.xubin.login.client;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xubin.login.node.GameNode;
import org.xubin.login.node.GameNodeService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlayerService {
    @Autowired
    private GameNodeService gameNodeService;
    private static Map<Long, Integer> lastLoginNodeMap = new ConcurrentHashMap<>();

    // 客户端请求登录
    public String reqLogin(long accountId) {
        int lastLoginServerId = lastLoginNodeMap.getOrDefault(accountId, 0);
        int recommendServerId = gameNodeService.getRecommendServerId();
        List<GameNode> gameList = gameNodeService.getAllNodes();
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setLastLoginServerId(lastLoginServerId);
        loginInfo.setRecommendServerId(recommendServerId);
        loginInfo.setGameList(gameList);

        return JSON.toJSONString(loginInfo);
    }

    // 玩家在游戏服成功登录，记录玩家最后登录的服务器
    public void updatePlayerLastLoginServer(long accountId, int serverId) {
        lastLoginNodeMap.put(accountId, serverId);
    }

    @Data
    public class LoginInfo {
        private int lastLoginServerId;
        private int recommendServerId;
        List<GameNode> gameList;
    }
}
