package com.device.ui.viewModel.viewStatus

class VmResult<T> {
    var onAppSuccess: (data: T?) -> Unit = {}
    var onAppError: (msg :String ,errorCode:Int ) -> Unit = { _, _ ->  }
    var onAppLoading: () -> Unit = {}
    var onAppComplete: () -> Unit = {}
}

sealed class VmState<out T> {
    object Loading: VmState<Nothing>()
    data class Success<out T>(val data: T?) : VmState<T>()
    data class Error(val msg :String, val errorCode :Int ) : VmState<Nothing>()
}