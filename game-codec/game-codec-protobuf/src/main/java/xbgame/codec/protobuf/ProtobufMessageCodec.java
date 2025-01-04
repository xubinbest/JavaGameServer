package xbgame.codec.protobuf;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import lombok.extern.slf4j.Slf4j;
import xbgame.codec.MessageCodec;

import java.io.IOException;

@Slf4j
public class ProtobufMessageCodec implements MessageCodec {
    @Override
    public Object decode(Class<?> clazz, byte[] bytes) {
        try {
            Codec<?> codec = ProtobufProxy.create(clazz);
            return codec.decode(bytes);
        } catch (IOException e) {
            log.error("read message {} failed", clazz.getName(), e);
        }
        return null;
    }

    @Override
    public byte[] encode(Object message) {
        //写入具体消息的内容
        byte[] body = null;
        Class msgClazz = message.getClass();
        try {
            Codec<Object> codec = ProtobufProxy.create(msgClazz);
            body = codec.encode(message);
        } catch (Exception e) {
            log.error("read message failed", e);
        }
        return body;
    }
}
