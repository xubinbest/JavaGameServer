package org.xubin.game.redis;

import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;

/**
 * 单机配置工厂
 * @author xubin
 */
public class StandaloneConfigurationFactory implements RedisConfigurationFactory {
    private final RedisConfig redisConfig;

    public StandaloneConfigurationFactory(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    @Override
    public RedisConfiguration createConfiguration() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisConfig.getHost());
        configuration.setPort(redisConfig.getPort());
        if (!redisConfig.getPassword().isEmpty()) {
            configuration.setPassword(redisConfig.getPassword());
        }
        return configuration;
    }
}
