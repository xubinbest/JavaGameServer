package xbgame.socket.support;

import lombok.extern.slf4j.Slf4j;
import xbgame.socket.share.message.MessageHeader;

import java.nio.ByteBuffer;

@Slf4j
public class DefaultMessageHeader implements MessageHeader {

    public static final int SIZE = 12;

    private int msgLength;

    private int index;

    private int cmd;

    @Override
    public byte[] write() {
        ByteBuffer allocate = ByteBuffer.allocate(SIZE);
        allocate.putInt(msgLength);
        allocate.putInt(index);
        allocate.putInt(cmd);
        byte[] ret = allocate.array();
        allocate.clear();
        return ret;
    }

    @Override
    public void read(byte[] bytes) {
        if (bytes == null || bytes.length != SIZE) {
            throw new IllegalArgumentException("invalid byte array, size must be " + SIZE);
        }

        ByteBuffer allocate = ByteBuffer.wrap(bytes);
        msgLength = allocate.getInt();
        index = allocate.getInt();
        cmd = allocate.getInt();

        allocate.clear();
    }

    @Override
    public int getMessageLength() {
        return msgLength;
    }

    @Override
    public void setMessageLength(int messageLength) {
        this.msgLength = messageLength;

    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;

    }

    @Override
    public int getCmd() {
        return cmd;
    }

    @Override
    public void setCmd(int cmd) {
        this.cmd = cmd;
    }
}
