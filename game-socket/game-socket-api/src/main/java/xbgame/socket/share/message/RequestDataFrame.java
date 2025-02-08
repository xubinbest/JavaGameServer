package xbgame.socket.share.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDataFrame {
    private MessageHeader header;
    private Object message;

    public RequestDataFrame(MessageHeader header, Object message) {
        this.header = header;
        this.message = message;
    }
}
