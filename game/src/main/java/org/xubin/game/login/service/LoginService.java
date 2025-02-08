package org.xubin.game.login.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xubin.game.account.service.AccountService;
import org.xubin.game.base.GameContext;
import org.xubin.game.database.game.user.entity.Account;
import org.xubin.game.database.game.user.entity.Player;
import org.xubin.game.listener.EventDispatcher;
import org.xubin.game.listener.EventType;
import org.xubin.game.login.events.PlayerLoginEvent;
import org.xubin.game.login.message.LoginS2C;
import org.xubin.game.login.message.SelectPlayerS2C;
import org.xubin.game.login.message.vo.PlayerLoginVo;
import org.xubin.game.player.service.PlayerService;
import xbgame.socket.share.IdSession;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录服务
 * @author xubin
 */
@Service
@Slf4j
public class LoginService {
    @Autowired
    private AccountService accountService;
    @Autowired
    private PlayerService playerService;

    /**
     * 登录
     * @param session session
     * @param accountId 账号ID
     */
    public void login(IdSession session, long accountId) {
        Account account = accountService.getAccount(accountId);
        if (account == null) {
            accountService.createAccount(accountId);
        }

        session.setAttribute("accountId", accountId);

        playerService.loadPlayersByAccountId(accountId);
        List<Player> playerList = playerService.listByAccountId(accountId);
        List<PlayerLoginVo> playerVoList = new ArrayList<>();
        for (Player player : playerList) {
            PlayerLoginVo vo = new PlayerLoginVo();
            vo.setId(player.getId());
            vo.setName(player.getName());
            vo.setLevel(player.getLevel());
            playerVoList.add(vo);
        }

        // 发送角色信息
        sendLoginS2C(session, 0, playerVoList);
    }

    /**
     * 选择角色
     * @param session session
     * @param playerId 角色ID
     */
    public void selectPlayer(IdSession session, long playerId) {
        Player player = playerService.getPlayer(playerId);
        if(player != null) {
            GameContext.getSessionManager().registerNewPlayer(player, session);
            GameContext.getPlayerService().addOnlinePlayer(playerId);

            sendSelectPlayerS2C(session, 0, playerId);

            // 登录事件(同步事件)
            EventDispatcher.getInstance().triggerEvent(new PlayerLoginEvent(EventType.LOGIN, playerId));

            // 玩家所有数据加载放在上面的登录事件中，所有加载完成后计算属性
            GameContext.getPlayerAttrService().initPlayerAttr(playerId);
            GameContext.getLoginClient().updatePlayerLastServerId(player.getAccountId());
        } else {
            log.error("player not found, playerId: {}", playerId);
            sendSelectPlayerS2C(session, 1, playerId);
        }
    }

    // 发送登录结果
    private void sendSelectPlayerS2C(IdSession session, int result, long playerId) {
        SelectPlayerS2C selectPlayerS2C = new SelectPlayerS2C();
        selectPlayerS2C.setResult(result);
        selectPlayerS2C.setPlayerId(playerId);
        session.send(selectPlayerS2C);
    }

    // 发送登录结果
    private void sendLoginS2C(IdSession session, int code, List<PlayerLoginVo> players) {
        LoginS2C loginS2C = new LoginS2C();
        loginS2C.setCode(code);
        loginS2C.setPlayers(players);
        session.send(loginS2C);
    }
}
