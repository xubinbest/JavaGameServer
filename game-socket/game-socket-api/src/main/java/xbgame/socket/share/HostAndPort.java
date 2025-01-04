package xbgame.socket.share;

import lombok.Getter;
import lombok.Setter;

public class HostAndPort {
    @Setter
    @Getter
    private String host;
    @Setter
    @Getter
    private int port;

    public static HostAndPort valueOf(String host, int port) {
        HostAndPort hostPort = new HostAndPort();
        hostPort.host = host;
        hostPort.port = port;
        return hostPort;
    }

    public static HostAndPort valueOf(int port) {
        HostAndPort hostPort = new HostAndPort();
        hostPort.host = "localhost";
        hostPort.port = port;
        return hostPort;
    }


}
