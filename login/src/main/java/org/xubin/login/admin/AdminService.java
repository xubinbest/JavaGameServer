package org.xubin.login.admin;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xubin.login.node.GameNode;
import org.xubin.login.node.GameNodeService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 后台管理服务
 * @author xubin
 */
@Service
public class AdminService {
    @Autowired
    private GameNodeService gameNodeService;

    private static Map<Long, String> adminUserMap = new ConcurrentHashMap<>();


    @PostConstruct
    public void init() {
        adminUserMap.put(10001L, "123456");
    }

    public String login(long accountId, String password) {
        if (!adminUserMap.containsKey(accountId)) {
            return loginResult(1, "账号不存在");
        }
        if (!adminUserMap.get(accountId).equals(password)) {
            return loginResult(2, "密码错误");
        }

        return loginResult(200, "登录成功");
    }

    private String loginResult(int code, String msg) {
        ReqResult result = new ReqResult();
        result.setCode(code);
        result.setMsg(msg);
        return JSON.toJSONString(result);
    }

    public String updateServerInfo(GameNode gameNode) {
        try {
            gameNodeService.updateServerInfo(gameNode);
            return updateServerInfoResult(200, "success");
        } catch (RuntimeException e) {
            return updateServerInfoResult(1, e.getMessage());
        }
    }

    private String updateServerInfoResult(int code, String msg) {
        ReqResult result = new ReqResult();
        result.setCode(code);
        result.setMsg(msg);
        return JSON.toJSONString(result);
    }

    public String serverList() {
        List<GameNode> gameNodes = gameNodeService.getAllNodes();
        ServerListResult result = new ServerListResult();
        result.setGameList(gameNodes);
        return JSON.toJSONString(result);
    }

    @Data
    public static class ServerListResult {
        List<GameNode> gameList;
    }

    @Data
    public static class ReqResult {
        private int code;
        private String msg;
    }

}
