package xbgame.socket.support;

import xbgame.socket.share.MessageHandlerRegister;
import xbgame.socket.share.message.MessageExecutor;

import java.util.HashMap;
import java.util.Map;

public class DefaultMessageHandlerRegister implements MessageHandlerRegister {

    private Map<Integer, MessageExecutor> cmdHandlers = new HashMap<>();

    @Override
    public void register(int cmd, MessageExecutor executor) {
        cmdHandlers.put(cmd, executor);
    }

    @Override
    public MessageExecutor getMessageExecutor(int cmd) {
        return cmdHandlers.get(cmd);
    }

    @Override
    public void showAllExecutors() {
        for (Map.Entry<Integer, MessageExecutor> entry : cmdHandlers.entrySet()) {
            System.out.println("cmd: " + entry.getKey() + ", executor: " + entry.getValue());
        }
    }
}
