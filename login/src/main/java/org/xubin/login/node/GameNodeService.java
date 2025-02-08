package org.xubin.login.node;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xubin.login.server.message.g2l.GameNodeRegisterG2L;
import org.xubin.login.server.message.l2g.GameNodeRegisterL2G;
import xbgame.commons.NumberUtil;
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
@Slf4j
public class GameNodeService {
    private static final Map<Integer, GameNode> gameNodeMap = new ConcurrentHashMap<>();
    private static int recommendServerId = 1;

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
        session.setAttribute(IdSession.ID, id);
        GameNode node = gameNodeMap.get(id);
        if (node == null) {
            node = new GameNode();
            node.setId(msg.getId());
            node.setIp(msg.getIp());
            node.setPort(msg.getPort());
            node.setSession(session);
            node.setName(gameNodeDefaultName(id));
            node.setGameStatus(1);
            addNode(node);
        } else {
            node.setIp(msg.getIp());
            node.setPort(msg.getPort());
            node.setGameStatus(1);
            node.setSession(session);
            updateNode(node);
        }

        log.info("服务器[{}]注册成功.节点[{}]", id, node);
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
        node.setAdminStatus(gameNode.getAdminStatus());
        log.info("更新服务器信息.节点[{}]", gameNode);
        updateNode(node);
        if (gameNode.getRecommend() == 1) {
            recommendServerId = gameNode.getId();
        }
    }

    // 服务关闭，修改状态
    public void onServerClose(IdSession session) {
        int serverId = NumberUtil.intValue(session.getId());
        log.info("服务器[{}]关闭", serverId);
        GameNode node = gameNodeMap.get(serverId);
        if (node == null) {
            return;
        }
        node.setGameStatus(0);
        updateNode(node);
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
