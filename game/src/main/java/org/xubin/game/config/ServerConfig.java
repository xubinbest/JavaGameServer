package org.xubin.game.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Getter
@Setter
@DependsOn("serverConfigFactory")
public class ServerConfig {
    @Value("${socket.id:0}")
    private int serverId;

    @Value("${socket.serverIp}")
    private String serverIp;

    @Value("${socket.port:0}")
    private int serverPort;

    @Value("${admin.http.ips}")
    private String adminIps;

    @Value("${login.serverIp}")
    private String loginServerIp;

    @Value("${login.port:0}")
    private int loginServerPort;


    private Set<String> adminWhiteIps;

    public Set<String> getAdminIps() {
        if (adminWhiteIps == null) {
            Set<String> tmpIps = new HashSet<>();
            for (String ip : adminIps.split(";")) {
                tmpIps.add(ip);
            }
            adminWhiteIps = tmpIps;
        }
        return adminWhiteIps;
    }
}
