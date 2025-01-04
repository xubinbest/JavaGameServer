package xbgame.socket.netty.support.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import xbgame.codec.MessageCodec;
import xbgame.socket.netty.support.ChannelIoHandler;
import xbgame.socket.netty.support.DefaultProtocolDecoder;
import xbgame.socket.netty.support.DefaultProtocolEncoder;
import xbgame.socket.share.ChainedMessageDispatcher;
import xbgame.socket.share.HostAndPort;
import xbgame.socket.share.message.MessageFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TcpSocketServerBuilder {

    public static TcpSocketServerBuilder newBuilder() {
        return new TcpSocketServerBuilder();
    }

    List<HostAndPort> ipPorts = new ArrayList<>();

    int maxProtocolBytes = 4096;

    MessageFactory messageFactory;

    MessageCodec messageCodec;

    ChainedMessageDispatcher socketIoDispatcher;

    ByteToMessageDecoder protocolDecoder;

    MessageToByteEncoder<Object> protocolEncoder;

    private ChannelIoHandler channelIoHandler;

    private final ServerIdleHandler serverIdleHandler = new ServerIdleHandler();

    private int idleTime;

    public TcpSocketServerBuilder setSocketIoDispatcher(ChainedMessageDispatcher socketIoDispatcher) {
        this.socketIoDispatcher = socketIoDispatcher;
        return this;
    }

    public TcpSocketServerBuilder setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
        return this;
    }

    public TcpSocketServerBuilder setMessageCodec(MessageCodec messageCodec) {
        this.messageCodec = messageCodec;
        return this;
    }

    public TcpSocketServerBuilder bindingPort(HostAndPort hostAndPort) {
        this.ipPorts.add(hostAndPort);
        return this;
    }

    public TcpSocketServer build() {
        TcpSocketServer server = new TcpSocketServer();
        server.nodesConfig = ipPorts;
        channelIoHandler = new ChannelIoHandler(socketIoDispatcher);

        server.childChannelInitializer = new ChildChannelHandler();

        return server;
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel channel) throws Exception {
            ChannelPipeline pipeline = channel.pipeline();

            if (protocolDecoder != null) {
                pipeline.addLast("protocolDecoder",protocolDecoder);
            } else {
                pipeline.addLast("protocolDecoder", new DefaultProtocolDecoder(messageFactory, messageCodec, maxProtocolBytes));
            }
            if (protocolEncoder != null) {
                pipeline.addLast("protocolEncoder", protocolEncoder);
            } else {
                pipeline.addLast("protocolEncoder", new DefaultProtocolEncoder(messageFactory, messageCodec));
            }

            if (idleTime > 0) {
                // 客户端XXX没收发包，便会触发UserEventTriggered事件到IdleEventHandler
                pipeline.addLast(new IdleStateHandler(0, 0, idleTime, TimeUnit.MILLISECONDS));
                pipeline.addLast("serverIdleHandler", serverIdleHandler);
            }
            pipeline.addLast("socketIoHandler", channelIoHandler);

        }
    }


}
