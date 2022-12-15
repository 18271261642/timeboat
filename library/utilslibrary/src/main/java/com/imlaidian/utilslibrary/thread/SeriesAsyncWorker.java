package com.imlaidian.utilslibrary.thread;

import android.view.View;
import java.lang.ref.WeakReference;
import androidx.annotation.MainThread;
import androidx.core.view.ViewCompat;

/**
 * <p>
 * 简化版的对异步顺序执行的封装,支持链式调用,简洁多次异步执行的代码.
 * 设计的思想和RxJava类似.
 */

public class SeriesAsyncWorker implements Work.Callback {
    private WorkNode mHead = null;
    private WorkNode mTail = null;
    private boolean mStarted;
    private WeakReference<View> targetViewRef;

    private SeriesAsyncWorker(View targetView) {
        if (targetView != null) {
            this.targetViewRef = new WeakReference<>(targetView);
        }
    }

    public static SeriesAsyncWorker create() {
        return new SeriesAsyncWorker(null);
    }

    /**
     * @param targetView 目标View,每个work会以{@link View#postOnAnimation(Runnable)}的方式执行,即{@link Work#doInBackground}会失效
     */
    public static SeriesAsyncWorker create(View targetView) {
        if (targetView == null) {
            throw new NullPointerException("targetView cannot be null");
        }
        return new SeriesAsyncWorker(targetView);
    }

    public synchronized SeriesAsyncWorker next(Work work) {
        if (mStarted) {
            throw new IllegalStateException("call #next after worker has started");
        }
        if (work == null)
            return this;
        work.setCallback(this);
        final WorkNode appendNode = new WorkNode(work);
        if (mHead == null) {
            mHead = mTail = appendNode;
        }
        appendNode.next = null;
        mTail.next = appendNode;
        mTail = appendNode;
        return this;
    }

    @MainThread
    public void start() {
        start(null);
    }

    @MainThread
    public synchronized boolean start(final Object param) {
        if (mStarted)
            return false;
        mStarted = true;
        ThreadDispatcher.getInstance().runOnMainThread(new Runnable() {
            @Override
            public void run() {
                doWorkInternal(mHead, param);
            }
        });
        return true;
    }

    public void doNext() {
        doNext(null);
    }

    @Override
    public synchronized void doNext(Object lastResult) {
        final WorkNode head = mHead;
        if (head != null && head.next != null) {
            doWorkInternal(head.next, lastResult);
        }
    }

    @Override
    public synchronized void interrupt() {
        reset();
    }

    private synchronized void doWorkInternal(WorkNode node, final Object lastResult) {
        if (node == null) {
            reset();
            return;
        }
        mHead = node;
        final Work work = node.work;
        BusinessRunnable runnable = new BusinessRunnable(work.getPriority()) {
            @Override
            public void run() {
                work.doWork(lastResult);
            }
        };
        final long delay = work.getDelayTime();
        if (targetViewRef != null) {
            final View targetView = targetViewRef.get();
            if (targetView != null) {
                if (delay > 0) {
                    ViewCompat.postOnAnimationDelayed(targetView, runnable, delay);
                } else {
                    ViewCompat.postOnAnimation(targetView, runnable);
                }
            }
        } else {
            final boolean doInBackground = work.isDoInBackground();
            if (doInBackground) {
                if (delay > 0) {
                    ThreadDispatcher.getInstance().postBusinessRunnableDelayed(runnable, delay);
                } else {
                    ThreadDispatcher.getInstance().postBusinessRunnable(runnable);
                }
            } else {
                if (delay > 0) {
                    ThreadDispatcher.getInstance().postToMainThreadDelayed(runnable, delay);
                } else {
                    ThreadDispatcher.getInstance().postToMainThread(runnable);
                }
            }
        }
    }

    private void reset() {
        mHead = mTail = null;
        mStarted = false;
    }

    private static class WorkNode {
        Work work;
        WorkNode next;

        WorkNode(Work work) {
            this.work = work;
        }
    }
}