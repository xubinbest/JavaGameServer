package org.xubin.game.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis工厂
 * @author xubin
 */
@Configuration
public class RedisFactory {
    @Autowired
    private RedisConfig redisConfig;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisConfigurationFactory factory = createFactory();
        return new LettuceConnectionFactory(factory.createConfiguration());
    }

//    @Bean
//    public LettuceConnectionFactory redisConnectionFactory() {
//        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
//        clusterConfiguration.clusterNode("192.168.101.80", 7000);
//        clusterConfiguration.clusterNode("192.168.101.80", 7001);
//        clusterConfiguration.clusterNode("192.168.101.80", 7002);
//        clusterConfiguration.clusterNode("192.168.101.80", 7003);
//        clusterConfiguration.clusterNode("192.168.101.80", 7004);
//        clusterConfiguration.clusterNode("192.168.101.80", 7005);
//
//        return new LettuceConnectionFactory(clusterConfiguration);
//    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        template.setValueSerializer(new FastJsonRedisSerializer<>(Object.class));
        template.setHashValueSerializer(new FastJsonRedisSerializer<>(Object.class));

        template.afterPropertiesSet();

        return template;
    }

    // 配置工厂创建器
    private RedisConfigurationFactory createFactory() {
        return "cluster".equalsIgnoreCase(redisConfig.getRedisMode())
                ? new ClusterConfigurationFactory(redisConfig)
                : new StandaloneConfigurationFactory(redisConfig);
    }
}
