package org.xubin.game.redis;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Configuration
@PropertySource("file:config/redis.properties")
@Getter
public class RedisConfig {
    @Value("${redis.mode}")
    private String redisMode;

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    @Value("${redis.password:null}")
    private String password;

    @Value("${redis.cluster.nodes}")
    private String clusterNodes;

    public List<String> getClusterNodes() {
        return List.of(clusterNodes.split(","));
    }

}
