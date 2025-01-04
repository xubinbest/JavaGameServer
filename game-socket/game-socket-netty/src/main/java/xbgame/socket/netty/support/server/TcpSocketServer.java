package xbgame.socket.netty.support.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import xbgame.socket.share.HostAndPort;
import xbgame.socket.share.ServerNode;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class TcpSocketServer implements ServerNode {

    protected List<HostAndPort> nodesConfig;
    protected ChannelInitializer<SocketChannel> childChannelInitializer;

    private final int CORE_SIZE = Runtime.getRuntime().availableProcessors();

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @Override
    public void start() throws Exception {
        try {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(CORE_SIZE);
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(childChannelInitializer)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            for (HostAndPort node : nodesConfig) {
                log.info("socket server is listening at {}......", node.getPort());
                bootstrap.bind(new InetSocketAddress(node.getPort())).sync();
            }
        } catch (Exception e) {
            log.error("start socket server error", e);
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            throw e;
        }

    }

    @Override
    public void shutdown() throws Exception {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        log.info("socket server stopped successfully");
    }
}
