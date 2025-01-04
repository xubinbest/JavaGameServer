package org.xubin.game;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StopWatch;
import org.xubin.game.base.GameContext;
import org.xubin.game.config.ServerType;
import xbgame.commons.NumberUtil;

import java.util.Collections;
import java.util.Properties;

@SpringBootApplication(scanBasePackages = "org.xubin.game")
@EnableCaching
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableScheduling
@Slf4j
public class GameApplication {

    public static void main(String[] args) throws Exception {
        Properties commonProperty = PropertiesLoaderUtils.loadProperties(new FileSystemResource("game/config/common.properties"));
        ServerType serverType = ServerType.of(NumberUtil.intValue(commonProperty.get("server.type")));
        GameContext.serverType = serverType;

        log.info("[{}]启动开始", GameContext.serverType.name);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        SpringApplication app = new SpringApplication(GameApplication.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", NumberUtil.intValue(commonProperty.get("server.port")) + serverType.type));
        app.run(args);

        GameContext.getBean(BaseServer.class).start();

        stopWatch.stop();
        log.info("[{}]启动完成，耗时[{}]", GameContext.serverType.name, stopWatch.getTotalTimeSeconds());

    }

}
