package xbgame.codec;

public interface MessageCodec {
    Object decode(Class<?> clazz, byte[] bytes);

    byte[] encode(Object message);
}
