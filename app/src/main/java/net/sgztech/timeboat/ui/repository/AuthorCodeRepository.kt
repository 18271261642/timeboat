package net.sgztech.timeboat.ui.repository

import android.util.Log
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
import kotlin.math.log

class AuthorCodeRepository :BaseRepository() {
    private val TAG = AuthorCodeRepository::class.java.simpleName

    suspend fun getData(phone:String): HttpResponseModel<AuthorCodeModel> {
        val params: MutableMap<String, String> = HashMap()
        params["phone"] = phone
        val request = JSON.toJSONString(params)
        val requestCall = OkHttpUtils
            .postString()
            .url(HttpRequestConfig.sendLoginSms)
            .content(request)
            .mediaType(MediaType.parse("application/json;charset=utf-8"))
            .addHeader("Content-Type" ,"application/json; charset=UTF-8")
            .build()

        val responseModel = await(requestCall ,object :Parser<AuthorCodeModel>{
            override fun onParse(response: String): HttpResponseModel<AuthorCodeModel> {
                return jsonUtil.parseRespondModel<AuthorCodeModel>(response ,
                    AuthorCodeModel::class.java)
            }
        })


        return  responseModel
    }



    suspend fun bindingSmsData(phone:String): HttpResponseModel<AuthorCodeModel>{
        val params: MutableMap<String, String> = HashMap()
        val userInfo = SharedPreferencesUtil.getInstance().getString(Constants.USER_INFO)
        val userModel =jsonUtil.getObject(userInfo , UserLoginModel::class.java)
        params["phone"] = phone
        if(userModel!=null ){
            params["token"] = userModel.token
        }else{
            params["token"] = ""
        }

        val request = JSON.toJSONString(params)
        Log.d(TAG,"bindingSmsData request=" +request)
       val requestCall = OkHttpUtils
            .postString()
            .url(HttpRequestConfig.sendBindingPhoneSms)
            .content(request)
            .mediaType(MediaType.parse("application/json;charset=utf-8"))
            .addHeader("Content-Type" ,"application/json; charset=UTF-8")
            .build()

        val responseModel = await(requestCall ,object :Parser<AuthorCodeModel>{
            override fun onParse(response: String): HttpResponseModel<AuthorCodeModel> {
                return jsonUtil.parseRespondModel<AuthorCodeModel>(response ,
                    AuthorCodeModel::class.java)
            }
        })

        return  responseModel
    }


}