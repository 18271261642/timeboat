package com.imlaidian.utilslibrary.utilsManager;

import android.util.Log;

import com.imlaidian.utilslibrary.utils.LogUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {
    private static final String TAG = "ThreadPoolManager";
    private static ThreadPoolManager mInstance = null;
    private static final int CORE_POOL_SIZE = 8;
    // 线程池适用执行单个比较短的数据操作
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(CORE_POOL_SIZE);
    // 线程池适用执行单个定时任务的数据操作
    private ScheduledExecutorService mScheduledFixRateService = Executors.newScheduledThreadPool(CORE_POOL_SIZE);
    public static ThreadPoolManager getInstance() {
       if (null == mInstance) {
           synchronized (ThreadPoolManager.class) {
                if (null == mInstance) {
                    mInstance = new ThreadPoolManager();
                }
           }
       }

       return mInstance;
    }

    public void execute(Runnable runnable) {
        if (null != runnable) {
            mExecutorService.execute(runnable);
        }
    }


    public ScheduledFuture<?> fixedRate(Runnable runnable , long delay , long period){
        if (null != runnable) {
            LogUtil.d(TAG ,"fixedRate");
           return mScheduledFixRateService.scheduleAtFixedRate(runnable ,delay, period, TimeUnit.MILLISECONDS);
        }else{
            LogUtil.d(TAG ,"fixedRate null");
            return null ;
        }
    }

}
