package org.xubin.game.redis;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheIndex {
    CacheIndexType type();
}
