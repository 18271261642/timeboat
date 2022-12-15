package com.device.ui.extension

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.device.ui.viewModel.viewStatus.VmState

@MainThread
inline fun <T> LiveData<T>.observe(
        owner: LifecycleOwner,
        crossinline onChanged: (T) -> Unit
): Observer<T> {
    val wrappedObserver = Observer<T> { t -> onChanged(t) }
    observe(owner, wrappedObserver)
    return wrappedObserver
}

typealias VmLiveData<T> = MutableLiveData<VmState<T>>