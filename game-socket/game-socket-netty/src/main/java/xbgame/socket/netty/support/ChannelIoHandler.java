package xbgame.socket.netty.support;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import xbgame.socket.netty.ChannelUtils;
import xbgame.socket.netty.NSession;
import xbgame.socket.share.IdSession;
import xbgame.socket.share.SocketIoDispatcher;
import xbgame.socket.share.message.RequestDataFrame;

@ChannelHandler.Sharable
@Slf4j
public class ChannelIoHandler extends ChannelInboundHandlerAdapter {

    private final SocketIoDispatcher messageDispatcher;

    public ChannelIoHandler(SocketIoDispatcher messageDispatcher) {
        super();
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive {}", ChannelUtils.parseRemoteAddress(ctx.channel()));
        Channel channel = ctx.channel();
        if(ChannelUtils.duplicateBindingSession(ctx.channel(), new NSession(channel))) {
            ctx.channel().close();
            log.info("重复连接，关闭连接 {}", ChannelUtils.parseRemoteAddress(channel));
            return;
        }
        IdSession session = ChannelUtils.getSessionBy(channel);
        messageDispatcher.onSessionCreated(session);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object frame) throws Exception {
        RequestDataFrame dataFrame = (RequestDataFrame) frame;

        final Channel channel = ctx.channel();
        IdSession session = ChannelUtils.getSessionBy(channel);
        messageDispatcher.dispatch(session, frame);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        IdSession session = ChannelUtils.getSessionBy(channel);
        messageDispatcher.onSessionClosed(session);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.debug("exceptionCaught", cause);
        Channel channel = ctx.channel();
        IdSession session = ChannelUtils.getSessionBy(channel);
        messageDispatcher.exceptionCaught(session, cause);
    }
}
