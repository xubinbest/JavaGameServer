package xbgame.socket.share.message;

import lombok.Getter;
import lombok.Setter;

public class RequestDataFrame {
    @Getter
    @Setter
    private MessageHeader header;

    @Getter
    @Setter
    private Object message;

    public RequestDataFrame(MessageHeader header, Object message) {
        this.header = header;
        this.message = message;
    }
}
