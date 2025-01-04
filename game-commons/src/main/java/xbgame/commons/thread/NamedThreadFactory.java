package xbgame.commons.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

    private ThreadGroup threadGroup;
    private final String groupName;
    private final boolean daemon;
    private final AtomicInteger idGen = new AtomicInteger(1);

    public NamedThreadFactory(String group) {
        this(group, false);
    }

    public NamedThreadFactory(String group, boolean daemon) {
        this.groupName = group;
        this.daemon = daemon;
        threadGroup = Thread.currentThread().getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        String name = getNextThreadName();
        Thread t = new Thread(threadGroup, r, name, 0);
        t.setDaemon(daemon);
        if(t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }

    public String getNextThreadName() {
        return this.groupName + "-thread-" + this.idGen.getAndIncrement();
    }
}
