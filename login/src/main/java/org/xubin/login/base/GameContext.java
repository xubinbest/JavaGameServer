package org.xubin.login.base;

import jakarta.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.xubin.login.config.ServerConfig;
import org.xubin.login.node.GameNodeService;
import xbgame.socket.share.message.MessageFactory;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

@Component
@Setter(onMethod = @__(@Autowired))
@Slf4j
public class GameContext implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;
    private static GameContext instance;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        GameContext.applicationContext = applicationContext;
    }

    @PostConstruct
    private void init() {
        instance = this;
    }

    public GameContext getInstance() {
        return instance;
    }

    public ServerConfig serverConfig;
    public static ServerConfig getServerConfig() {
        return instance.serverConfig;
    }

    private MessageFactory messageFactory;
    public static MessageFactory getMessageFactory() {
        return instance.messageFactory;
    }

    private GameNodeService gameNodeService;
    public static GameNodeService getGameNodeService() {
        return instance.gameNodeService;
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static <T> Collection<T> getBeansOfType(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz).values();
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    public static <T> Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> clazz) {
        return applicationContext.getBeansWithAnnotation(clazz);
    }
}
