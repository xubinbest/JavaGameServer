package org.xubin.login.server.message.g2l;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Getter;
import lombok.Setter;
import org.xubin.login.server.message.cmd.LoginCmd;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

@Getter
@Setter
@ProtobufClass
@MessageMeta(cmd = LoginCmd.GAME_NODE_REGISTER_G2L)
public class GameNodeRegisterG2L implements Message {
    private int id;
    private String ip;
    private int port;
}
