package org.xubin.game.faction.cache;

import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.xubin.game.database.game.faction.dao.FactionMemberDao;
import org.xubin.game.database.game.faction.entity.FactionMember;
import org.xubin.game.redis.RedisCacheService;

import java.util.List;

/**
 * 帮派成员缓存服务
 * @author xubin
 */
@Service
public class FactionMemberCacheService {
    private final FactionMemberDao factionMemberDao;

    private final RedisCacheService<FactionMember> cache;

    public FactionMemberCacheService(RedisTemplate<String, Object> redisTemplate, FactionMemberDao factionMemberDao) {
        this.factionMemberDao = factionMemberDao;
        cache = new RedisCacheService.Builder<FactionMember>()
                .setRedisTemplate(redisTemplate)
                .setDao(factionMemberDao)
                .setCacheName("faction_member")
                .setEntityClass(FactionMember.class)
                .build();
    }

    public FactionMember getFactionMember(Long memberId) {
        return cache.getEntity(memberId);
    }

    public void putFactionMember(FactionMember factionMember) {
        cache.updateEntity(factionMember);
    }

    public void insertFactionMember(FactionMember factionMember) {
        cache.insertEntity(factionMember);
    }

    public void removeFactionMember(FactionMember factionMember) {
        cache.removeEntity(factionMember);
    }

    public List<FactionMember> listByFactionId(Long factionId) {
        return cache.getEntityFromMultipleList("factionId", factionId.toString());
    }

    public List<Long> idListByFactionId(Long factionId) {
        return cache.getIdsFromMultipleList("factionId", factionId.toString());
    }

    public void loadAllFactionMembers() {
        List<FactionMember> factionMembers = factionMemberDao.findAll();
        for (FactionMember factionMember : factionMembers) {
            cache.cacheEntityIfNotExist(factionMember);
        }
    }
}
