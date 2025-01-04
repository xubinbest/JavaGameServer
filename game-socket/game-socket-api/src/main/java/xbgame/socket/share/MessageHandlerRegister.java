package xbgame.socket.share;

import xbgame.socket.share.message.MessageExecutor;

public interface MessageHandlerRegister {

    void register(int cmd, MessageExecutor executor);

    MessageExecutor getMessageExecutor(int cmd);

    void showAllExecutors();
}
