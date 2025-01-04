package org.xubin.game.base;

import org.xubin.game.database.game.BaseEntity;

import java.io.Serializable;

public interface EntityCacheService<E extends BaseEntity<ID>, ID extends Serializable & Comparable<ID>> {
    E getEntity(ID id);
    BaseEntity<ID> putEntity(E entity);

    default BaseEntity<ID> removeEntity(ID id) {
        throw new UnsupportedOperationException();
    }
}
