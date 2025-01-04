package xbgame.socket.share.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageMeta {
    byte source() default 0;

    short module() default 0;

    int cmd() default 0;
}
