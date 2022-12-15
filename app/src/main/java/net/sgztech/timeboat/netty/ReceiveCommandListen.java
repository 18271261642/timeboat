package net.sgztech.timeboat.netty;

public interface ReceiveCommandListen {
    void receivedMsgFromServer(Object commandModel);
}
