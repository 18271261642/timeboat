package com.device.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.device.ui.extension.VmLiveData
import com.device.ui.extension.paresVmException
import com.device.ui.extension.paresVmResult
import com.device.ui.viewModel.viewStatus.VmState
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel将界面和数据分离,并且独立于Activity的重建。
 */
open class BaseViewModel : ViewModel() {

    fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {

        viewModelScope.launch {
            block()
        }
    }
    suspend fun <T> launchOnIO(block: suspend CoroutineScope.() -> T  ) : T{
       return withContext(Dispatchers.IO) {
             return@withContext block()
        }
    }

    /**
     * BaseViewModel开启协程扩展
     */
    fun <T> launchVmRequest(
        request: suspend CoroutineScope.() ->HttpResponseModel<T>,
        viewState: VmLiveData<T>
    ) {
        viewModelScope.launch{
            runCatching {
                viewState.postValue(VmState.Loading)
                request()
            }.onSuccess {
                viewState.paresVmResult(it)
            }.onFailure {
                viewState.paresVmException(it)
            }
        }
    }

    /**
     * 当此viewModel没有任何Activity与之关联时,会调用onCleared,适合做清理资源的操作
     * 在使用ViewModel的时候,千万不能从外面传入Context之类的引用，否则系统会认为该ViewModel还在使用中
     * 从而无法被系统销毁回收,导致内存泄漏的发生
     *
     * 如果希望在ViewModel中使用Context,可以使用AndroidViewModel类
     * 它继承自ViewModel,并且接收Application作为Context
     */
    public override fun onCleared() {
        super.onCleared()
    }
}