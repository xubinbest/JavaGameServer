package xbgame.socket.share;

import xbgame.socket.share.message.SocketDataFrame;

import java.io.Closeable;
import java.net.InetSocketAddress;

public interface IdSession extends Closeable {
    String ID = "ID";

    default String getId() {
        if (getAttribute(ID) != null) {
            return getAttribute(ID).toString();
        }
        return "";
    }

    void send(Object packet);

    default void send(int index, Object packet) {
        send(SocketDataFrame.withIndex(index, packet));
    }

    InetSocketAddress getRemoteAddress();
    String getRemoteIP();
    int getRemotePort();

    InetSocketAddress getLocalAddress();
    String getLocalIP();
    int getLocalPort();

    void setAttribute(String key, Object value);
    Object getAttribute(String key);

    default boolean hasAttribute(String key) {
        return getAttribute(key) != null;
    }

    Object getRawSession();


}
