package org.xubin.login.net;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xubin.login.ConfigScanPaths;
import org.xubin.login.base.GameContext;
import xbgame.codec.protobuf.ProtobufMessageCodec;
import xbgame.socket.netty.support.server.TcpSocketServerBuilder;
import xbgame.socket.share.HostAndPort;
import xbgame.socket.share.ServerNode;
import xbgame.socket.share.message.MessageFactory;

@Configuration
public class SocketServerAutoConfiguration {
    @Bean
    public ServerNode serverNode() {
        TcpSocketServerBuilder builder = new TcpSocketServerBuilder();
        builder.bindingPort(HostAndPort.valueOf(GameContext.getServerConfig().getServerPort()))
                .setMessageFactory(GameContext.getMessageFactory())
                .setMessageCodec(new ProtobufMessageCodec())
                .setSocketIoDispatcher(new MessageIoDispatcher());

        return builder.build();
    }

    @Bean
    public MessageFactory createMessageFactory() {
        return new LoginMessageFactory(ConfigScanPaths.MESSAGE_BASE_PATH);
    }
}
