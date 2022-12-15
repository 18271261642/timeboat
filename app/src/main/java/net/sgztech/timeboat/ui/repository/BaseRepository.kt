package net.sgztech.timeboat.ui.repository

import android.util.Log
import com.imlaidian.okhttp.callback.StringCallback
import com.imlaidian.okhttp.request.RequestCall
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import com.imlaidian.utilslibrary.utils.LogUtil
import com.imlaidian.utilslibrary.utils.jsonUtil
import kotlinx.coroutines.suspendCancellableCoroutine
import net.sgztech.timeboat.provide.dataModel.UserLoginModel
import okhttp3.Call
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

open class BaseRepository {
    private val TAG= BaseRepository::class.java.simpleName
    interface Parser<T> {
        /**
         * 数据解析,Http请求成功后回调
         */
        fun onParse(response: String): HttpResponseModel<T>
    }

    suspend fun <T> await(call: RequestCall, parser: Parser<T>): HttpResponseModel<T> {
        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation {
                call.cancel()  //当前线程同关闭协程时的线程 如：A线程关闭协程，这当前就在A线程调用
            }
            call.execute(object : StringCallback() {
                override fun onError(call: Call, e: Exception, id: Int) {
                    e.printStackTrace()
                    continuation.resumeWithException(e)
                }

                override fun onResponse(response: String, id: Int) {
                    continuation.resume(parser.onParse(response))
                }
            })
        }
    }
}