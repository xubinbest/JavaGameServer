package xbgame.socket.share;

import lombok.extern.slf4j.Slf4j;
import xbgame.commons.thread.NamedThreadFactory;
import xbgame.socket.share.task.BaseGameTask;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class DispatchThreadModel implements ThreadModel {

    private final Worker[] workerPool;

    private static final AtomicBoolean running = new AtomicBoolean(true);

    public DispatchThreadModel() {
        this(Runtime.getRuntime().availableProcessors());
    }

    public DispatchThreadModel(int workCapacity) {
        ThreadFactory threadFactory = new NamedThreadFactory("message-business");
        workerPool = new Worker[workCapacity];
        for (int i = 0; i < workCapacity; i++) {
            Worker w = new Worker();
            workerPool[i] = w;
            threadFactory.newThread(w).start();
        }
    }

    private static class Worker implements Runnable {
        LinkedBlockingQueue<BaseGameTask> taskQueue = new LinkedBlockingQueue<>();

        void receive(BaseGameTask task) {
            taskQueue.add(task);
        }

        @Override
        public void run() {
            while (running.get()) {
                try {
                    BaseGameTask task = taskQueue.take();
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
    }

    @Override
    public void accept(BaseGameTask task) {
        if (task == null) {
            throw new NullPointerException("task is null");
        }
        if (!running.get()) {
            return;
        }
        int distributeKey = (int) (task.getDispatchKey() % workerPool.length);
        workerPool[distributeKey].receive(task);
    }

    @Override
    public void shutDown() {
        running.compareAndSet(true, false);
    }
}
