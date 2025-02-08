package xbgame.socket.client;

import lombok.Getter;
import lombok.Setter;
import xbgame.codec.MessageCodec;
import xbgame.socket.share.HostAndPort;
import xbgame.socket.share.IdSession;
import xbgame.socket.share.SocketIoDispatcher;
import xbgame.socket.share.message.MessageFactory;

public abstract class AbstractSocketClient implements SocketClient {

    @Setter
    @Getter
    protected SocketIoDispatcher ioDispatcher;

    @Setter
    @Getter
    protected MessageFactory messageFactory;

    @Setter
    @Getter
    protected MessageCodec messageCodec;

    @Setter
    @Getter
    protected HostAndPort targetAddress;

    protected IdSession session;

    @Override
    public IdSession getSession() {
        return session;
    }
}
