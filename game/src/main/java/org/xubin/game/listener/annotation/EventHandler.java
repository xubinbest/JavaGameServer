package org.xubin.game.listener.annotation;

import org.xubin.game.listener.EventType;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
    EventType[] value();
}
