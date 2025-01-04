package xbgame.socket.netty.support;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import xbgame.codec.MessageCodec;
import xbgame.socket.share.message.MessageFactory;
import xbgame.socket.share.message.SocketDataFrame;
import xbgame.socket.support.DefaultMessageHeader;

import java.util.Arrays;

@Slf4j
public class DefaultProtocolEncoder extends MessageToByteEncoder<Object> {

    private final MessageFactory messageFactory;

    private final MessageCodec messageCodec;

    public DefaultProtocolEncoder(MessageFactory messageFactory, MessageCodec messageCodec) {
        this.messageFactory = messageFactory;
        this.messageCodec = messageCodec;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object message, ByteBuf out) throws Exception {
        SocketDataFrame dataFrame = (SocketDataFrame) message;

        int  cmd = messageFactory.getMessageId(dataFrame.getMessage().getClass());
        try {
            byte[] body = messageCodec.encode(dataFrame.getMessage());
            int msgLength = body.length + DefaultMessageHeader.SIZE;

            /** 写入长度 */
            out.writeInt(msgLength);
            /** 写入index */
            out.writeInt(dataFrame.getIndex());
            /** 写入cmd */
            out.writeInt(cmd);
            /** 写入消息体 */
            out.writeBytes(body);
        } catch (Exception e) {
            log.error("encode message {} failed", cmd, e);
        }
    }
}
