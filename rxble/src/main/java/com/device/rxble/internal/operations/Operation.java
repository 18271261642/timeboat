package com.device.rxble.internal.operations;


import androidx.annotation.RestrictTo;

import com.device.rxble.internal.Priority;
import com.device.rxble.internal.serialization.QueueReleaseInterface;

import io.reactivex.Observable;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public interface Operation<T> extends Comparable<Operation<?>> {

    Observable<T> run(QueueReleaseInterface queueReleaseInterface);

    Priority definedPriority();
}
