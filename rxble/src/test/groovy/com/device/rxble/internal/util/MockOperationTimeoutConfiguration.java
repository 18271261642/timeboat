package com.device.rxble.internal.util;

import com.device.rxble.internal.operations.TimeoutConfiguration;

import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;


public class MockOperationTimeoutConfiguration extends TimeoutConfiguration {

    public static final int TIMEOUT_IN_SEC = 30;

    public MockOperationTimeoutConfiguration(int seconds, Scheduler timeoutScheduler) {
        super(seconds, TimeUnit.SECONDS, timeoutScheduler);
    }

    public MockOperationTimeoutConfiguration(Scheduler timeoutScheduler) {
        this(TIMEOUT_IN_SEC, timeoutScheduler);
    }
}
