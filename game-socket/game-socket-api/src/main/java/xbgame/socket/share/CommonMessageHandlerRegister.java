package xbgame.socket.share;

import lombok.extern.slf4j.Slf4j;
import xbgame.commons.ClassScanner;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.annotation.MessageRoute;
import xbgame.socket.share.annotation.RequestHandler;
import xbgame.socket.share.message.MessageExecutor;
import xbgame.socket.share.message.MessageFactory;
import xbgame.socket.support.DefaultMessageHandlerRegister;
import xbgame.socket.support.MessageExecuteUnit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class CommonMessageHandlerRegister extends DefaultMessageHandlerRegister {

    private MessageFactory messageFactory;

    public CommonMessageHandlerRegister(String scanPath, MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
        Set<Class<?>> controllers = ClassScanner.listClassesWithAnnotation(scanPath, MessageRoute.class);

        for (Class<?> controller : controllers) {
            try {
                Constructor<?> constructor =  controller.getDeclaredConstructor();
                Object handler = constructor.newInstance();
                register(handler);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    public CommonMessageHandlerRegister(Collection<Object> routes, MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
        routes.forEach(this::register);
    }


    private void register(Object handler) {
        Method[] methods = handler.getClass().getDeclaredMethods();
        for (Method method : methods) {
            RequestHandler annotation = method.getAnnotation(RequestHandler.class);
            if(annotation != null) {
                Optional<Class<?>> msgMeta = getMessageMeta(method);
                msgMeta.ifPresent(c->{
                    int cmd = messageFactory.getMessageId(c);
                    MessageExecutor cmdExecutor = getMessageExecutor(cmd);
                    if (cmdExecutor != null) {
                        throw new RuntimeException(String.format("cmd[%d] duplicated", cmd));
                    }
                    cmdExecutor = MessageExecuteUnit.valueOf(method, method.getParameterTypes(), handler);
                    register(cmd, cmdExecutor);
                });
            }
        }
    }

    private Optional<Class<?>> getMessageMeta(Method method) {
        return Arrays.stream(method.getParameterTypes()).filter(c->c.isAnnotationPresent(MessageMeta.class)).findAny();
    }
}
