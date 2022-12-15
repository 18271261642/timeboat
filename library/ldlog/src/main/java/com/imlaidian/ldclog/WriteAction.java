

package com.imlaidian.ldclog;

import android.text.TextUtils;

class WriteAction {
    //日志内容
    String log;
    //日志标识
    String tag ;
    // 日志类型 如：普通日志，埋点
    String type;
    //线程id
    long threadId;
    //线程名字
    String threadName = "";
    // 时间
    long localTime;
    // 日志等级
    String lever;

    boolean isValid() {
        boolean valid = false;
        if (!TextUtils.isEmpty(log)) {
            valid = true;
        }
        return valid;
    }
}
