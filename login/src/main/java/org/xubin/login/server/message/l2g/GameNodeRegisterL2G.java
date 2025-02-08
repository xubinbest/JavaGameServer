package org.xubin.login.server.message.l2g;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import org.xubin.login.server.message.cmd.LoginCmd;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

@ProtobufClass
@MessageMeta(cmd = LoginCmd.GAME_NODE_REGISTER_L2G)
public class GameNodeRegisterL2G implements Message {
}
