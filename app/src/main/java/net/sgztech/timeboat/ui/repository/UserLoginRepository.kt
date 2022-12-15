package net.sgztech.timeboat.ui.repository

import com.alibaba.fastjson.JSON
import com.imlaidian.okhttp.OkHttpUtils
import com.imlaidian.okhttp.callback.StringCallback
import com.imlaidian.okhttp.request.RequestCall
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import com.imlaidian.utilslibrary.utils.LogUtil
import com.imlaidian.utilslibrary.utils.jsonUtil
import kotlinx.coroutines.suspendCancellableCoroutine
import net.sgztech.timeboat.config.HttpRequestConfig
import net.sgztech.timeboat.provide.dataModel.AuthorCodeModel
import net.sgztech.timeboat.provide.dataModel.UserLoginModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UserLoginRepository :BaseRepository() {

    private val tag =UserLoginRepository::class.java.simpleName
    suspend fun getData(loginType :Int  ,phone:String? ,code :String?  ,avatarUrl:String? , openId:String?,nickname:String?): HttpResponseModel<UserLoginModel>{
        val params: MutableMap<String, String> = HashMap()
        if(avatarUrl!=null){
            params["avatarUrl"] = avatarUrl
        }else{
            params["avatarUrl"] = ""
        }

        if(code!=null){
           params["code"] = code
        }else{
            params["code"] = ""
        }
        if(phone!=null){
            params["phone"] = phone
        }else{
            params["phone"] = ""
        }

        params["loginType"] = ""+ loginType

        if(openId!=null){
            params["openId"] = openId
        }else{
            params["openId"] = ""
        }
        if(nickname!=null){
            params["nickname"] = nickname
        }else{
            params["nickname"] = ""
        }
        val request = JSON.toJSONString(params)
        LogUtil.d(tag, "UserLoginRepository request="+request)
        val requestCall = OkHttpUtils
            .postString()
            .url(HttpRequestConfig.userLoginUrl)
            .content(request)
            .mediaType(MediaType.parse("application/json;charset=utf-8"))
            .addHeader("Content-Type" ,"application/json; charset=UTF-8")
            .build()

        val responseModel = await(requestCall,object : BaseRepository.Parser<UserLoginModel> {
            override fun onParse(response: String): HttpResponseModel<UserLoginModel> {
                return jsonUtil.parseRespondModel<UserLoginModel>(response ,
                    UserLoginModel::class.java)
            }
        })
        return responseModel
    }

}