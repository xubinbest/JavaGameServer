package org.xubin.login;

import org.springframework.stereotype.Component;
import org.xubin.login.base.GameContext;
import xbgame.socket.share.ServerNode;

@Component
public class BaseServer {
    private final GameContext gameContext;

    public BaseServer(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    public void start() throws Exception {
        initFramework();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> stop()));
    }

    private void initFramework() throws Exception {
        GameContext.getBean(ServerNode.class).start();
    }

    private static void stop() {
        System.out.println("stop");
    }
}
