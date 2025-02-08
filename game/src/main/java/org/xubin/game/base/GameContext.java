package org.xubin.game.base;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.xubin.game.LogService;
import org.xubin.game.account.service.AccountService;
import org.xubin.game.asyncdb.AsyncDbService;
import org.xubin.game.attribute.service.PlayerAttrService;
import org.xubin.game.bag.service.BagService;
import org.xubin.game.config.ServerConfig;
import org.xubin.game.config.ServerType;
import org.xubin.game.data.DataCfgManager;
import org.xubin.game.equip.service.EquipService;
import org.xubin.game.faction.service.FactionService;
import org.xubin.game.player.service.PlayerService;
import org.xubin.game.rpc.login.LoginClient;
import xbgame.socket.share.message.MessageFactory;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

@Component
@Setter(onMethod = @__(@Autowired))
@Slf4j
public class GameContext implements ApplicationContextAware {

    public static ServerType serverType;

    private static GameContext instance;

    @Getter
    private static ApplicationContext applicationContext = null;

    @PostConstruct
    private void init() {
        instance = this;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        GameContext.applicationContext = applicationContext;
    }

    public GameContext getInstance() {
        return instance;
    }

    public ServerConfig serverConfig;
    public static ServerConfig getServerConfig() {
        return instance.serverConfig;
    }

    private AsyncDbService asyncDbService;

    public static AsyncDbService getAsyncDbService() {
        return instance.asyncDbService;
    }

    private SessionManager sessionManager;
    public static SessionManager getSessionManager() {
        return instance.sessionManager;
    }

    private LoginClient loginClient;
    public static LoginClient getLoginClient() {
        return instance.loginClient;
    }

    private PlayerService playerService;
    public static PlayerService getPlayerService() {
        return instance.playerService;
    }

    private AccountService accountService;
    public static AccountService getAccountService() {
        return instance.accountService;
    }


    private MessageFactory messageFactory;
    public static MessageFactory getMessageFactory() {
        return instance.messageFactory;
    }

    // 登录服务
    private LogService logService;
    public static LogService getLogService() {
        return instance.logService;
    }

    // 背包服务
    private BagService bagService;
    public static BagService getBagService() {
        return instance.bagService;
    }

    // 装备服务
    private EquipService equipService;
    public static EquipService getEquipService() {
        return instance.equipService;
    }

    // 玩家属性服务
    private PlayerAttrService playerAttrService;
    public static PlayerAttrService getPlayerAttrService() {
        return instance.playerAttrService;
    }

    // 数据配置管理
    private DataCfgManager dataCfgManager;
    public static DataCfgManager getDataCfgManager() {
        return instance.dataCfgManager;
    }

    // 公会服务
    private FactionService factionService;
    public static FactionService getFactionService() {
        return instance.factionService;
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static <T> Collection<T> getBeansOfType(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz).values();
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    public static <T> Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> clazz) {
        return applicationContext.getBeansWithAnnotation(clazz);
    }

}
