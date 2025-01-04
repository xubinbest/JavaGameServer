package xbgame.socket.support;

import xbgame.socket.share.IdSession;
import xbgame.socket.share.MessageParameterConverter;
import xbgame.socket.share.message.MessageFactory;
import xbgame.socket.share.message.RequestDataFrame;

public class DefaultMessageParameterConverter implements MessageParameterConverter {

    private MessageFactory messageFactory;

    public DefaultMessageParameterConverter(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    @Override
    public Object[] convertToMethodParams(IdSession session, Class<?>[] methodParams, Object frame) {
        RequestDataFrame dataFrame = (RequestDataFrame)frame;
        Object message = dataFrame.getMessage();
        Object[] result = new Object[methodParams == null ? 0 : methodParams.length];
        if(result.length == 0) {
            return result;
        }
        // 方法签名
        //          如果有两个参数，则为  method(IdSession session, Object message);
        //          如果有三个参数，则为  method(IdSession session, int index, Object message);

        // 第一个参数必须是IdSession
        if (IdSession.class.isAssignableFrom(methodParams[0])) {
            result[0] = session;
        } else {
            throw new IllegalArgumentException("message handler 1st argument must be IdSession");
        }

        // 如果有两个参数，则第二个参数必须是Message
        if(result.length == 2) {
            if (messageFactory.contains(message.getClass())) {
                result[1] = message;
            } else {
                throw new IllegalArgumentException("message handler 2nd argument must be registered Message");
            }
        } else if(result.length == 3) {
            // 如果有三个参数，则第二个参数必须是int，第三个参数必须是Message
            if (int.class.isAssignableFrom(methodParams[1])) {
                result[1] = dataFrame.getHeader().getIndex();
            } else {
                throw new IllegalArgumentException("2nd argument must be int");
            }
            if (messageFactory.contains(message.getClass())) {
                result[2] = message;
            } else {
                throw new IllegalArgumentException("message handler 3nd argument must be registered Message");
            }
        }
        return result;
    }
}
