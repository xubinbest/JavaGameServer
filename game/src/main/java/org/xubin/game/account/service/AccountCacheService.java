package org.xubin.game.account.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.xubin.game.database.game.user.dao.AccountDao;
import org.xubin.game.database.game.user.entity.Account;
import org.xubin.game.redis.RedisCacheService;

@Service
public class AccountCacheService {
    private final RedisCacheService<Account> cache;

    public AccountCacheService(RedisTemplate<String, Object> redisTemplate, AccountDao accountDao) {
        cache = new RedisCacheService.Builder<Account>()
                .setRedisTemplate(redisTemplate)
                .setDao(accountDao)
                .setCacheName("account")
                .setEntityClass(Account.class)
                .build();
    }

    public Account getAccount(Long id) {
        return cache.getEntity(id);
    }

    public void putAccount(Account entity) {
        cache.updateEntity(entity);
    }

    public void insertAccount(Account entity) {
        cache.insertEntity(entity);
    }

    public void removeAccount(Account entity) {
        cache.removeEntity(entity);
    }
}
