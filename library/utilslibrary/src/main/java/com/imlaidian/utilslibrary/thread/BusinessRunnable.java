package com.imlaidian.utilslibrary.thread;


import androidx.annotation.NonNull;

/**
 * 业务线程使用的runnable
 */

public abstract class BusinessRunnable implements PriorityRunnable {
    public static final BusinessPriority DEFAULT_PRIORITY = BusinessPriority.NORMAL;

    //业务优先级
    private BusinessPriority mPriority = DEFAULT_PRIORITY;

    public BusinessRunnable() {
    }

    public BusinessRunnable(BusinessPriority priority) {
        if (priority == null) {
            priority = DEFAULT_PRIORITY;
        }
        this.mPriority = priority;
    }

    public void setPriority(BusinessPriority priority) {
        if (priority == null)
            throw new NullPointerException("priority cannot be null");
        this.mPriority = priority;
    }

    @Override
    public BusinessPriority getPriority() {
        return mPriority;
    }

    @Override
    public int compareTo(@NonNull PriorityRunnable o) {
        return mPriority.value() - o.getPriority().value();
    }
}
