package xbgame.socket.netty.support;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import xbgame.codec.MessageCodec;
import xbgame.socket.share.message.MessageFactory;
import xbgame.socket.share.message.RequestDataFrame;
import xbgame.socket.support.DefaultMessageHeader;

import java.util.List;

@Slf4j
public class DefaultProtocolDecoder extends ByteToMessageDecoder {
    @Setter
    private int maxProtocolBytes;

    private final MessageFactory messageFactory;

    private final MessageCodec messageCodec;

    public DefaultProtocolDecoder(MessageFactory messageFactory, MessageCodec messageCodec) {
        this(messageFactory, messageCodec, 4096);
    }

    public DefaultProtocolDecoder(MessageFactory messageFactory, MessageCodec messageCodec, int maxProtocolBytes) {
        this.messageFactory = messageFactory;
        this.messageCodec = messageCodec;
        this.maxProtocolBytes = maxProtocolBytes;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in.readableBytes() < DefaultMessageHeader.SIZE) {
            return;
        }
        in.markReaderIndex();

        byte[] header = new byte[DefaultMessageHeader.SIZE];

        in.readBytes(header);

        DefaultMessageHeader headerMeta = new DefaultMessageHeader();
        headerMeta.read(header);

        int length = headerMeta.getMessageLength();

        if (length > maxProtocolBytes) {
            log.error("message data frame [{}] too large, close session now", length);
            ctx.close();
            return;
        }

        int bodySize = length - DefaultMessageHeader.SIZE;
        if (in.readableBytes() < bodySize) {
            in.resetReaderIndex();
            return;
        }

        int cmd = headerMeta.getCmd();
        byte[] body = new byte[bodySize];
        in.readBytes(body);

        Class<?> msgClazz = messageFactory.getMessage(cmd);

        Object message = messageCodec.decode(msgClazz, body);
        out.add(new RequestDataFrame(headerMeta, message));
    }
}
