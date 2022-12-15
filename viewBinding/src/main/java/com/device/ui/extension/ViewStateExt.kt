package com.device.ui.extension


import com.device.ui.viewModel.viewStatus.VmState
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import com.imlaidian.utilslibrary.utils.LogUtil
import java.net.SocketException
import java.net.UnknownHostException


/**
 * 处理返回值
 *
 * @param result 请求结果
 */
fun <T> VmLiveData<T>.paresVmResult(result: HttpResponseModel<T>) {
     val data = if (result.isSuccess)  {
          VmState.Success(result.data)
      }else{
          VmState.Error(result.msg,result.result)
      }
     postValue(data)
}

/**
 * 异常转换异常处理
 */
fun <T> VmLiveData<T>.paresVmException(e: Throwable) {
    var errorMsg ="网络异常"
    when(e){
        is SocketException -> errorMsg ="网络异常，请检查网络！"
        is UnknownHostException ->errorMsg ="网络异常，请检查网络！"
        else ->{
            errorMsg ="服务异常,请稍重试"
            LogUtil.d("paresVmException" ,"e="+e.toString())
        }
    }

    postValue(VmState.Error(errorMsg,-12345))
}