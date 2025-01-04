package org.xubin.game.player.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xubin.game.account.module.AccountProfile;
import org.xubin.game.base.GameContext;
import org.xubin.game.commons.utils.IdGenerator;
import org.xubin.game.database.game.user.entity.AccountEnt;
import org.xubin.game.database.game.user.entity.PlayerEnt;
import org.xubin.game.database.game.user.dao.PlayerDao;
import org.xubin.game.login.message.CreatePlayerS2C;
import org.xubin.game.login.message.vo.PlayerLoginVo;
import org.xubin.game.player.module.PlayerProfile;
import xbgame.commons.ds.ConcurrentHashSet;
import xbgame.socket.share.IdSession;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class PlayerService {
    private Set<Long> onlinePlayers = new ConcurrentHashSet<>();

    private ConcurrentMap<Long, PlayerProfile> playerProfiles = new ConcurrentHashMap<>();
    private ConcurrentMap<Long, AccountProfile> accountProfiles = new ConcurrentHashMap<>();

    @Autowired
    private PlayerDao playerDao;

    @Autowired
    private PlayerCacheService playerCacheService;

    public PlayerEnt getPlayer(long id) {
        return playerCacheService.getEntity(id);
    }

    public void savePlayer(PlayerEnt player) {
        playerCacheService.putEntity(player);
    }

    public void addOnlinePlayer(long playerId) {
        onlinePlayers.add(playerId);
    }

    public void removeOnlinePlayer(long playerId) {
        onlinePlayers.remove(playerId);
    }

    public Set<Long> getOnlinePlayers() {
        return new HashSet<>(this.onlinePlayers);
    }

    public AccountProfile getAccountProfile(Long accountId) {
        AccountProfile accountProfile = accountProfiles.get(accountId);
        if(accountProfile != null) {
            return accountProfile;
        }

        AccountEnt accountEnt = GameContext.getAccountService().getEntity(accountId);
        if(accountEnt != null) {
            accountProfile = new AccountProfile();
            accountProfile.setAccountId(accountId);
            List<PlayerProfile> players = playerDao.getByAccountId(accountId);
            accountProfile.setPlayers(players);
            accountProfiles.putIfAbsent(accountId, accountProfile);
        }
        return accountProfile;
    }

    public void addAccountProfile(AccountEnt accountEnt) {
        long accountId = accountEnt.getId();
        if(accountProfiles.containsKey(accountId)) {
            return;
        }
        AccountProfile accountProfile = new AccountProfile();
        accountProfile.setAccountId(accountId);
        accountProfiles.put(accountId, accountProfile);
    }

    public void createPlayer(IdSession session, String name) {
        long accountId = (long) session.getAttribute("accountId");
        AccountProfile accountProfile = getAccountProfile(accountId);

        PlayerEnt player = new PlayerEnt();
        player.setId(IdGenerator.nextId());
        player.setName(name);
        player.setAccountId(accountId);
        player.setLevel(1);


        GameContext.getAsyncDbService().saveToDb(player);
        GameContext.getLogService().logNewPlayer(player);

        long playerId = player.getId();
        PlayerProfile playerProfile = new PlayerProfile(playerId, accountId, name, 0, 1, 0);
        playerProfiles.put(playerProfile.getPlayerId(), playerProfile);
        accountProfile.addPlayerProfile(playerProfile);

        sendCreatePlayerResult(session, playerProfile);

    }

    private void sendCreatePlayerResult(IdSession session, PlayerProfile player) {
        CreatePlayerS2C createPlayerS2C = new CreatePlayerS2C();
        PlayerLoginVo vo = new PlayerLoginVo();
        vo.setId(player.getPlayerId());
        vo.setName(player.getName());
        vo.setLevel(player.getLevel());
        vo.setFight(0);
        createPlayerS2C.setPlayer(vo);
        session.send(createPlayerS2C);
    }

}
