package net.sgztech.timeboat.ui.repository

import com.alibaba.fastjson.JSON
import com.imlaidian.okhttp.OkHttpUtils
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil
import com.imlaidian.utilslibrary.utils.jsonUtil
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.HttpRequestConfig
import net.sgztech.timeboat.provide.dataModel.AuthorCodeModel
import net.sgztech.timeboat.provide.dataModel.UserLoginModel
import okhttp3.MediaType

class UnsubscribeUerInfoRepository :BaseRepository(){

    suspend fun unsubscribe(): HttpResponseModel<Any> {
        val params: MutableMap<String, String> = HashMap()
        val userInfo = SharedPreferencesUtil.getInstance().getString(Constants.USER_INFO)
        val userModel =jsonUtil.getObject(userInfo , UserLoginModel::class.java)
        if(userModel!=null){
            params["token"] = userModel.token
        }
        val request = JSON.toJSONString(params)
        val requestCall = OkHttpUtils
            .postString()
            .url(HttpRequestConfig.unsubscribeUserInfo)
            .content(request)
            .mediaType(MediaType.parse("application/json;charset=utf-8"))
            .addHeader("Content-Type" ,"application/json; charset=UTF-8")
            .build()

        val responseModel = await(requestCall ,object :Parser<Any>{
            override fun onParse(response: String): HttpResponseModel<Any> {
                return jsonUtil.parseRespondModel<Any>(response ,
                    Any::class.java)
            }
        })

        return  responseModel
    }
}