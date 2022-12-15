
package com.imlaidian.ldclog;

class LdLogProtocol implements LdLogProtocolHandler {

    private static LdLogProtocol sLdLogProtocol;

    private LdLogProtocolHandler mCurProtocol;
    private boolean mIsInit;
    private OnLdLogProtocolStatus mLdLogProtocolStatus;

    private LdLogProtocol() {

    }

    static LdLogProtocol newInstance() {
        if (sLdLogProtocol == null) {
            synchronized (LdLogProtocol.class) {
                sLdLogProtocol = new LdLogProtocol();
            }
        }
        return sLdLogProtocol;
    }

    @Override
    public void log_flush() {
        if (mCurProtocol != null) {
            mCurProtocol.log_flush();
        }
    }

    @Override
    public void log_write(String tag  ,String local_time,String lever,  String type, String thread_name,String log) {
        if (mCurProtocol != null) {
            mCurProtocol.log_write( tag , local_time, lever, type , thread_name, log);
        }
    }

    @Override
    public void log_open(String file_name,String subPath) {
        if (mCurProtocol != null) {
            mCurProtocol.log_open(file_name,subPath);
        }
    }

    @Override
    public void log_init(String cache_path, String dir_path, String sub_dir_path,int max_file ,int outputType) {
        if (mIsInit) {
            return;
        }
        if (LdCLogProtocol.isLdClogSuccess()) {
            mCurProtocol = LdCLogProtocol.newInstance();
            mCurProtocol.setOnLogProtocolStatus(mLdLogProtocolStatus);
            mCurProtocol.log_init(cache_path, dir_path,sub_dir_path, max_file,outputType);
            mIsInit = true;
        } else {
            mCurProtocol = null;
        }
    }

    @Override
    public void log_debug(boolean debug) {
        if (mCurProtocol != null) {
            mCurProtocol.log_debug(debug);
        }
    }

    @Override
    public void setOnLogProtocolStatus(OnLdLogProtocolStatus listener) {
        mLdLogProtocolStatus = listener;
    }
}
