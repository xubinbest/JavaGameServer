package xbgame.socket.support;

import lombok.extern.slf4j.Slf4j;
import xbgame.socket.share.message.MessageFactory;

import java.util.*;

@Slf4j
public class DefaultMessageFactory implements MessageFactory {

    private final Map<Integer, Class<?>> id2Clazz = new HashMap<>();

    private final Map<Class<?>, Integer> clazz2Id = new HashMap<>();


    @Override
    public void registerMessage(int cmd, Class<?> clazz) {

        if (id2Clazz.containsKey(cmd)) {
            throw new IllegalStateException("message meta [" + cmd + "] duplicate！！");
        }
        id2Clazz.put(cmd, clazz);
        clazz2Id.put(clazz, cmd);
    }

    @Override
    public Class<?> getMessage(int cmd) {
        return id2Clazz.get(cmd);
    }

    @Override
    public int getMessageId(Class<?> clazz) {
        return clazz2Id.get(clazz);
    }

    @Override
    public boolean contains(Class<?> clazz) {
        return clazz2Id.containsKey(clazz);
    }

    @Override
    public Collection<Class<?>> registeredClassTypes() {
        return new HashSet<>(clazz2Id.keySet());
    }
}
