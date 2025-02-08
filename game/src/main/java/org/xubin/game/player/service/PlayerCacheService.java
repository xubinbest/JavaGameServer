package org.xubin.game.player.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.xubin.game.database.game.user.dao.PlayerDao;
import org.xubin.game.database.game.user.entity.Player;
import org.xubin.game.redis.RedisCacheService;

import java.util.List;

/**
 * 玩家缓存服务
 *
 * @author xubin
 */
@Service
public class PlayerCacheService {
    private final PlayerDao playerDao;
    private final RedisCacheService<Player> cache;

    public PlayerCacheService(RedisTemplate<String, Object> redisTemplate, PlayerDao playerDao) {
        this.playerDao = playerDao;
        cache = new RedisCacheService.Builder<Player>()
                .setRedisTemplate(redisTemplate)
                .setDao(playerDao)
                .setCacheName("player")
                .setEntityClass(Player.class)
                .build();
    }

    public Player getPlayer(Long id) {
        return cache.getEntity(id);
    }

    public void putPlayer(Player entity) {
        cache.updateEntity(entity);
    }

    public void insertPlayer(Player entity) {
        cache.insertEntity(entity);
    }

    public List<Player> listByAccountId(long accountId) {
        return cache.getEntityFromMultipleList("accountId", String.valueOf(accountId));
    }

    public void loadPlayersByAccountId(long accountId) {
        List<Player> players = playerDao.listByAccountId(accountId);
        for (Player player : players) {
            cache.cacheEntityIfNotExist(player);
        }
    }
}
