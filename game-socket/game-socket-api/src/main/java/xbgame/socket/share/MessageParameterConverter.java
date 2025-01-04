package xbgame.socket.share;

public interface MessageParameterConverter {
    Object[] convertToMethodParams(IdSession session, Class<?>[] methodParams, Object message);
}
