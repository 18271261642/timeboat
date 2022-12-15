package com.imlaidian.utilslibrary.thread;

/**
 * 带优先级的Runnable
 */

public interface PriorityRunnable extends Runnable, Comparable<PriorityRunnable> {
    BusinessPriority getPriority();
}
