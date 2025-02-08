package org.xubin.game.redis;

import org.springframework.data.redis.connection.RedisConfiguration;

/**
 * Redis配置工厂
 * @author xubin
 */
public interface RedisConfigurationFactory {
    RedisConfiguration createConfiguration();
}
