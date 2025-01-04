package org.xubin.game.account.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xubin.game.LogService;
import org.xubin.game.base.EntityCacheService;
import org.xubin.game.base.GameContext;
import org.xubin.game.database.game.user.dao.AccountDao;
import org.xubin.game.database.game.user.entity.AccountEnt;

@Service
public class AccountService implements EntityCacheService<AccountEnt, Long> {

    @Autowired
    private AccountDao accountDao;

    public AccountEnt createNew(AccountEnt accountEnt) {
        GameContext.getLogService().logNewAccount(accountEnt);
        return putEntity(accountEnt);
    }

    @Override
    public AccountEnt getEntity(Long id) {
        return accountDao.findById(id).orElse(null);
    }

    @Override
    public AccountEnt putEntity(AccountEnt entity) {
        GameContext.getPlayerService().addAccountProfile(entity);
        GameContext.getAsyncDbService().saveToDb(entity);
        return entity;
    }
}
