package xbgame.socket.share;

public interface MessageHandler {
    boolean messageReceived(IdSession session, Object frame) throws Exception;
}
