package net.sgztech.timeboat.netty;

import com.imlaidian.utilslibrary.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zbo on 16/7/14.
 */
public class ShutdowNettyClient {
    private static final String TAG = "ShutdowNettyClient";

    private static ShutdowNettyClient shutdown;
    private List<ShutdownListener> listeners = new ArrayList<ShutdownListener>();

    public synchronized static ShutdowNettyClient sharedInstance() {
        if (shutdown == null) {
            shutdown = new ShutdowNettyClient();
            shutdown.addShutdownHook();
        }

        return shutdown;
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                for (ShutdownListener listener : listeners) {
                    listener.shutdown();
                }

                LogUtil.i(TAG,"APP_SHUTDOWN_SUCCESSFULLY!");
            }
        });
    }

    public void addListener(ShutdownListener listener) {
        listeners.add(listener);
    }
}
