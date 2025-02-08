package org.xubin.game.redis;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.xubin.game.database.game.BaseEntity;
import xbgame.commons.thread.NamedThreadFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Redis 缓存服务
 * @author xubin
 */
@Slf4j
public class RedisCacheService<T extends BaseEntity<?>> {
    private static final String NOT_EXIST = "not exist";

    private final Worker worker;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JpaRepository<T, Long> dao;
    private final String cacheName;
    private final Class<?> entityClass;


    public RedisCacheService(Builder builder) {
        this.redisTemplate = builder.redisTemplate;
        this.dao = builder.dao;
        this.cacheName = builder.cacheName;
        this.entityClass = builder.entityClass;
        worker = new Worker(builder.saveDbInterval);
        startSaveDbThread();
    }

    /**
     * 获取实体缓存，如果不存在则尝试从数据库加载
     * 如果数据库中不存在，则缓存 NOT_EXIST, 避免缓存穿透
     *
     * @param entityId  实体 ID
     * @return 实体
     */
    public T getEntity(Long entityId) {
        String redisKey = generateKey(entityId);
        Map<Object, Object> entityMap = redisTemplate.opsForHash().entries(redisKey);

        if (!entityMap.isEmpty()) {
            Object data = entityMap.get("data");
            if (NOT_EXIST.equals(data)) {
                return null;
            }
            return (T) entityClass.cast(entityMap.get("data"));
        }
        return loadAndCacheEntityById(entityId);
    }

    /**
     * 更新实体缓存, 并标记为脏数据
     * 定时任务会定时将脏数据写入数据库
     *
     * @param entity    实体
     */
    public void updateEntity(T entity) {
        String redisKey = generateKey(entity.getId().toString());
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("data", entity);
        entityMap.put("dirty", true);
        redisTemplate.opsForHash().putAll(redisKey, entityMap);
        worker.addDirtyEntity(entity);
    }

    public void cacheEntityIfNotExist(T entity) {
        // 避免新增索引时检查实体已存在，不会创建新索引
        addCacheList(entity);

        String redisKey = generateKey(entity.getId().toString());
        Map<Object, Object> entityMap = redisTemplate.opsForHash().entries(redisKey);
        if (!entityMap.isEmpty()) {
            Object data = entityMap.get("data");
            if (!NOT_EXIST.equals(data)) {
                return;
            }
        }
        cacheEntity(entity);
    }

    /**
     * 插入实体缓存, 并添加到列表缓存
     *
     * @param entity    实体
     */
    public void insertEntity(T entity) {
        updateEntity(entity);
        addCacheList(entity);
    }

    /**
     * 删除实体缓存,并删除列表缓存
     *
     * @param entity    实体 ID
     */
    public void removeEntity(T entity) {
        String redisKey = generateKey(entity.getId().toString());
        redisTemplate.delete(redisKey);
        deleteCacheList(entity);
        worker.addDeleteEntity(entity);
    }

    /**
     * 获取索引缓存
     *
     * @param fieldKey  索引字段名
     * @return List<Long> 实体 ID 列表
     */
    public List<Long> getIdsFromSingleList(String fieldKey) {
        String listKey = genSingleListKey(fieldKey);
        return Objects.requireNonNull(redisTemplate.opsForSet().members(listKey))
                .stream()
                .map(id -> Long.parseLong(id.toString())).
                toList();
    }

    /**
     * 从列表缓存中获取实体
     *
     * @param fieldKey  索引字段名
     * @return List<T> 实体列表
     */
    public List<T> getEntityFromSingleList(String fieldKey) {
        List<Long> ids = getIdsFromSingleList(fieldKey);
        return ids.stream().map(id -> getEntity(id)).toList();
    }

    /**
     * 获取索引缓存
     *
     * @param fieldKey   索引字段名
     * @param fieldValue 索引字段值
     * @return List<Long> 实体 ID 列表
     */
    public List<Long> getIdsFromMultipleList(String fieldKey, String fieldValue) {
        String listKey = generateListKey(fieldKey, fieldValue);
        return Objects.requireNonNull(redisTemplate.opsForSet().members(listKey))
                .stream()
                .map(id -> Long.parseLong(id.toString()))
                .collect(Collectors.toList());
    }

    /**
     * 从列表缓存中获取实体
     *
     * @param fieldKey   索引字段名
     * @param fieldValue 索引字段值
     * @return List<T> 实体列表
     */
    public List<T> getEntityFromMultipleList(String fieldKey, String fieldValue) {
        List<Long> ids = getIdsFromMultipleList(fieldKey, fieldValue);
        return ids.stream().map(id -> getEntity(id)).toList();
    }

    // 从数据库中加载并缓存
    private T loadAndCacheEntityById(Long entityId) {
        T entity = dao.findById(entityId).orElse(null);
        if (entity != null) {
            cacheEntity(entity);
            addCacheList(entity);
        } else {
            cacheEntityNotExist(entityId);
        }

        return entity;
    }

    // 按需求添加到列表缓存(类似索引使用)
    private void addCacheList(T entity) {
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            CacheIndex cacheIndex = field.getAnnotation(CacheIndex.class);
            if (cacheIndex != null) {
                String fieldName = field.getName();
                switch (cacheIndex.type()) {
                    case SINGLE:
                        addToSingleList(fieldName, entity.getId());
                        break;
                    case MULTIPLE:
                        try {
                            field.setAccessible(true);
                            String fileValue = field.get(entity).toString();
                            addToMultipleList(fieldName, fileValue, entity.getId());
                        } catch (Exception e) {
                            log.error("Failed to add entity to list for entity: {}", entity.getId(), e);
                        }
                        break;
                    default:
                        logUnknownCacheIndexType(cacheIndex.type());
                        break;
                }
            }
        }
    }

    private void logUnknownCacheIndexType(CacheIndexType type) {
        log.error("Unknown cache index type: {}", type);
    }

    // 按需求删除列表缓存
    private void deleteCacheList(T entity) {
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            CacheIndex cacheIndex = field.getAnnotation(CacheIndex.class);
            if (cacheIndex != null) {
                String fieldName = field.getName();
                switch (cacheIndex.type()) {
                    case SINGLE:
                        removeFromSingleList(fieldName, entity.getId());
                        break;
                    case MULTIPLE:
                        try {
                            field.setAccessible(true);
                            String fieldValue = field.get(entity).toString();
                            removeFromMultipleList(fieldName, fieldValue, entity.getId().toString());
                        } catch (Exception e) {
                            log.error("Failed to delete list for entity: {}", entity.getId(), e);
                        }
                        break;
                    default:
                        logUnknownCacheIndexType(cacheIndex.type());
                        break;
                }
            }
        }
    }

    // 添加实体 ID 到列表
    private void addToMultipleList(String fieldKey, String fieldValue, Serializable entityId) {
        String listKey = generateListKey(fieldKey, fieldValue);
        redisTemplate.opsForSet().add(listKey, entityId);
    }

    private void addToSingleList(String fileKey, Serializable entityId) {
        String listKey = genSingleListKey(fileKey);
        redisTemplate.opsForSet().add(listKey, entityId);
    }

    // 从列表中移除实体 ID
    private void removeFromMultipleList(String fieldKey, String fieldValue, String entityId) {
        String listKey = generateListKey(fieldKey, fieldValue);
        redisTemplate.opsForSet().remove(listKey, 1, entityId);
    }

    private void removeFromSingleList(String fieldKey, Serializable entityId) {
        String listKey = genSingleListKey(fieldKey);
        redisTemplate.opsForSet().remove(listKey, 1, entityId);
    }

    // 从数据库中加载失败，缓存 NOT_EXIST
    private void cacheEntityNotExist(Long entityId) {
        String redisKey = generateKey(entityId);
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("data", NOT_EXIST);
        redisTemplate.opsForHash().putAll(redisKey, entityMap);
    }

    //缓存实体到 Redis, 并标记为非脏数据
    //避免从数据库加载的数据再次写入数据库,后续更新时会标记为脏数据
    private void cacheEntity(T entity) {
        String redisKey = generateKey(entity.getId().toString());
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("data", entity);
        entityMap.put("dirty", false);
        redisTemplate.opsForHash().putAll(redisKey, entityMap);
    }

    // 生成 Redis 键：单个实体
    private String generateKey(Long entityId) {
        return cacheName + ":" + entityId.toString();
    }

    private String generateKey(String entityId) {
        return cacheName + ":" + entityId;
    }

    // 生成 Redis 键：实体列表
    private String generateListKey(String fieldKey, String fieldValue) {
        return cacheName + "_" + fieldKey + "_list:" + fieldValue;
    }

    private String genSingleListKey(String fieldKey) {
        return cacheName + "_" + fieldKey + "_list";
    }

    // 开启保存数据库线程
    private void startSaveDbThread() {
        new NamedThreadFactory(cacheName + "_save_db_worker").newThread(worker).start();
    }

    // 保存数据库线程
    private class Worker implements Runnable {
        private final AtomicBoolean run = new AtomicBoolean(true);
        private final Map<Long, BaseEntity<?>> dirtyEntities = new ConcurrentHashMap<>();
        private final Set<Long> deleteEntities = ConcurrentHashMap.newKeySet();
        // 每分钟检查一次写库
        private static final int DEFAULT_SAVE_INTERVAL = 6000;
        private final int saveInterval;

        public Worker(int saveIntervalOffset) {
            this.saveInterval = DEFAULT_SAVE_INTERVAL + saveIntervalOffset;
        }

        public void shutDown() {
            run.set(false);
            saveAll();
        }

        // 永远缓存最新数据
        public void addDirtyEntity(BaseEntity entity) {
            dirtyEntities.put((long) entity.getId(), entity);
        }

        // 删除数据时要从脏数据中移除
        public void addDeleteEntity(BaseEntity entity) {
            Long id = (long) entity.getId();
            dirtyEntities.remove(id);
            deleteEntities.add(id);
        }

        @Override
        public void run() {
            while (run.get()) {
                if (dirtyEntities.isEmpty() && deleteEntities.isEmpty()) {
                    try {
                        Thread.sleep(saveInterval);
                    } catch (InterruptedException e) {
                        log.error("Save db worker interrupted", e);
                    }
                } else {
                    saveAll();
                }
            }
        }

        private void saveAll() {
            // 处理脏数据（写入数据库）
            if (!dirtyEntities.isEmpty()) {
                log.debug("table {} Save dirty entities to the database. count {}.", Thread.currentThread().getName(), dirtyEntities.size());
                Collection<T> allEntities = new ArrayList<>((Collection<T>) dirtyEntities.values());
                try {
                    dao.saveAllAndFlush(allEntities);
                    dirtyEntities.clear();
                } catch (Exception e) {
                    log.error("Failed to save dirty entities to the database.", e);
                }
            }

            // 处理删除数据
            if (!deleteEntities.isEmpty()) {
                log.debug("table {} Delete entities from the database. count {}.", Thread.currentThread().getName(), deleteEntities.size());
                dao.deleteAllByIdInBatch(deleteEntities);
                deleteEntities.clear();
            }
        }
    }

    @PreDestroy
    public void shutDown() {
        worker.shutDown();
    }

    /**
     * 构造器
     *
     * @param <T> 实体类型
     */
    public static class Builder <T extends BaseEntity<?>> {
        private RedisTemplate<String, Object> redisTemplate;
        private JpaRepository<T, Long> dao;
        private String cacheName;
        private Class<T> entityClass;
        private int saveDbInterval;

        public Builder<T> setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
            this.redisTemplate = redisTemplate;
            return this;
        }

        public Builder<T> setDao(JpaRepository<T, Long> dao) {
            this.dao = dao;
            return this;
        }

        public Builder<T> setCacheName(String cacheName) {
            this.cacheName = cacheName;
            return this;
        }

        public Builder<T> setEntityClass(Class<T> entityClass) {
            this.entityClass = entityClass;
            return this;
        }

        public Builder<T> setSaveDbInterval(int saveDbInterval) {
            this.saveDbInterval = saveDbInterval;
            return this;
        }

        public RedisCacheService<T> build() {
            if (redisTemplate == null) {
                throw new IllegalArgumentException("redisTemplate is null");
            }

            if (dao == null) {
                throw new IllegalArgumentException("dao is null");
            }

            if (cacheName == null) {
                throw new IllegalArgumentException("cacheName is null");
            }

            if (entityClass == null) {
                throw new IllegalArgumentException("entityClass is null");
            }

            if (saveDbInterval <= 0) {
                saveDbInterval = 1000;
            }
            return new RedisCacheService<T>(this);
        }
    }
}
