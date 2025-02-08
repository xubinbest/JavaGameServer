package org.xubin.game.faction.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.xubin.game.database.game.faction.dao.FactionDao;
import org.xubin.game.database.game.faction.entity.Faction;
import org.xubin.game.redis.RedisCacheService;

import java.util.List;

/**
 * 帮派缓存服务
 * @author xubin
 */
@Service
@Slf4j
public class FactionCacheService {
    private final FactionDao factionDao;
    private RedisCacheService<Faction> cache;

    public FactionCacheService(RedisTemplate<String, Object> redisTemplate, FactionDao factionDao) {
        this.factionDao = factionDao;
        cache = new RedisCacheService.Builder<Faction>()
                .setRedisTemplate(redisTemplate)
                .setDao(factionDao)
                .setCacheName("faction")
                .setEntityClass(Faction.class)
                .build();
    }

    public Faction getFaction(Long id) {
        return cache.getEntity(id);
    }

    public void putFaction(Faction entity) {
        cache.updateEntity(entity);
    }

    public void insertFaction(Faction entity) {
        cache.insertEntity(entity);
    }

    public void removeFaction(Faction entity) {
        cache.removeEntity(entity);
    }

    // 获取所有缓存内的帮派
    public List<Faction> getAllFactions() {
        return cache.getEntityFromSingleList("id");
    }

    // 从数据库中加载所有帮派
    public void loadAllFactions() {
        List<Faction> factions = factionDao.findAll();
        for (Faction faction : factions) {
            cache.cacheEntityIfNotExist(faction);
        }
    }
}
