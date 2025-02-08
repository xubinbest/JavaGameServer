package org.xubin.game.base;

import org.springframework.stereotype.Component;
import org.xubin.game.database.game.user.entity.Player;
import xbgame.commons.NumberUtil;
import xbgame.socket.share.IdSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class SessionManager {
    private ConcurrentMap<Long, IdSession> player2sessions = new ConcurrentHashMap<>();

    public void registerNewPlayer(Player player, IdSession session) {
        session.setAttribute(IdSession.ID, player.getId());
        session.setAttribute("PLAYER", player);
        this.player2sessions.put(player.getId(), session);
    }

    public long getPlayerIdBy(IdSession session) {
        if(session != null) {
            return NumberUtil.longValue(session.getId());
        }
        return 0;
    }

    public IdSession getSessionBy(long playerId) {
        return player2sessions.get(playerId);
    }

}
