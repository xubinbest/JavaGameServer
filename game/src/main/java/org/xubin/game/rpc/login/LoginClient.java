package org.xubin.game.rpc.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xubin.game.base.GameContext;
import org.xubin.game.config.ServerConfig;
import org.xubin.game.net.MessageIoDispatcher;
import org.xubin.game.rpc.login.message.g2l.GameNodeRegisterG2L;
import org.xubin.game.rpc.login.message.g2l.UpdatePlayerLastLoginServerG2L;
import org.xubin.game.rpc.login.message.l2g.GameNodeRegisterL2G;
import xbgame.codec.MessageCodec;
import xbgame.codec.protobuf.ProtobufMessageCodec;
import xbgame.socket.netty.support.client.TcpSocketClient;
import xbgame.socket.share.HostAndPort;
import xbgame.socket.share.IdSession;
import xbgame.socket.share.SocketIoDispatcher;
import xbgame.socket.share.message.MessageFactory;

/**
 * 连接登录服务器
 * @author xubin
 */
@Service
public class LoginClient {
    private final Logger logger = LoggerFactory.getLogger(LoginClient.class);
    private IdSession session;

    public void updatePlayerLastServerId(long accountId) {
        UpdatePlayerLastLoginServerG2L msg = new UpdatePlayerLastLoginServerG2L();
        msg.setAccountId(accountId);
        msg.setServerId(GameContext.getServerConfig().getServerId());
        send(msg);
    }

    public void start() {
        ServerConfig config = GameContext.getServerConfig();
        String ip = config.getLoginServerIp();
        int port = config.getLoginServerPort();
        HostAndPort hostPort = HostAndPort.valueOf(ip, port);
        SocketIoDispatcher msgDispatcher = new MessageIoDispatcher();
        MessageFactory messageFactory = GameContext.getMessageFactory();
        MessageCodec messageCodec = new ProtobufMessageCodec();
        TcpSocketClient client = new TcpSocketClient(msgDispatcher, messageFactory, messageCodec, hostPort);
        try {
            session = client.openSession();
            logger.info("连接登录服务器成功");
            sendLoginRegiste();

        } catch (Exception e) {
            logger.error("连接登录服务器失败", e);
        }
    }

    public void loginResult(GameNodeRegisterL2G msg) {

    }

    private void sendLoginRegiste() {
        GameNodeRegisterG2L msg = new GameNodeRegisterG2L();
        ServerConfig config = GameContext.getServerConfig();
        msg.setId(config.getServerId());
        msg.setIp(config.getServerIp());
        msg.setPort(config.getServerPort());
        send(msg);
    }

    // 给登录服务器发送消息
    public void send(Object msg) {
        session.send(msg);
    }

}
