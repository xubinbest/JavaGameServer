package xbgame.socket.share.message;

import java.lang.reflect.Method;

public interface MessageExecutor {
    Method getMethod();
    Class<?>[] getParamTypes();
    Object getHandler();
}
