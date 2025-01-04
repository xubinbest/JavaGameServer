package xbgame.socket.share.message;

import xbgame.socket.share.annotation.MessageMeta;

public interface Message {

    default short getModule() {
        MessageMeta annotation = getClass().getAnnotation(MessageMeta.class);
        if (annotation != null) {
            return annotation.module();
        }
        return 0;
    }

    default int getCmd() {
        MessageMeta annotation = getClass().getAnnotation(MessageMeta.class);
        if (annotation != null) {
            return annotation.cmd();
        }
        return 0;
    }

    default String key() {
        return this.getModule() + "_" + this.getCmd();
    }
}
