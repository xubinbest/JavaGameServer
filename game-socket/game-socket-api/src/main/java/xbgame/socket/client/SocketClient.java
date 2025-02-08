package xbgame.socket.client;

import xbgame.socket.share.IdSession;

import java.io.IOException;

public interface SocketClient {

    IdSession openSession() throws IOException;

    void close() throws IOException;

    IdSession getSession();
}
