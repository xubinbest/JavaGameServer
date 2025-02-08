package org.xubin.game.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 数据库配置
 * @author xubin
 */
@Configuration
@PropertySource("file:config/db.properties")
@Getter
public class DbConfig {
    @Value("${db.game.ip}")
    private String dbGameIp;

    @Value("${db.game.port}")
    private int dbGamePort;

    @Value("${db.game.dbname}")
    private String dbGameName;

    @Value("${db.game.username}")
    private String dbGameUsername;

    @Value("${db.game.password}")
    private String dbGamePassword;

    @Value("${db.game.driver}")
    private String dbGameDriver;

    @Value("${db.game.maxActive:10}")
    private int dbGameMaxActive;

    @Value("${db.game.maxIdle:5}")
    private int dbGameMaxIdle;

    @Value("${db.game.minIdle:0}")
    private int dbGameMinIdle;


    @Value("${db.log.ip}")
    private String dbLogIp;

    @Value("${db.log.port}")
    private int dbLogPort;

    @Value("${db.log.dbname}")
    private String dbLogName;

    @Value("${db.log.username}")
    private String dbLogUsername;

    @Value("${db.log.password}")
    private String dbLogPassword;

    @Value("${db.log.driver}")
    private String dbLogDriver;

    @Value("${db.log.maxActive:10}")
    private int dbLogMaxActive;

    @Value("${db.log.maxIdle:5}")
    private int dbLogMaxIdle;

    @Value("${db.log.minIdle:0}")
    private int dbLogMinIdle;

}
