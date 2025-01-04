package xbgame.socket.share.message;

import java.util.Collection;

public interface MessageFactory {

    void registerMessage(int cmd, Class<?> clazz);

    Class<?> getMessage(int cmd);

    int getMessageId(Class<?> clazz);

    boolean contains(Class<?> clazz);

    Collection<Class<?>> registeredClassTypes();

}
