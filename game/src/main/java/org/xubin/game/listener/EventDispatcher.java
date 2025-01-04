package org.xubin.game.listener;

import lombok.extern.slf4j.Slf4j;
import xbgame.commons.thread.NamedThreadFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class EventDispatcher {
    private volatile static EventDispatcher instance;

    private final Map<EventType, Set<Object>> observers = new HashMap<>();
    private LinkedBlockingQueue<BaseEvent> eventQueue = new LinkedBlockingQueue<>();


    private EventDispatcher() {
        new NamedThreadFactory("event-dispatch").newThread(new Worker()).start();
    }

    public static EventDispatcher getInstance() {
        if(instance == null) {
            synchronized (EventDispatcher.class) {
                if(instance == null) {
                    instance = new EventDispatcher();
                }
            }
        }
        return instance;
    }

    // 注册事件监听器
    public void registerEvent(EventType eventType, Object listener) {
        Set<Object> listeners = observers.computeIfAbsent(eventType, k -> new CopyOnWriteArraySet<>());

        listeners.add(listener);
    }

    public void triggerEvent(BaseEvent event) {
        if (event == null) {
            throw new NullPointerException("event cannot be null");
        }

        if(event.isSynchronized()) {
            doEvent(event);
        } else {
            eventQueue.add(event);
        }
    }

    private void doEvent(BaseEvent event) {
        Set<Object> listeners = observers.get(event.getEventType());
        if (listeners == null) {
            return;
        }

        for (Object listener : listeners) {
            try {
                ListenerService.getInstance().doEvent(listener, event);
            } catch (Exception e) {
                log.error("event-dispatch error", e);
            }
        }
    }


    private class Worker implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    BaseEvent event = eventQueue.take();
                    doEvent(event);
                } catch (InterruptedException e) {
                    log.error("event-dispatch error", e);
                }
            }
        }
    }

}
