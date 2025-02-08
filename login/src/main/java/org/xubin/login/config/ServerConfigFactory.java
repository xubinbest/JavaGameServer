package org.xubin.login.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

@Configuration
@Slf4j
public class ServerConfigFactory {

    @Autowired
    private StandardEnvironment environment;

    @PostConstruct
    public void init() throws IOException {
        environment.getPropertySources().addLast(new ResourcePropertySource("file:config/login.properties"));
    }
}
