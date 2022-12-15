
package com.imlaidian.ldclog;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class LdCLogProtocol implements LdLogProtocolHandler {

    private static final String LIBRARY_NAME = "ldCLog";

    private static LdCLogProtocol sLdCLogProtocol;
    private static boolean mIsLdClogOk;

    private boolean mIsLdLogInit;
    private boolean mIsLdLogOpen;
    private OnLdLogProtocolStatus mLdLogProtocolStatus;
    private Set<Integer> mArraySet = Collections.synchronizedSet(new HashSet<Integer>());

    static {
        try {
            System.loadLibrary(LIBRARY_NAME);
            mIsLdClogOk = true;
        } catch (Throwable e) {
            e.printStackTrace();
            mIsLdClogOk = false;
        }
    }

    static boolean isLdClogSuccess() {
        return mIsLdClogOk;
    }

    static LdCLogProtocol newInstance() {
        if (sLdCLogProtocol == null) {
            synchronized (LdCLogProtocol.class) {
                if (sLdCLogProtocol == null) {
                    sLdCLogProtocol = new LdCLogProtocol();
                }
            }
        }
        return sLdCLogProtocol;
    }

    /**
     * 初始化Clog
     *
     * @param dir_path 目录路径
     * @param max_file 最大文件值
     */
    private native int clog_init(String cache_path, String dir_path, String sub_dir_path,int max_file ,int outputType);

    private native int clog_open(String file_name ,String sub_path_dirs);

    private native void clog_debug(boolean is_debug);

    /**
     * @param lever       日志类型
     * @param log         日志内容
     * @param local_time  本地时间
     * @param thread_name 线程名称
     * @param tag   标识
     * @param type     事件类型
     */
    private native int clog_write(String tag  ,String local_time,String lever,  String type, String thread_name,String log);

    private native void clog_flush();

    @Override
    public void log_init(String cache_path, String dir_path ,String sub_dir_path, int max_file,int outputType) {
        if (mIsLdLogInit) {
            return;
        }
        if (!mIsLdClogOk) {
            logStatusCode(ConstantCode.ClogStatus.CLOG_LOAD_SO,
                    ConstantCode.ClogStatus.CLOG_LOAD_SO_FAIL);
            return;
        }

        try {
            int code = clog_init(cache_path, dir_path, sub_dir_path,max_file,outputType);
            mIsLdLogInit = true;
            logStatusCode(ConstantCode.ClogStatus.CLOG_INIT_STATUS, code);
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            logStatusCode(ConstantCode.ClogStatus.CLOG_INIT_STATUS,
                    ConstantCode.ClogStatus.CLOG_INIT_FAIL_JNI);
        }
    }

    @Override
    public void log_debug(boolean debug) {
        if (!mIsLdLogInit || !mIsLdClogOk) {
            return;
        }
        try {
            clog_debug(debug);
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOnLogProtocolStatus(OnLdLogProtocolStatus listener) {
        mLdLogProtocolStatus = listener;
    }

    @Override
    public void log_open(String file_name ,String sub_path_dir) {
        if (!mIsLdLogInit || !mIsLdClogOk) {
            return;
        }
        try {
            int code = clog_open(file_name,sub_path_dir);
            mIsLdLogOpen = true;
            logStatusCode(ConstantCode.ClogStatus.CLOG_OPEN_STATUS, code);
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            logStatusCode(ConstantCode.ClogStatus.CLOG_OPEN_STATUS,
                    ConstantCode.ClogStatus.CLOG_OPEN_FAIL_JNI);
        }
    }

    @Override
    public void log_flush() {
        if (!mIsLdLogOpen || !mIsLdClogOk) {
            return;
        }
        try {
            clog_flush();
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }

    }

    @Override
    public void log_write(String tag  ,String local_time,String lever,  String type, String thread_name,String log) {
        if (!mIsLdLogOpen || !mIsLdClogOk) {
            return;
        }
        try {

            int code = clog_write( tag , local_time, lever,  type, thread_name, log);

            if (code != ConstantCode.ClogStatus.CLOG_WRITE_SUCCESS || LdLog.sDebug) {
                logStatusCode(ConstantCode.ClogStatus.CLOG_WRITE_STATUS, code);
            }
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            logStatusCode(ConstantCode.ClogStatus.CLOG_WRITE_STATUS,
                    ConstantCode.ClogStatus.CLOG_WRITE_FAIL_JNI);
        }
    }

    private void logStatusCode(String cmd, int code) {
        if (code < 0) {
            if (ConstantCode.ClogStatus.CLOG_WRITE_STATUS.endsWith(cmd)
                    && code != ConstantCode.ClogStatus.CLOG_WRITE_FAIL_JNI) {
                if (mArraySet.contains(code)) {
                    return;
                } else {
                    mArraySet.add(code);
                }
            }
            if (mLdLogProtocolStatus != null) {
                mLdLogProtocolStatus.logProtocolStatus(cmd, code);
            }
        }
    }
}
