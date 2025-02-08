package org.xubin.game.faction.cache;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.xubin.game.database.game.faction.dao.FactionApplyDao;
import org.xubin.game.database.game.faction.entity.FactionApply;
import org.xubin.game.redis.RedisCacheService;

import java.util.List;

/**
 * 帮派申请缓存服务
 * @author xubin
 */
@Service
public class FactionApplyCacheService {
    private final  FactionApplyDao factionApplyDao;
    private final RedisCacheService<FactionApply> cache;

    public FactionApplyCacheService(RedisTemplate<String, Object> redisTemplate, FactionApplyDao factionApplyDao) {
        this.factionApplyDao = factionApplyDao;
        cache = new RedisCacheService.Builder<FactionApply>()
                .setRedisTemplate(redisTemplate)
                .setDao(factionApplyDao)
                .setCacheName("faction_apply")
                .setEntityClass(FactionApply.class)
                .build();
    }

    public void insertFactionApply(FactionApply factionApply) {
        cache.insertEntity(factionApply);
    }

    public FactionApply getFactionApplyById(Long id) {
        return cache.getEntity(id);
    }

    public void removeFactionApply(FactionApply factionApply) {
        cache.removeEntity(factionApply);
    }

    public List<FactionApply> listByFactionId(Long factionId) {
        return cache.getEntityFromMultipleList("factionId", factionId.toString());
    }

    public void loadAllFactionApplies() {
        List<FactionApply> factionApplies = factionApplyDao.findAll();
        for (FactionApply factionApply : factionApplies) {
            cache.cacheEntityIfNotExist(factionApply);
        }
    }

}
