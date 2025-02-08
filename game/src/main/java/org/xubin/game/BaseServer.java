package org.xubin.game;

import org.springframework.stereotype.Component;
import org.xubin.game.base.GameContext;
import org.xubin.game.faction.cache.FactionApplyCacheService;
import org.xubin.game.faction.cache.FactionCacheService;
import org.xubin.game.faction.cache.FactionMemberCacheService;
import org.xubin.game.rpc.login.LoginClient;
import xbgame.socket.share.ServerNode;

@Component
public class BaseServer {

    private final GameContext gameContext;

    public BaseServer(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    public void start() throws Exception {
        initFramework();
        loadData();
        Runtime.getRuntime().addShutdownHook(new Thread(BaseServer::stop));
    }

    private void initFramework() throws Exception {
        GameContext.getBean(ServerNode.class).start();
        GameContext.getBean(LoginClient.class).start();
    }

    private void loadData() {
        GameContext.getBean(FactionCacheService.class).loadAllFactions();
        GameContext.getBean(FactionMemberCacheService.class).loadAllFactionMembers();
        GameContext.getBean(FactionApplyCacheService.class).loadAllFactionApplies();
    }

    private static void stop() {
        System.out.println("stop");
    }
}
