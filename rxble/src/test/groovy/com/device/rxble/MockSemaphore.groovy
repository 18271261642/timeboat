package com.device.rxble

import com.device.rxble.internal.serialization.QueueAwaitReleaseInterface
import com.device.rxble.internal.serialization.QueueReleaseInterface

class MockSemaphore implements QueueReleaseInterface, QueueAwaitReleaseInterface {
    int permits = 0

    MockSemaphore() {
    }

    @Override
    void awaitRelease() throws InterruptedException {
        permits++
    }

    @Override
    void release() {
        permits--
    }

    boolean isReleased() {
        permits <= 0
    }
}
