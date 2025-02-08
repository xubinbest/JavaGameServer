package org.xubin.game.database.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
 * Log数据源配置
 * 存储游戏内日志数据
 * @author xubin
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "org.xubin.game.database.log", // log 数据库的 DAO 包路径
        entityManagerFactoryRef = "logEntityManagerFactory",
        transactionManagerRef = "logTransactionManager"
)
public class LogDataSourceConfig {
    private final ServerConfig serverConfig;
    private final DbConfig dbConfig;

    public LogDataSourceConfig(ServerConfig serverConfig, DbConfig dbConfig) {
        this.serverConfig = serverConfig;
        this.dbConfig = dbConfig;
    }

    @Bean(name = "logDataSource")
    public DataSource hikariDataSource() {
        return new HikariDataSource(createHikariConfig());
    }

    // 配置HikariCP连接池详细参数
    private HikariConfig createHikariConfig() {
        HikariConfig config = new HikariConfig();
        // 基础连接配置
        config.setJdbcUrl(getJdbcUrl());
        config.setUsername(dbConfig.getDbLogUsername());
        config.setPassword(dbConfig.getDbLogPassword());
        config.setDriverClassName(dbConfig.getDbLogDriver());

        // 连接池配置
        // max-active
        config.setMaximumPoolSize(dbConfig.getDbLogMaxActive());
        // max-idle
        config.setMaximumPoolSize(dbConfig.getDbLogMaxIdle());
        // min-idle
        config.setMinimumIdle(dbConfig.getDbLogMinIdle());

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

    private String getJdbcUrl() {
        return "jdbc:mysql://" + dbConfig.getDbLogIp() + ":" + dbConfig.getDbLogPort() + "/" +
                dbConfig.getDbLogName() + "_" + serverConfig.getServerId() +
                "?useUnicode=true";
    }

    @Bean(name = "logEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(hikariDataSource())
                .packages("org.xubin.game.database.log")
                .persistenceUnit("log")
                .properties(getJpaProperties())
                .build();
    }

    // JPA属性配置
    private Map<String, Object> getJpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        props.put("hibernate.format_sql", true);
        props.put("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
        props.put("hibernate.hbm2ddl.auto", "update");
        return props;
    }

    @Bean(name = "logTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("logEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}