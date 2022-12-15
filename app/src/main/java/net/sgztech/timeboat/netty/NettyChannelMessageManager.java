package net.sgztech.timeboat.netty;

public class NettyChannelMessageManager {
    private final String TAG = NettyChannelMessageManager.class.getSimpleName();
    private static NettyChannelMessageManager instance;
    private ReceiveCommandListen commandListen ;
    public static NettyChannelMessageManager getInstance() {
        if (instance == null) {
            synchronized (NettyChannelMessageManager.class) {
                if (instance == null) {
                    instance = new NettyChannelMessageManager();
                }
            }
        }
        return instance;
    }

    public synchronized void sendCommandByReceiveFromServer( Object taskModel) {

    }

    public void setReceivedCommandFromServerListen(ReceiveCommandListen listen) {
        commandListen = listen ;
    }
}
