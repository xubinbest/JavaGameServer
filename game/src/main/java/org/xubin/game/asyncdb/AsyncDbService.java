package org.xubin.game.asyncdb;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xubin.game.base.GameContext;
import org.xubin.game.config.ServerType;
import org.xubin.game.database.game.BaseEntity;
import org.xubin.game.database.game.user.entity.PlayerEnt;
import xbgame.commons.ds.ConcurrentHashSet;
import xbgame.commons.thread.NamedThreadFactory;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class AsyncDbService {
    private final AtomicBoolean run = new AtomicBoolean(true);

    private final Worker playerWorker = new Worker("player");
    private final Worker commonWorker = new Worker("common");

    @PostConstruct
    public void init() {
        new NamedThreadFactory("player-save-service").newThread(playerWorker).start();
        new NamedThreadFactory("common-save-service").newThread(commonWorker).start();
    }

    public void saveToDb(BaseEntity<? extends Serializable> entity) {
        if(GameContext.serverType != ServerType.GAME) {
            return;
        }

        if(entity instanceof PlayerEnt) {
            playerWorker.add2Queue(entity);
        } else {
            commonWorker.add2Queue(entity);
        }
    }

    private class Worker implements  Runnable {

        private String name;
        private BlockingQueue<BaseEntity> queue = new LinkedBlockingDeque<BaseEntity>();
        private Set<String> savingQueue = new ConcurrentHashSet<String>();

        public void add2Queue(BaseEntity entity) {
            if (savingQueue.contains(entity.getKey())) {
                return;
            }
            queue.add(entity);
            savingQueue.add(entity.getKey());
        }

        public Worker(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            while(run.get()) {
                BaseEntity entity = null;
                try {
                     entity = queue.take();
                     save(entity);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    add2Queue(entity);
                }
            }
        }

        private void save(BaseEntity entity) {
            try {
                if(entity.isDelete()) {
                    entity.getCrudRepository().delete(entity);
                } else {
                    entity.getCrudRepository().save(entity);
                }
                savingQueue.remove(entity.getKey());
            } catch (Exception e) {
                e.printStackTrace();;
                add2Queue(entity);
            }
        }

        private void saveAll() {
            while(!queue.isEmpty()) {
                Iterator it = queue.iterator();
                while (it.hasNext()) {
                    BaseEntity entity = (BaseEntity) it.next();
                    it.remove();
                    save(entity);
                }
            }
        }

        public void showDown() {
            while(true) {
                if(queue.isEmpty()) {
                    break;
                }
                saveAll();
            }
            log.info("save service {} shutdown", name);
        }
    }

    public void shutDown() {
        run.getAndSet(false);
        playerWorker.showDown();
        commonWorker.showDown();
    }
}
