package org.xubin.game.player.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xubin.game.base.GameContext;
import org.xubin.game.commons.utils.IdGenerator;
import org.xubin.game.database.game.user.entity.Player;
import org.xubin.game.database.game.user.dao.PlayerDao;
import org.xubin.game.login.message.CreatePlayerS2C;
import org.xubin.game.login.message.vo.PlayerLoginVo;
import xbgame.commons.ds.ConcurrentHashSet;
import xbgame.socket.share.IdSession;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class PlayerService {
    private Set<Long> onlinePlayers = new ConcurrentHashSet<>();

    @Autowired
    private PlayerCacheService playerCacheService;

    public void loadPlayersByAccountId(long accountId) {
        playerCacheService.loadPlayersByAccountId(accountId);
    }

    public Player getPlayer(long id) {
        return playerCacheService.getPlayer(id);
    }

    public void savePlayer(Player player) {
        playerCacheService.putPlayer(player);
    }

    public void addOnlinePlayer(long playerId) {
        onlinePlayers.add(playerId);
    }

    public List<Player> listByAccountId(long accountId) {
        return playerCacheService.listByAccountId(accountId);
    }

    public void removeOnlinePlayer(long playerId) {
        onlinePlayers.remove(playerId);
    }

    public Set<Long> getOnlinePlayers() {
        return new HashSet<>(this.onlinePlayers);
    }

    public void createPlayer(IdSession session, String name) {
        long accountId = (long) session.getAttribute("accountId");
        Player player = new Player();
        player.setId(IdGenerator.nextId());
        player.setName(name);
        player.setAccountId(accountId);
        player.setLevel(1);
        playerCacheService.insertPlayer(player);

        GameContext.getLogService().logNewPlayer(player);

        sendCreatePlayerResult(session, player);

    }

    private void sendCreatePlayerResult(IdSession session, Player player) {
        CreatePlayerS2C createPlayerS2C = new CreatePlayerS2C();
        PlayerLoginVo vo = new PlayerLoginVo();
        vo.setId(player.getId());
        vo.setName(player.getName());
        vo.setLevel(player.getLevel());
        vo.setFight(0);
        createPlayerS2C.setPlayer(vo);
        session.send(createPlayerS2C);
    }

}
