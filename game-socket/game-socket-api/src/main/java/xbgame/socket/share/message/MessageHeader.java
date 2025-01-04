package xbgame.socket.share.message;

public interface MessageHeader {
    byte[] write();
    void read(byte[] bytes);

    int getMessageLength();
    void setMessageLength(int messageLength);

    int getIndex();
    void setIndex(int index);

    int getCmd();
    void setCmd(int cmd);
}
