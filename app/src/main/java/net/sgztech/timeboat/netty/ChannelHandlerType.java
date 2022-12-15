package net.sgztech.timeboat.netty;

/**
 * Created by zbo on 16/7/20.
 */
public enum ChannelHandlerType {
    loginChannel(0),
    heartBeatChannel(1),
    bleCmdChannel(3);

    private int value;

    private ChannelHandlerType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ChannelHandlerType create(int status) {
        if (status == loginChannel.getValue()) {
            //机器状态
            return loginChannel;
        } else if (status == heartBeatChannel.getValue()) {
            //心跳类型
            return heartBeatChannel;
        }  else {
            return null;
        }
    }
}
