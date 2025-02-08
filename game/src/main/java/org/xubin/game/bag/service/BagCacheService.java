package org.xubin.game.bag.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.xubin.game.database.game.item.dao.ItemDao;
import org.xubin.game.database.game.item.entity.Item;
import org.xubin.game.redis.RedisCacheService;

import java.util.List;

/**
 * 背包缓存服务
 * @author xubin
 */
@Service
public class BagCacheService {
    private final ItemDao itemDao;
    private final RedisCacheService<Item> cache;

    public BagCacheService(RedisTemplate<String, Object> redisTemplate, ItemDao itemDao) {
        this.itemDao = itemDao;
        cache = new RedisCacheService.Builder<Item>()
                .setRedisTemplate(redisTemplate)
                .setDao(itemDao)
                .setCacheName("item")
                .setEntityClass(Item.class)
                .build();
    }

    public Item getItem(Long id) {
        return cache.getEntity(id);
    }

    public void putItem(Item entity) {
        cache.updateEntity(entity);
    }

    public void insertItem(Item entity) {
        cache.insertEntity(entity);
    }

    public void removeItem(Item entity) {
        cache.removeEntity(entity);
    }

    public List<Item> listByPlayerId(Long playerId) {
        return cache.getEntityFromMultipleList("playerId", playerId.toString());
    }

    public void loadPlayerItems(Long playerId) {
        List<Item> items = itemDao.listByPlayerId(playerId);
        for (Item item : items) {
            cache.cacheEntityIfNotExist(item);
        }
    }

}
