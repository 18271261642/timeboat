package net.sgztech.timeboat.ui.repository

import com.alibaba.fastjson.JSON
import com.imlaidian.okhttp.OkHttpUtils
import com.imlaidian.utilslibrary.UtilsApplication
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil
import com.imlaidian.utilslibrary.utils.SystemTool
import com.imlaidian.utilslibrary.utils.jsonUtil
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.HttpRequestConfig
import net.sgztech.timeboat.provide.dataModel.DeviceConfigModel
import net.sgztech.timeboat.provide.dataModel.UserLoginModel
import okhttp3.MediaType

class DeviceConfigRepository :BaseRepository() {
    suspend fun configQuery(): HttpResponseModel<DeviceConfigModel> {
        val params: MutableMap<String, String> = HashMap()
        val userInfo = SharedPreferencesUtil.getInstance().getString(Constants.USER_INFO)
        val userModel = jsonUtil.getObject(userInfo , UserLoginModel::class.java)
        if(userModel!=null){
            params["token"] = userModel.token
        }
        params["os"] =  "android"
        params["versionCode"] = "" + SystemTool.getAppVersionCode(UtilsApplication.getInstance().applicationContext)
        params["versionName"] = SystemTool.getAppVersion(UtilsApplication.getInstance().applicationContext)
        val request = JSON.toJSONString(params)
        val requestCall = OkHttpUtils
            .postString()
            .url(HttpRequestConfig.configUrlQuery)
            .content(request)
            .mediaType(MediaType.parse("application/json;charset=utf-8"))
            .addHeader("Content-Type" ,"application/json; charset=UTF-8")
            .build()

        val responseModel = await(requestCall ,object :Parser<DeviceConfigModel>{
            override fun onParse(response: String): HttpResponseModel<DeviceConfigModel> {
                return jsonUtil.parseRespondModel<DeviceConfigModel>(response ,
                    DeviceConfigModel::class.java)
            }
        })

        return  responseModel
    }

}