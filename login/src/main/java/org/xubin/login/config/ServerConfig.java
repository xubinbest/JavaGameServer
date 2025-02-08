package org.xubin.login.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Slf4j
@DependsOn("serverConfigFactory")
public class ServerConfig {

    @Value("${socket.serverIp}")
    private String serverIp;

    @Value("${socket.port:0}")
    private int serverPort;
}
