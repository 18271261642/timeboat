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
import net.sgztech.timeboat.provide.dataModel.UserLoginModel
import okhttp3.Call
import okhttp3.MediaType
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UpdateUserInfoRepository :BaseRepository(){
    suspend fun updateUserData(avatarUrl :String?  ,birthday:String? ,gender :Int?  ,height:String? , nickname:String? ,weight :String?): HttpResponseModel<UserLoginModel> {
        val params: MutableMap<String, String> = HashMap()


        if(birthday!=null){
            params["birthday"] = birthday
        }

        if(gender!=null){
            params["gender"] = ""  + gender
        }

        if(height!=null) {
            params["height"] = "" + height
        }

        if(weight!=null){
            params["weight"] = weight
        }

        val userInfo = SharedPreferencesUtil.getInstance().getString(Constants.USER_INFO)
        val userModel =jsonUtil.getObject(userInfo ,UserLoginModel::class.java)
        if(userModel!=null){
            params["token"] = userModel.token
        }else{
            params["token"] = ""
        }

        if(userModel!=null&&avatarUrl!=null){
            params["avatarUrl"] = userModel.avatarUrl!!
        }

        if(nickname!=null&&nickname.isNotEmpty()){
            params["nickname"] = nickname
        }

        val request = JSON.toJSONString(params)
        val requestCall =OkHttpUtils
            .postString()
            .url(HttpRequestConfig.updateUserInfo)
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