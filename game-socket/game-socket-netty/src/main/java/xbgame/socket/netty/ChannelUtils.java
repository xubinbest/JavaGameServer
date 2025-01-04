package xbgame.socket.netty;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import xbgame.socket.share.IdSession;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ChannelUtils {
    public static AttributeKey<IdSession> SESSION_KEY = AttributeKey.valueOf("session");

    public static boolean duplicateBindingSession(Channel channel, IdSession session) {
        Attribute<IdSession> sessionAttr = channel.attr(SESSION_KEY);
        return !sessionAttr.compareAndSet(null, session);
    }

    public static IdSession getSessionBy(Channel channel) {
        Attribute<IdSession> sessionAttr = channel.attr(SESSION_KEY);
        return sessionAttr.get();
    }

    public static String parseRemoteAddress(Channel channel) {
        if (null == channel) {
            return "";
        } else {
            SocketAddress remote = channel.remoteAddress();
            return doParse(remote != null ? remote.toString().trim() : "");
        }
    }

    private static String doParse(String addr) {
        if (addr == null || addr.isEmpty()) {
            return "";
        } else if (addr.charAt(0) == '/') {
            return addr.substring(1);
        } else {
            int len = addr.length();

            for(int i = 1; i < len; ++i) {
                if (addr.charAt(i) == '/') {
                    return addr.substring(i + 1);
                }
            }

            return addr;
        }
    }
}
