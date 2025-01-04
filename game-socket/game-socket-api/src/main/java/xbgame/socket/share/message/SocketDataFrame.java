package xbgame.socket.share.message;

import lombok.Getter;
import lombok.Setter;

public class SocketDataFrame {
    @Setter
    @Getter
    private int index;
    @Setter
    @Getter
    private Object message;

    public static SocketDataFrame withIndex(int index, Object message) {
        SocketDataFrame frame = new SocketDataFrame();
        frame.setMessage(message);
        frame.setIndex(index);
        return frame;
    }

    public static SocketDataFrame withoutIndex(Object message) {
        SocketDataFrame frame = new SocketDataFrame();
        frame.setMessage(message);
        return frame;
    }
}
