package org.xubin.game.database.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.xubin.game.config.DbConfig;
import org.xubin.game.config.ServerConfig;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Game数据源配置
 * 存储游戏内玩家数据和公共数据
 * @author xubin
 */
@Configuration
@Slf4j
@EnableJpaRepositories(
        basePackages = "org.xubin.game.database.game",
        entityManagerFactoryRef = "gameEntityManagerFactory",
        transactionManagerRef = "gameTransactionManager"
)
public class GameDataSourceConfig {
    private final ServerConfig serverConfig;
    private final DbConfig dbConfig;

    public GameDataSourceConfig(ServerConfig serverConfig, DbConfig dbConfig) {
        this.serverConfig = serverConfig;
        this.dbConfig = dbConfig;
    }

    @Primary
    @Bean(name = "gameDataSource")
    public DataSource hikariDataSource() {
        return new HikariDataSource(createHikariConfig());
    }

    private String getJdbcUrl() {
        return "jdbc:mysql://" + dbConfig.getDbGameIp() + ":" + dbConfig.getDbGamePort() + "/" +
                dbConfig.getDbGameName() + "_" + serverConfig.getServerId() +
                "?useUnicode=true";
    }

    // 配置HikariCP连接池详细参数
    private HikariConfig createHikariConfig() {
        HikariConfig config = new HikariConfig();
        // 基础连接配置
        config.setJdbcUrl(getJdbcUrl());
        config.setUsername(dbConfig.getDbGameUsername());
        config.setPassword(dbConfig.getDbGamePassword());
        config.setDriverClassName(dbConfig.getDbGameDriver());

        // 连接池配置
        // max-active
        config.setMaximumPoolSize(dbConfig.getDbGameMaxActive());
        // max-idle
        config.setMaximumPoolSize(dbConfig.getDbGameMaxIdle());
        // min-idle
        config.setMinimumIdle(dbConfig.getDbGameMinIdle());

        // 其他优化配置
        config.setAutoCommit(true);
        // 连接超时时间：30秒
        config.setConnectionTimeout(30000);
        // 空闲连接超时时间：10分钟
        config.setIdleTimeout(600000);
        // 连接最大生命周期：30分钟
        config.setMaxLifetime(1800000);

        return config;
    }

    @Primary
    @Bean(name = "gameEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(hikariDataSource())
                .packages("org.xubin.game.database.game")
                .persistenceUnit("game")
                .properties(getJpaProperties())
                .build();
    }

    // JPA属性配置
    private Map<String, Object> getJpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        props.put("hibernate.format_sql", true);
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
        return props;
    }

    @Primary
    @Bean(name = "gameTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("gameEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
