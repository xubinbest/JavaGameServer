package xbgame.socket.support;

import xbgame.socket.share.message.MessageExecutor;

import java.lang.reflect.Method;

public class MessageExecuteUnit implements MessageExecutor {

    private Method method;

    private Class<?>[] params;

    private Object handler;

    public static MessageExecuteUnit valueOf(Method method, Class<?>[] params, Object handler) {
        MessageExecuteUnit unit = new MessageExecuteUnit();
        unit.method = method;
        unit.params = params;
        unit.handler = handler;
        return unit;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Class<?>[] getParamTypes() {
        return params;
    }

    @Override
    public Object getHandler() {
        return handler;
    }
}
