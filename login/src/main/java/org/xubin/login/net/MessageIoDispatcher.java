package org.xubin.login.net;

import lombok.extern.slf4j.Slf4j;
import xbgame.socket.share.*;
import xbgame.socket.share.annotation.MessageRoute;
import xbgame.socket.share.message.MessageExecutor;
import xbgame.socket.share.message.MessageFactory;
import xbgame.socket.share.message.RequestDataFrame;
import xbgame.socket.share.task.MessageTask;
import xbgame.socket.support.DefaultMessageParameterConverter;
import org.xubin.login.base.GameContext;

import java.util.Map;

@Slf4j
public class MessageIoDispatcher extends ChainedMessageDispatcher {

    private MessageHandlerRegister handlerRegister;

    private MessageParameterConverter msgParameterConverter = new DefaultMessageParameterConverter(GameContext.getMessageFactory());

    private ThreadModel threadModel = new DispatchThreadModel();

    public MessageIoDispatcher() {
        MessageFactory messageFactory = GameContext.getMessageFactory();
        Map<String, Object> messageRoutes = GameContext.getBeansWithAnnotation(MessageRoute.class);

        this.handlerRegister = new CommonMessageHandlerRegister(messageRoutes.values(), messageFactory);

        MessageHandler messageHandler = (session, frame) -> {
            RequestDataFrame dataFrame = (RequestDataFrame) frame;
            Object message = dataFrame.getMessage();
            int cmd = GameContext.getMessageFactory().getMessageId(message.getClass());
            MessageExecutor cmdExecutor = handlerRegister.getMessageExecutor(cmd);
            if (cmdExecutor == null) {
                log.error("message executor missed,  cmd={}", cmd);
                handlerRegister.showAllExecutors();
                return true;
            }
            Object[] params = msgParameterConverter.convertToMethodParams(session, cmdExecutor.getParamTypes(), frame);

            Object controller = cmdExecutor.getHandler();
            MessageTask task = MessageTask.valueOf(session, 0, controller, cmdExecutor.getMethod(), params);
            task.setRequest(message);
            threadModel.accept(task);
            return true;
        };
        addMessageHandler(messageHandler);
    }

    @Override
    public void onSessionCreated(IdSession session) {

    }

    @Override
    public void onSessionClosed(IdSession session) {

    }
}
