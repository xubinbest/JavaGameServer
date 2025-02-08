package org.xubin.game.equip.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.xubin.game.database.game.item.dao.EquipDao;
import org.xubin.game.database.game.item.entity.Equip;
import org.xubin.game.redis.RedisCacheService;

import java.util.List;

/**
 * 装备缓存服务
 * @author xubin
 */
@Service
public class EquipCacheService {
    private final EquipDao equipDao;
    private final RedisCacheService<Equip> cache;

    public EquipCacheService(RedisTemplate<String, Object> redisTemplate, EquipDao equipDao) {
        this.equipDao = equipDao;
        cache = new RedisCacheService.Builder<Equip>()
                .setRedisTemplate(redisTemplate)
                .setDao(equipDao)
                .setCacheName("equip")
                .setEntityClass(Equip.class)
                .build();
    }

    public Equip getEquip(Long id) {
        return cache.getEntity(id);
    }

    public void putEquip(Equip entity) {
        cache.updateEntity(entity);
    }

    public void insertEquip(Equip entity) {
        cache.insertEntity(entity);
    }

    public void removeEquip(Equip entity) {
        cache.removeEntity(entity);
    }

    public List<Equip> listByPlayerId(Long playerId) {
        return cache.getEntityFromMultipleList("playerId", playerId.toString());
    }

    public Equip getEquipByPlayerIdAndSlot(Long playerId, int slot) {
        List<Equip> equips = listByPlayerId(playerId);
        for (Equip equip : equips) {
            if (equip.getSlot() == slot) {
                return equip;
            }
        }
        return null;
    }

    // 从数据库中加载玩家的装备
    public void loadPlayerEquips(Long playerId) {
        List<Equip> equips = equipDao.listByPlayerId(playerId);
        for (Equip equip : equips) {
            cache.cacheEntityIfNotExist(equip);
        }
    }
}
