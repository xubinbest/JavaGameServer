package xbgame.socket.share.task;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import xbgame.socket.share.IdSession;

import java.lang.reflect.Method;

@Slf4j
public class MessageTask extends BaseGameTask {

    private IdSession session;

    @Setter
    private int msgIndex;
    @Getter
    private Object handler;
    @Getter
    private Method method;
    @Getter
    private Object[] params;

    @Setter
    private Object request;

    public static MessageTask valueOf(IdSession session, long dispatchKey, Object handler,
                                      Method method, Object[] params) {
        MessageTask msgTask = new MessageTask();
        msgTask.dispatchKey = dispatchKey;
        msgTask.session = session;
        msgTask.handler = handler;
        msgTask.method = method;
        msgTask.params = params;

        return msgTask;
    }

    @Override
    public void action() {
        try {
            Object response = method.invoke(handler, params);
            // 如果消息签名带有返回值，则无需index字段，这里会自动把index附加到返回值
            // index字段只用于异步推送才是必填字段
            if (response != null) {
                // 消息处理器包含消息序号，则下发响应将其带上
                session.send(msgIndex, response);
            }
        } catch (Exception e) {
            log.error("message task execute failed ", e);
        }
    }

    @Override
    public String toString() {
        return "[" + handler.getClass().getName() + "@" + method.getName() + "]";
    }
}
