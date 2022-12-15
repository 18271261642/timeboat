
package com.imlaidian.ldclog;

import android.text.TextUtils;

import java.io.File;

public abstract class SendLogRunnable implements Runnable {
    public static final int SENDING = 10001;
    public static final int FINISH = 10002;

    private SendAction mSendAction;
    private OnSendLogCallBackListener mCallBackListener;

    /**
     * 真正发送上传日志文件的方法，留给外部实现
     * @param uploadItem 上传文件的具体属性

     */
    public abstract void sendLog(LdUploadItem uploadItem);

    void setSendAction(SendAction action) {
        mSendAction = action;
    }

    @Override
    public void run() {
        if (mSendAction == null || mSendAction.uploadItem ==null || TextUtils.isEmpty(mSendAction.uploadItem.fileName)) {
            finish();
            return;
        }

        if (TextUtils.isEmpty(mSendAction.uploadItem.uploadPath)) {
            finish();
            return;
        }

        sendLog(mSendAction.uploadItem );
    }

    void setCallBackListener(OnSendLogCallBackListener callBackListener) {
        mCallBackListener = callBackListener;
    }

    /**
     * Must call this method after send log finish!
     */
    protected void finish() {
        if (mCallBackListener != null) {
            mCallBackListener.onCallBack(FINISH);
        }
    }

    interface OnSendLogCallBackListener {
        void onCallBack(int statusCode);
    }
}
