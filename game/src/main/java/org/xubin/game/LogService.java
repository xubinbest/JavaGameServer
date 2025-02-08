package org.xubin.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xubin.game.database.game.user.entity.Account;
import org.xubin.game.database.game.user.entity.Player;
import org.xubin.game.database.log.dao.NewAccountDao;
import org.xubin.game.database.log.dao.NewPlayerDao;
import org.xubin.game.database.log.entity.NewAccountEnt;
import org.xubin.game.database.log.entity.NewPlayerEnt;

@Service
public class LogService {

    @Autowired
    NewAccountDao newAccountDao;
    // 新建账号日志
    public void logNewAccount(Account account) {
        NewAccountEnt newAccountEnt = new NewAccountEnt();
        newAccountEnt.setAccountId(account.getId());
        newAccountEnt.setAccountName(account.getName());
        newAccountEnt.setCreateTime(System.currentTimeMillis() / 1000);
        newAccountDao.save(newAccountEnt);
    }

    @Autowired
    NewPlayerDao newPlayerDao;
    // 新建玩家日志
    public void logNewPlayer(Player player) {
        NewPlayerEnt newPlayerEnt = new NewPlayerEnt();
        newPlayerEnt.setUserId(player.getId());
        newPlayerEnt.setUserName(player.getName());
        newPlayerEnt.setCreateTime(System.currentTimeMillis() / 1000);
        newPlayerDao.save(newPlayerEnt);
    }
}
