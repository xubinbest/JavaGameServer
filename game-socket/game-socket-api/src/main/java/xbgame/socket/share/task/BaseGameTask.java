package xbgame.socket.share.task;

import lombok.Getter;
import lombok.Setter;

public abstract class BaseGameTask implements Runnable {
    @Getter
    private long startTime;

    @Getter
    private long endTime;

    @Getter
    @Setter
    protected long dispatchKey;

    public abstract void action();

    @Override
    public void run() {
        this.startTime = System.currentTimeMillis();
        action();
        this.endTime = System.currentTimeMillis();
    }

    public String getName() {
        return getClass().getSimpleName();
    }
}
