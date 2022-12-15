package net.sgztech.timeboat.ui.repository

import com.alibaba.fastjson.JSON
import com.imlaidian.okhttp.OkHttpUtils
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil
import com.imlaidian.utilslibrary.utils.jsonUtil
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.HttpRequestConfig
import net.sgztech.timeboat.provide.dataModel.UnSubscribeAgreeModel
import net.sgztech.timeboat.provide.dataModel.UserLoginModel
import okhttp3.MediaType

class UnSubscribeAgreementRepository :BaseRepository(){

    suspend fun agreeUrl(): HttpResponseModel<UnSubscribeAgreeModel> {
        val params: MutableMap<String, String> = HashMap()
        val userInfo = SharedPreferencesUtil.getInstance().getString(Constants.USER_INFO)
        val userModel = jsonUtil.getObject(userInfo , UserLoginModel::class.java)
        if(userModel!=null){
            params["token"] = userModel.token
        }
        val request = JSON.toJSONString(params)
        val requestCall = OkHttpUtils
            .postString()
            .url(HttpRequestConfig.unsubscribeUserAgree)
            .content(request)
            .mediaType(MediaType.parse("application/json;charset=utf-8"))
            .addHeader("Content-Type" ,"application/json; charset=UTF-8")
            .build()

        val responseModel = await(requestCall ,object :Parser<UnSubscribeAgreeModel>{
            override fun onParse(response: String): HttpResponseModel<UnSubscribeAgreeModel> {
                return jsonUtil.parseRespondModel<UnSubscribeAgreeModel>(response ,
                    UnSubscribeAgreeModel::class.java)
            }
        })

        return  responseModel
    }
}