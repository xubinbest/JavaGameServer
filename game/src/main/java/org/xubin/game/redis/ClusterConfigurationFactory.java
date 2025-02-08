package org.xubin.game.redis;

import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConfiguration;

import java.util.List;

public class ClusterConfigurationFactory implements RedisConfigurationFactory {
    private RedisConfig redisConfig;

    public ClusterConfigurationFactory(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    @Override
    public RedisConfiguration createConfiguration() {
        RedisClusterConfiguration configuration = new RedisClusterConfiguration();

        List<String> clusterNodes = redisConfig.getClusterNodes();
        if (clusterNodes == null || clusterNodes.isEmpty()) {
            throw new IllegalArgumentException("Cluster nodes must be set");
        } else {
            // 使用配置文件中的集群节点
            for (String node : clusterNodes) {
                String[] parts = node.split(":");
                configuration.clusterNode(parts[0], Integer.parseInt(parts[1]));
            }
        }

        if (!redisConfig.getPassword().isEmpty()) {
            configuration.setPassword(redisConfig.getPassword());
        }

        return configuration;
    }
}
