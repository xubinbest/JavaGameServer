package org.xubin.login.server.message.g2l;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Data;
import org.xubin.login.server.message.cmd.LoginCmd;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

@Data
@ProtobufClass
@MessageMeta(cmd = LoginCmd.UPDATE_PLAYER_LAST_LOGIN_SERVER_G2L)
public class UpdatePlayerLastLoginServerG2L implements Message {
    private long accountId;
    private int serverId;
}
