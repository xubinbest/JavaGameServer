package org.xubin.game.net;

import lombok.extern.slf4j.Slf4j;
import org.xubin.game.base.GameContext;
import xbgame.socket.share.*;
import xbgame.socket.share.annotation.MessageRoute;
import xbgame.socket.share.message.MessageExecutor;
import xbgame.socket.share.message.MessageFactory;
import xbgame.socket.share.message.RequestDataFrame;
import xbgame.socket.share.task.MessageTask;
import xbgame.socket.support.DefaultMessageParameterConverter;

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
//            if (message instanceof BattleMessage) {
//                if (GameContext.serverType == ServerType.GAME) {
//                    PlayerEnt player = (PlayerEnt) session.getAttribute("PLAYER");
//                    BattleMessage battleMessage = (BattleMessage) message;
//                    int battleSid = battleMessage.getBattleServerId(player);
//                    if (battleSid > 0) {
//                        IdSession crossSession = GameContext.getRpcClientRouter().getSession(battleSid);
//                        if (crossSession == null) {
//                            log.error("fight server is unreachable", cmd);
//                            return false;
//                        }
//                        crossSession.send(battleMessage);
//                        return false;
//                    }
//                }
//            }
            Object[] params = msgParameterConverter.convertToMethodParams(session, cmdExecutor.getParamTypes(), frame);

            Object controller = cmdExecutor.getHandler();

            Object playerEnt = session.getAttribute("PLAYER");

            long dispatchKey = 0;
            if (playerEnt != null) {
//                dispatchKey = ((PlayerEnt) playerEnt).dispatchKey();
                // TODO: 临时屏蔽
            }
            MessageTask task = MessageTask.valueOf(session, dispatchKey, controller, cmdExecutor.getMethod(), params);
            task.setRequest(message);
            // 丢到任务消息队列，不在io线程进行业务处理
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
