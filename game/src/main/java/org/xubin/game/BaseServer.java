package org.xubin.game;

import org.springframework.stereotype.Component;
import org.xubin.game.base.GameContext;
import xbgame.socket.share.ServerNode;

@Component
public class BaseServer {

    public void start() throws Exception {
        initFramework();
        Runtime.getRuntime().addShutdownHook(new Thread(BaseServer::stop));
    }

    private void initFramework() throws Exception {
        GameContext.getBean(ServerNode.class).start();
    }

    private static void stop() {
        System.out.println("stop");
    }
}
