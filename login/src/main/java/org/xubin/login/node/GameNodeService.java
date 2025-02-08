package org.xubin.login.node;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.xubin.login.server.message.g2l.GameNodeRegisterG2L;
import org.xubin.login.server.message.l2g.GameNodeRegisterL2G;
import xbgame.socket.share.IdSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 游戏节点管理服务
 * @author xubin
 */
@Service
public class GameNodeService {
    private static final Map<Integer, GameNode> gameNodeMap = new ConcurrentHashMap<>();
    private static int recommendServerId = 1;

//    @PostConstruct
//    public void init() {
//        GameNode node = new GameNode();
//        node.setId(1);
//        node.setIp("127.0.0.1");
//        node.setPort(9527);
//        node.setName("服务器1");
//        node.setRecommend(1);
//        gameNodeMap.put(node.getId(), node);
//    }

    // 获取推荐服务器
    public int getRecommendServerId() {
        if (recommendServerId == 0) {
            recommendServerId = gameNodeMap.keySet().iterator().next();
        }
        return recommendServerId;
    }

    // 游戏服注册
    public void gameNodeRegister(IdSession session, GameNodeRegisterG2L msg) {
        int id = msg.getId();
        GameNode node = gameNodeMap.get(id);
        if (node == null) {
            node = new GameNode();
            node.setId(msg.getId());
            node.setIp(msg.getIp());
            node.setPort(msg.getPort());
            node.setSession(session);
            node.setName(gameNodeDefaultName(id));
            addNode(node);
        } else {
            node.setIp(msg.getIp());
            node.setPort(msg.getPort());
            node.setSession(session);
            updateNode(node);
        }

        System.out.println("节点注册成功: " + node);
        sendLoginL2G(session);
    }

    public void updateServerInfo(GameNode gameNode) throws RuntimeException {
        GameNode node = gameNodeMap.get(gameNode.getId());
        if (node == null) {
            String error = String.format("节点[%d]不存在", gameNode.getId());
            throw new RuntimeException(error);
        }
        node.setName(gameNode.getName());
        node.setRecommend(gameNode.getRecommend());
        updateNode(node);
        if (gameNode.getRecommend() == 1) {
            recommendServerId = gameNode.getId();
        }
    }

    private void sendLoginL2G(IdSession session) {
        GameNodeRegisterL2G msg = new GameNodeRegisterL2G();
        session.send(msg);
    }

    private String gameNodeDefaultName(int id) {
        return "服务器" + id;
    }

    private void addNode(GameNode node) {
        gameNodeMap.put(node.getId(), node);
    }

    private void updateNode(GameNode node) {
        gameNodeMap.put(node.getId(), node);
    }

    private GameNode getNode(int id) {
        return gameNodeMap.get(id);
    }

    public List<GameNode> getAllNodes() {
        return new ArrayList<>(gameNodeMap.values());
    }
}
