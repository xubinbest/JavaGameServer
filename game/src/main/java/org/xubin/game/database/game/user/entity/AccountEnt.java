package org.xubin.game.database.game.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Proxy;
import org.springframework.data.repository.CrudRepository;
import org.xubin.game.base.GameContext;
import org.xubin.game.database.game.BaseEntity;
import org.xubin.game.database.game.user.dao.AccountDao;

@Entity
@Table(name="accountent")
@Proxy(lazy = false)
@Data
public class AccountEnt implements BaseEntity<Long> {
    @Id
    @Column
    private Long id;

    @Column
    private String name;

    @Override
    public CrudRepository<AccountEnt, Long> getCrudRepository() {
        return GameContext.getBean(AccountDao.class);
    }
}
