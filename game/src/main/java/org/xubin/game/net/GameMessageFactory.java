package org.xubin.game.net;

import lombok.extern.slf4j.Slf4j;
import xbgame.commons.ClassScanner;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.annotation.MessageRoute;
import xbgame.socket.share.message.Message;
import xbgame.socket.share.message.MessageFactory;
import xbgame.socket.support.DefaultMessageFactory;

import java.util.Collection;
import java.util.Set;

@Slf4j
public class GameMessageFactory implements MessageFactory {

    private static volatile DefaultMessageFactory instance = new DefaultMessageFactory();

    public GameMessageFactory(String path) {
        Set<Class<?>> messages = ClassScanner.listClassesWithAnnotation(path, MessageRoute.class);
        for (Class<?> clazz : messages) {
            MessageRoute meta = clazz.getAnnotation(MessageRoute.class);
            if (meta == null) {
                throw new RuntimeException("messages[" + clazz.getSimpleName() + "] missed MessageMeta annotation");
            }

            short module = meta.module();

            // TODO: XB magic number
            if (Math.abs(module) > 326) {
                throw new RuntimeException("abs(module) must less than 326, target " + module);
            }

            // facede层所在包名的上一层
            int prevPacketNameIndex = clazz.getPackage().getName().lastIndexOf(".");
            String packetName = clazz.getPackage().getName().substring(0, prevPacketNameIndex);
            Set<Class<?>> msgClazzs = ClassScanner.listAllSubclasses(packetName, Message.class);

            msgClazzs.parallelStream().filter(msgClz -> msgClz.getAnnotation(MessageMeta.class) != null).forEach(msgClz -> {
                MessageMeta mapperAnnotation = msgClz.getAnnotation(MessageMeta.class);
                int cmdMeta = mapperAnnotation.cmd();
                if (Math.abs(cmdMeta) > 99) {
                    throw new RuntimeException("abs(cmd) must less than 100, target " + msgClz.getSimpleName());
                }
                short key = (short) (Math.abs(module) * 100 + cmdMeta);
                if (module < 0) {
                    key = (short) (-key);
                }
                instance.registerMessage(key, msgClz);
            });

        }
    }

    @Override
    public void registerMessage(int cmd, Class<?> clazz) {
        instance.registerMessage(cmd, clazz);
    }

    @Override
    public Class<?> getMessage(int cmd) {
        return instance.getMessage(cmd);
    }

    @Override
    public int getMessageId(Class<?> clazz) {
        return instance.getMessageId(clazz);
    }

    @Override
    public boolean contains(Class<?> clazz) {
        return instance.contains(clazz);
    }

    @Override
    public Collection<Class<?>> registeredClassTypes() {
        return instance.registeredClassTypes();
    }
}
