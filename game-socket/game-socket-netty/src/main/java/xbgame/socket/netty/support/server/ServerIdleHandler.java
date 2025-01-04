package xbgame.socket.netty.support.server;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class ServerIdleHandler extends ChannelDuplexHandler {
    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, Object event) throws Exception {
        if(event instanceof IdleStateEvent) {
            try {
                log.warn("session [{}] idle, close it from the server side", ctx.channel().remoteAddress());
                ctx.channel().close();
            } catch (Exception e) {
                log.error("close session failed", e);
            }
        } else {
            super.userEventTriggered(ctx, event);
        }
    }
}
