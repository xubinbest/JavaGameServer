package org.xubin.game.database.game;

import org.springframework.data.repository.CrudRepository;
import java.io.Serializable;

public interface BaseEntity<ID extends Serializable & Comparable<ID>> {
    ID getId();
    boolean isDelete = false;

    CrudRepository<? extends BaseEntity<ID>, ID> getCrudRepository();

    default String getKey() {
        return getClass().getSimpleName() + "@" + getId().toString();
    }

    default boolean isDelete() {
        return isDelete;
    }
}
