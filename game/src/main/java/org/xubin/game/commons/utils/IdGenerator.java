package org.xubin.game.commons.utils;

import org.xubin.game.base.GameContext;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private static AtomicInteger idGenerator = new AtomicInteger(0);

    public static long nextId() {
        long serverId = GameContext.getServerConfig().getServerId();
        //----------------id格式 -------------------------
        //----------long类型8个字节64个比特位----------------
        // 高16位          	| 中32位          |  低16位
        // serverId        系统秒数          自增长号
        return  (serverId << 48) | (((System.currentTimeMillis() / 1000)) << 16) | (idGenerator.getAndIncrement() & 0xFFFF);
    }
}
