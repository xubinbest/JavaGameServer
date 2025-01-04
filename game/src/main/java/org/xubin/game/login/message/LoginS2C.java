package org.xubin.game.login.message;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Getter;
import lombok.Setter;
import org.xubin.game.login.message.vo.PlayerLoginVo;
import xbgame.socket.share.annotation.MessageMeta;
import xbgame.socket.share.message.Message;

import java.util.List;

@Getter
@Setter
@ProtobufClass
@MessageMeta(cmd = 2)
public class LoginS2C implements Message {
    @Protobuf
    private int code;
    @Protobuf
    public List<PlayerLoginVo> players;
}
