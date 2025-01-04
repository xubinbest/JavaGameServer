package org.xubin.game.listener;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xubin.game.listener.annotation.EventHandler;
import org.xubin.game.listener.annotation.Listener;
import xbgame.commons.ClassScanner;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class ListenerService {

    private static ListenerService instance;
    private Map<String, Method> map = new HashMap<>();

    private final String SCAN_PATH = "org.xubin.game";

    @PostConstruct
    public void init() {
        instance = this;
        Set<Class<?>> listeners = ClassScanner.listClassesWithAnnotation(SCAN_PATH, Listener.class);

        for (Class<?> listener : listeners) {
            try {
                Object handler = listener.newInstance();
                Method[] methods = listener.getDeclaredMethods();
                for (Method method : methods) {
                    EventHandler annotation = method.getAnnotation(EventHandler.class);
                    if(annotation != null) {
                        EventType[] eventTypes = annotation.value();
                        for (EventType eventType : eventTypes) {
                            EventDispatcher.getInstance().registerEvent(eventType, handler);
                            String key = getKey(handler, eventType);
                            map.put(key, method);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    public static ListenerService getInstance() {
        return instance;
    }

    public void doEvent(Object object, BaseEvent event) {
        try {
            String key = getKey(object, event.getEventType());
            Method method = map.get(key);
            method.invoke(object, event);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private String getKey(Object handler, EventType eventType) {
        return handler.getClass().getName() + "-" + eventType.toString();
    }
}
