package com.device.rxble.internal.serialization;

import com.device.rxble.exceptions.BleException;

/**
 * {@inheritDoc}
 */
public interface ConnectionOperationQueue extends ClientOperationQueue {

    /**
     * A method for terminating all operations that are still queued on the connection.
     * @param disconnectedException the exception to be passed to all queued operations subscribers
     */
    void terminate(BleException disconnectedException);
}
