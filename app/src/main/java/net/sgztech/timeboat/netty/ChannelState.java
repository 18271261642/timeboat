package net.sgztech.timeboat.netty;

/**
 * Created by zbo on 16/7/14.
 */

/*
* DISCONNCET  机器net断网
* RUNNING netty 正在启动
* connecting netty 正在连接着
* FETCHING  通道 正在发送命令 此时不能 再发送
* CLOSING netty 正在关闭 机器时用
* CLOSED 通道已经关闭了
* */
public enum ChannelState {
    RUNNING(1),
    CLOSED(2),
    CONNECTING(3),
    CLOSING(4),
    RESTARTING(5),
    FETCHING(6),
    DISCONNECT(7);

    private int value;

    private ChannelState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
