
package com.imlaidian.ldclog;

import android.util.Log;

import java.util.List;

public class LdLog {
    private static OnLdLogProtocolStatus mLdLogProtocolStatus;
    private static LdLogControlCenter sLdLogControlCenter;
    static boolean sDebug = false;

    /**
     * Log初始化
     */
    public static void init(LdLogConfig ldLogConfig) {
        sLdLogControlCenter = LdLogControlCenter.instance(ldLogConfig);
    }

    /**
     * * @param tag  表示日志标识
     * @param log  表示日志内容
     * @brief Log写入日志
     */
    public static void r(String tag, String log ,String eventType) {
        if (sLdLogControlCenter == null) {
            Log.d("LdLog" ,"Please initialize Log first");
            return;
        }
        sLdLogControlCenter.write(tag ,log, "L6",eventType);
    }

    public static void i(String tag, String log ) {
        if (sLdLogControlCenter == null) {
            Log.d("LdLog" ,"Please initialize Log first");
            return;
        }
        sLdLogControlCenter.write(tag ,log, "L5");
    }

    public static void v(String tag, String log) {
        if (sLdLogControlCenter == null) {
            Log.d("LdLog" ,"Please initialize Log first");
            return;
        }
        sLdLogControlCenter.write(tag ,log, "L4");
    }

    public static void d(String tag, String log) {
        if (sLdLogControlCenter == null) {
            Log.d("LdLog" ,"Please initialize Log first");
            return;
        }
        sLdLogControlCenter.write(tag ,log, "L3");
    }

    public static void w(String tag, String log) {
        if (sLdLogControlCenter == null) {
            Log.d("LdLog" ,"Please initialize Log first");
            return;
        }
        sLdLogControlCenter.write(tag ,log, "L2");
    }

    public static void e(String tag, String log) {
        if (sLdLogControlCenter == null) {
            Log.d("LdLog" ,"Please initialize Log first");
            return;
        }
        sLdLogControlCenter.write(tag ,log, "L1");
    }



    /**
     *  立即写入日志文件
     */
    public static void f() {
        if (sLdLogControlCenter == null) {
            Log.d("LdLog" ,"Please initialize Log first");
            return;
        }
        sLdLogControlCenter.flush();
    }

    public static void stop(){

        if (sLdLogControlCenter == null) {
            Log.d("LdLog" ,"Please initialize Log first");
            return;
        }
        sLdLogControlCenter.stop();
    }

    /**
     * @param uploadList  需要上传的文件列表 分不同文件类型 UploadFileType  日志文件日期格式：“2020-10-27”
     * @param runnable 发送操作
     * @brief 发送日志
     */
    public static void upload(List<LdUploadItem> uploadList, SendLogRunnable runnable) {
        if (sLdLogControlCenter == null) {
            Log.d("LdLog" ,"Please initialize Log first");
            return;
        }
        sLdLogControlCenter.send(uploadList, runnable);
    }

    /**
     *  Log Debug开关
     */
    public static void setDebug(boolean debug) {
        LdLog.sDebug = debug;
    }

    static void onListenerLogWriteStatus(String name, int status) {
        if (mLdLogProtocolStatus != null) {
            mLdLogProtocolStatus.logProtocolStatus(name, status);
        }
    }

    public static void setOnLdLogProtocolStatus(OnLdLogProtocolStatus listener) {
        mLdLogProtocolStatus = listener;
    }
}
