package com.device.ui.viewModel.common

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import com.device.ui.extension.VmLiveData
import com.device.ui.extension.observe
import com.device.ui.viewModel.viewStatus.VmResult
import com.device.ui.viewModel.viewStatus.VmState
import java.lang.reflect.ParameterizedType
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException


/**
 * 获取vm clazz
 */
@Suppress("UNCHECKED_CAST")
fun <VM> getVmClazz(obj: Any): VM {
    return (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as VM
}

/**
 * 跟下面的方法类似,只是调用形式上有所区别
 * 这种vmResult要提前定义好
 * 下面vmResult: VmResult<T>.() -> Unit可以直接写在参数里面
 */
@MainThread
inline fun <T> VmLiveData<T>.vmObserver(owner: LifecycleOwner, vmResult: VmResult<T>) {
    observe(owner) {
        when (it) {
            is VmState.Loading ->{
                vmResult.onAppLoading()
            }
            is VmState.Success<*> -> {
                vmResult.onAppSuccess(it.data as T)
                vmResult.onAppComplete()
            }
            is VmState.Error -> {
                vmResult.onAppError(it.msg,it.errorCode)
                vmResult.onAppComplete()
            }
        }
    }
}

/**
 * 重写所有回调方法
 * onAppLoading
 * onAppSuccess
 * onAppError
 * onAppComplete
 */
@MainThread
inline fun <T> VmLiveData<T>.vmObserver(owner: LifecycleOwner, vmResult: VmResult<T>.() -> Unit) {
    val result = VmResult<T>();
    result.vmResult();
    observe(owner) {
        when (it) {
            is VmState.Loading ->{
                result.onAppLoading()
            }
            is VmState.Success<*> -> {
                result.onAppSuccess(it.data as T );result.onAppComplete()
            }
            is VmState.Error -> {
                result.onAppError(it.msg,it.errorCode);result.onAppComplete()
            }
        }
    }
}




