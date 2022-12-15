
package com.imlaidian.ldclog;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

class LdLogControlCenter {

    private static LdLogControlCenter sLdLogControlCenter;
    private String TAG =LdLogControlCenter.class.getSimpleName();
    private ConcurrentLinkedQueue<LdLogModel> mCacheLogQueue = new ConcurrentLinkedQueue<>();
    private String mCachePath; // 缓存文件路径
    private String mPath; //文件路径
    private String[] mWhitePath ;
    private long mSaveTime; //存储时间
    private long mMaxLogFile;//最大文件大小
    private long mMinSDCard;
    private long mMaxQueue; //最大队列数
    private LdLogThread mLdLogThread;
    private SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
    private boolean isStopWrite= false ;
    private LdLogControlCenter(LdLogConfig config) {
        if (!config.isValid()) {
            throw new NullPointerException("config's param is invalid");
        }

        mPath = config.mPathPath;
        mCachePath = config.mCachePath;
        mSaveTime = config.mDay;
        mMinSDCard = config.mMinSDCard;
        mMaxLogFile = config.mMaxFile;
        mMaxQueue = config.mMaxQueue;
        mWhitePath = config.mWhitePath ;
        init();
    }

    private void init() {
        if (mLdLogThread == null) {
            mLdLogThread = new LdLogThread(mCacheLogQueue, mCachePath, mPath, mSaveTime,
                    mMaxLogFile, mMinSDCard,mWhitePath);
            mLdLogThread.setName("ldlog-thread");
            mLdLogThread.start();
        }
    }

    static LdLogControlCenter instance(LdLogConfig config) {
        if (sLdLogControlCenter == null) {
            synchronized (LdLogControlCenter.class) {
                if (sLdLogControlCenter == null) {
                    sLdLogControlCenter = new LdLogControlCenter(config);
                }
            }
        }
        return sLdLogControlCenter;
    }

    void write(String tag,String log, String lever) {
        write(tag ,log ,lever , "Log");
    }

    void write(String tag,String log, String lever ,String type) {
        if (TextUtils.isEmpty(log)) {
            return;
        }
        LdLogModel model = new LdLogModel();
        model.action = LdLogModel.Action.WRITE;
        WriteAction action = new WriteAction();
        String threadName = Thread.currentThread().getName();
        long threadLog = Thread.currentThread().getId();
        action.log = log;
        action.localTime = System.currentTimeMillis();
        action.lever = lever;
        action.tag=tag ;
        action.type = type;
        action.threadId = threadLog;
        action.threadName = threadName;
        model.writeAction = action;
        if (mCacheLogQueue.size() < mMaxQueue &&!isStopWrite) {
            mCacheLogQueue.add(model);
            if (mLdLogThread != null) {
                mLdLogThread.notifyRun();
            }
        }else{
            if (LdLog.sDebug) {
                Log.d("TAG", "mCacheLogQueue.size()=" +mCacheLogQueue.size());
            }
        }
    }

    void send(List<LdUploadItem> uploadList, SendLogRunnable runnable) {
        if (TextUtils.isEmpty(mPath) || uploadList == null || uploadList.size() == 0) {
            return;
        }
        for (LdUploadItem uploadItem : uploadList) {
            if (uploadItem== null || TextUtils.isEmpty(uploadItem.getFileName())) {
                continue;
            }
            LdLogModel model = new LdLogModel();
            SendAction action = new SendAction();
            model.action = LdLogModel.Action.SEND;
            action.uploadItem = uploadItem ;
            action.sendLogRunnable = runnable;
            model.sendAction = action;
            mCacheLogQueue.add(model);
            if (mLdLogThread != null) {
                mLdLogThread.notifyRun();
            }
        }

    }



    void flush() {
        if (TextUtils.isEmpty(mPath)) {
            return;
        }
        LdLogModel model = new LdLogModel();
        model.action = LdLogModel.Action.FLUSH;
        mCacheLogQueue.add(model);
        if (mLdLogThread != null) {
            mLdLogThread.notifyRun();
        }
    }


    void stop(){
        flush();
        isStopWrite =true ;
    }

    File getDir() {
        return new File(mPath);
    }

    private long getDateTime(String time) {
        long tempTime = 0;
        try {
            tempTime = dataFormat.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tempTime;
    }
}
