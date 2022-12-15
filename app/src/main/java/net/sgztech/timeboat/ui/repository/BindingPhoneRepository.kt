package net.sgztech.timeboat.ui.repository

import com.alibaba.fastjson.JSON
import com.imlaidian.okhttp.OkHttpUtils
import com.imlaidian.okhttp.callback.StringCallback
import com.imlaidian.okhttp.request.RequestCall
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import com.imlaidian.utilslibrary.utils.LogUtil
import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil
import com.imlaidian.utilslibrary.utils.jsonUtil
import kotlinx.coroutines.suspendCancellableCoroutine
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.HttpRequestConfig
import net.sgztech.timeboat.managerUtlis.SettingInfoManager
import net.sgztech.timeboat.provide.dataModel.AuthorCodeModel
import net.sgztech.timeboat.provide.dataModel.UserLoginModel
import okhttp3.Call
import okhttp3.MediaType
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class BindingPhoneRepository :BaseRepository(){
    private val tag = BindingPhoneRepository::class.java.simpleName
    suspend fun bindingPhoneData(phone:String,code:String): HttpResponseModel<UserLoginModel>{
        val params: MutableMap<String, String> = HashMap()
        val userInfo = SharedPreferencesUtil.getInstance().getString(Constants.USER_INFO)
        val userModel =jsonUtil.getObject(userInfo ,UserLoginModel::class.java)
        params["phone"] = phone
        params["code"] = code
        if(userModel!=null){
            params["token"] = userModel.token
        }

        val request = JSON.toJSONString(params)
        LogUtil.d(tag, "BindingPhoneRepository request" +request)
        val requestCall =OkHttpUtils
            .postString()
            .url(HttpRequestConfig.bindingPhone)
            .content(request)
            .mediaType(MediaType.parse("application/json;charset=utf-8"))
            .addHeader("Content-Type" ,"application/json; charset=UTF-8")
            .build()

        val responseModel = await(requestCall,object :Parser<UserLoginModel>{
            override fun onParse(response: String): HttpResponseModel<UserLoginModel> {
                return jsonUtil.parseRespondModel<UserLoginModel>(response ,
                    UserLoginModel::class.java)
            }
        })
        return responseModel
    }

}