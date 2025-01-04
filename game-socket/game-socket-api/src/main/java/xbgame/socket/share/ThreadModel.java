package xbgame.socket.share;

import xbgame.socket.share.task.BaseGameTask;

public interface ThreadModel {

    void accept(BaseGameTask task);

    void shutDown();
}
