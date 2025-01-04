package xbgame.socket.share;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class ChainedMessageDispatcher implements SocketIoDispatcher {
    protected List<MessageHandler> dispatchChain = new ArrayList<>();

    public void addMessageHandler(MessageHandler handler) {
        this.dispatchChain.add(handler);
    }

    @Override
    public void dispatch(IdSession session, Object message) {
        for (MessageHandler handler : dispatchChain) {
            try {
                if(!handler.messageReceived(session, message)){
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exceptionCaught(IdSession session, Throwable cause) {
//        log.info("", cause);
    }
}
