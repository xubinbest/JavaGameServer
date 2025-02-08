package org.xubin.game.account.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xubin.game.database.game.user.entity.Account;

/**
 * 账号服务
 * @author xubin
 */
@Service
public class AccountService {
    @Autowired
    private AccountCacheService accountCache;

    public void createAccount(long accountId) {
        Account account = new Account();
        account.setId(accountId);
        accountCache.insertAccount(account);
    }

    public Account getAccount(Long id) {
        return accountCache.getAccount(id);
    }

    public void putAccount(Account entity) {
        accountCache.putAccount(entity);
    }

}
