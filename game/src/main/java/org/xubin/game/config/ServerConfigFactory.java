package org.xubin.game.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.ResourcePropertySource;
import org.xubin.game.base.GameContext;

import java.io.IOException;

@Configuration
@Slf4j
public class ServerConfigFactory {

    @Autowired
    private StandardEnvironment environment;

    @PostConstruct
    public void init() throws IOException {
        switch(GameContext.serverType) {
            case GATE:
                environment.getPropertySources().addLast(new ResourcePropertySource("file:game/config/gate.properties"));
                break;
            case GAME:
                environment.getPropertySources().addLast(new ResourcePropertySource("file:game/config/server.properties"));
                break;
            case CENTRE:
                environment.getPropertySources().addLast(new ResourcePropertySource("file:game/config/center.properties"));
                break;
            default:
                log.info("未知服务器类型 {}", GameContext.serverType);
                break;
        }
        environment.getPropertySources().addLast(new ResourcePropertySource("file:game/config/common.properties"));
    }
}
