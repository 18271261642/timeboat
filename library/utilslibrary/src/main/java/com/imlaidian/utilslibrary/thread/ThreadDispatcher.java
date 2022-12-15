package com.imlaidian.utilslibrary.thread;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;


import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;


/**
 * 线程分发，根据具体业务放入到不同线程队列中
 */
public class ThreadDispatcher implements ThreadFactory {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int HIGH_PRIORITY_POOL_SIZE = Math.max(1, Math.min(CPU_COUNT / 2, 4));//高优先级并发数(1 < nCpu/2 < 4)
    private static final int OTHER_PRIORITY_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 7));//其他优先级并发数(2 < nCpu-1 < 7)
    private static final int KEEP_ALIVE_SECONDS = 20;

    private static ThreadDispatcher sInstance;

    public static ThreadDispatcher getInstance() {
        if (sInstance == null) {
            synchronized (ThreadDispatcher.class) {
                if (sInstance == null) {
                    sInstance = new ThreadDispatcher();
                }
            }
        }
        return sInstance;
    }

    private final Map<PriorityRunnable, Runnable> mDelayRunnableMap = new ConcurrentHashMap<>();
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());

    private final ThreadPoolExecutor mOtherPriorityExecutor;
    private final ThreadPoolExecutor mHighPriorityExecutor;

    private ThreadDispatcher() {
        final ThreadPoolExecutor highExecutor = new ThreadPoolExecutor(
                HIGH_PRIORITY_POOL_SIZE, HIGH_PRIORITY_POOL_SIZE,
                KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(), this);
        highExecutor.allowCoreThreadTimeOut(true);
        mHighPriorityExecutor = highExecutor;

        final BlockingQueue<Runnable> otherQueue = new PriorityBlockingQueue<>(11, new Comparator<Runnable>() {
            @Override
            public int compare(Runnable o1, Runnable o2) {
                final boolean bL = o1 instanceof PriorityRunnable;
                final boolean bR = o2 instanceof PriorityRunnable;
                if (bL) {
                    if (bR)
                        return ((PriorityRunnable) o1).getPriority().value() - ((PriorityRunnable) o2).getPriority().value();
                    return -1;
                }
                return bR ? 1 : 0;
            }
        });
        final ThreadPoolExecutor otherExecutor = new ThreadPoolExecutor(
                OTHER_PRIORITY_POOL_SIZE, OTHER_PRIORITY_POOL_SIZE,
                KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                otherQueue, this);
        otherExecutor.allowCoreThreadTimeOut(true);
        mOtherPriorityExecutor = otherExecutor;

        final ExecutorService oldExecutor = (ExecutorService) hookAsyncTaskField("THREAD_POOL_EXECUTOR", otherExecutor);
        if (oldExecutor != null && !oldExecutor.isShutdown()) {
            oldExecutor.shutdown();
        }
        hookAsyncTaskField("sPoolWorkQueue", otherQueue);
        hookAsyncTaskField("sThreadFactory", this);
    }

    private static Object hookAsyncTaskField(String field, Object value) {
        Object old = null;
        try {
            final Field f = AsyncTask.class.getDeclaredField(field);
            f.setAccessible(true);
            old = f.get(AsyncTask.class);
            f.set(AsyncTask.class, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return old;
    }

    private boolean postInternal(PriorityRunnable runnable) {
        if (runnable == null)
            return false;
        if (BusinessPriority.UI == runnable.getPriority()) {
            final int highSize = mHighPriorityExecutor.getQueue().size();
            final int otherSize = mOtherPriorityExecutor.getQueue().size();
            if (highSize * OTHER_PRIORITY_POOL_SIZE <= otherSize * HIGH_PRIORITY_POOL_SIZE) {
                mHighPriorityExecutor.execute(runnable);
            } else {
                mOtherPriorityExecutor.execute(runnable);
            }
        } else {
            mOtherPriorityExecutor.execute(runnable);
        }
        return true;
    }

    private boolean postDelayedInternal(final PriorityRunnable runnable, long delay) {
        if (runnable == null)
            return false;
        if (delay <= 0)
            return postInternal(runnable);
        final Runnable delayRun = new Runnable() {
            @Override
            public void run() {
                mDelayRunnableMap.remove(runnable);
                postInternal(runnable);
            }
        };
        final boolean rlt = mMainHandler.postDelayed(delayRun, delay);
        if (rlt) {
            mDelayRunnableMap.put(runnable, delayRun);
        }
        return rlt;
    }

    private BusinessRunnable createBusinessRunnable(BusinessPriority priority, final Runnable runnable) {
        return new BusinessRunnable(priority) {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }

    /**
     * 提交一个业务runnable
     */
    public boolean postBusinessRunnable(PriorityRunnable runnable) {
        return postInternal(runnable);
    }

    /**
     * 提交一个非UI业务runnable，包含普通业务、网络业务、第三方业务、下载业务等
     *
     * @param priority 业务优先级
     * @param runnable 执行的线程
     * @return 提交结果
     */
    private boolean postUnUIBusinessRunnable(BusinessPriority priority, Runnable runnable) {
        if (priority == null || BusinessPriority.UI == priority) {
            priority = BusinessPriority.NORMAL;
        }
        return postInternal(createBusinessRunnable(priority, runnable));
    }


    /**
     * 提交一个UI相关业务runnable，此业务优先级别最高，请根据业务的紧急程度添加
     */
    public boolean postUIBusinessRunnable(Runnable runnable) {
        return postInternal(createBusinessRunnable(BusinessPriority.UI, runnable));
    }

    /**
     * 延时提交业务runnable，runnable需设置好对应业务优先级，默认为Normal
     */
    public boolean postBusinessRunnableDelayed(PriorityRunnable runnable, long delay) {
        return postDelayedInternal(runnable, delay);
    }

    /**
     * 提交普通业务runnable，提交后的runnable不可移除，如果后期要从队列中移除，请使用{@link ThreadDispatcher#postBusinessRunnable}.
     */
    public boolean postNormalBusinessRunnable(Runnable runnable) {
        return postUnUIBusinessRunnable(BusinessPriority.NORMAL, runnable);
    }

    /**
     * 提交下载业务runnable，提交后的runnable不可移除，如果后期要从队列中移除，请使用{@link ThreadDispatcher#postBusinessRunnable}.
     */
    public boolean postDownLoadBusinessRunnable(Runnable runnable) {
        return postUnUIBusinessRunnable(BusinessPriority.DOWNLOAD, runnable);
    }

    /**
     * 提交网络业务runnable，提交后的runnable不可移除，如果后期要从队列中移除，请使用{@link ThreadDispatcher#postBusinessRunnable}.
     */
    public boolean postNetWorkBusinessRunnable(Runnable runnable) {
        return postUnUIBusinessRunnable(BusinessPriority.NETWORK, runnable);
    }

    /**
     * 提交第三方相关业务runnable，提交后的runnable不可移除，如果后期要从队列中移除，请使用{@link ThreadDispatcher#postBusinessRunnable}.
     */
    public boolean postThirdPartyBusinessRunnable(Runnable runnable) {
        return postUnUIBusinessRunnable(BusinessPriority.THIRD_PARTY, runnable);
    }

    /**
     * 从队列中移除相关业务runnable
     */
    public void removeBusinessRunnable(PriorityRunnable runnable) {
        removeInternal(runnable);
    }

    private void removeInternal(PriorityRunnable runnable) {
        mMainHandler.removeCallbacks(mDelayRunnableMap.remove(runnable));
        mHighPriorityExecutor.getQueue().remove(runnable);
        mOtherPriorityExecutor.getQueue().remove(runnable);
    }

    public boolean postToMainThreadDelayed(Runnable runnable, long delay) {
        if (runnable == null) {
            return false;
        }
        return mMainHandler.postDelayed(runnable, delay);
    }

    public boolean removeRunnableFromMainThread(Runnable runnable) {
        if (runnable == null) {
            return false;
        }
        mMainHandler.removeCallbacks(runnable);
        return true;
    }

    public boolean runOnMainThread(Runnable run) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            run.run();
            return true;
        }
        return postToMainThread(run);
    }

    public boolean postToMainThread(final Runnable run) {
        if (run == null) {
            return false;
        }
        return mMainHandler.post(run);
    }

    /**
     * 顺序串行任务
     */
    final public void postSerial(Runnable run) {
        AsyncTask.execute(run);
    }

    public ExecutorService getExecutorService() {
        return mOtherPriorityExecutor;
    }

    @Override
    public Thread newThread(@NonNull Runnable r) {
        return new Thread(r) {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                super.run();
                if (Process.THREAD_PRIORITY_BACKGROUND != Process.getThreadPriority(Process.myTid())) {
                }
            }
        };
    }
}
