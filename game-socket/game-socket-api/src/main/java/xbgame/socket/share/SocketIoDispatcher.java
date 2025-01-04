package xbgame.socket.share;

public interface SocketIoDispatcher {
    void onSessionCreated(IdSession session);

    void dispatch(IdSession session, Object frame);

    void onSessionClosed(IdSession session);

    void exceptionCaught(IdSession session, Throwable cause);
}
