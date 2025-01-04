package org.xubin.game.net;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xubin.game.ConfigScanPaths;
import org.xubin.game.base.GameContext;
import xbgame.codec.protobuf.ProtobufMessageCodec;
import xbgame.socket.netty.support.server.TcpSocketServerBuilder;
import xbgame.socket.share.HostAndPort;
import xbgame.socket.share.ServerNode;
import xbgame.socket.share.message.MessageFactory;

@Configuration
@Slf4j
public class SocketServerAutoConfiguration {

    @Bean
    public ServerNode serverNode() {
        TcpSocketServerBuilder builder = TcpSocketServerBuilder.newBuilder();
        builder.bindingPort(HostAndPort.valueOf(GameContext.getServerConfig().getServerPort()))
                .setMessageFactory(GameContext.getMessageFactory())
                .setMessageCodec(new ProtobufMessageCodec())
                .setSocketIoDispatcher(new MessageIoDispatcher());
        return builder.build();
    }

    @Bean
    public MessageFactory createMessageFactory() {
        return new GameMessageFactory(ConfigScanPaths.MESSAGE_BASE_PATH);
    }
}
