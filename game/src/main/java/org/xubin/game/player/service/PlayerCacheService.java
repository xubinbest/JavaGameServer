package org.xubin.game.player.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.xubin.game.base.EntityCacheService;
import org.xubin.game.base.GameContext;
import org.xubin.game.database.game.BaseEntity;
import org.xubin.game.database.game.user.entity.PlayerEnt;
import org.xubin.game.database.game.user.dao.PlayerDao;

@Service
public class PlayerCacheService implements EntityCacheService<PlayerEnt, Long> {
    @Autowired
    private PlayerDao playerDao;


    @Override
    @Cacheable(cacheNames = "player")
    public PlayerEnt getEntity(Long id) {
        return playerDao.findById(id).orElse(null);
    }

    @Override
    @CachePut(cacheNames = "player")
    public BaseEntity<Long> putEntity(PlayerEnt entity) {
        GameContext.getAsyncDbService().saveToDb(entity);
        return entity;
    }
}
