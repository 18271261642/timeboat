package net.sgztech.timeboat.ui.repository

import com.alibaba.fastjson.JSON
import com.imlaidian.okhttp.OkHttpUtils
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil
import com.imlaidian.utilslibrary.utils.jsonUtil
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.HttpRequestConfig
import net.sgztech.timeboat.provide.dataModel.AdInfoModel
import net.sgztech.timeboat.provide.dataModel.BleDeviceInfoModel
import net.sgztech.timeboat.provide.dataModel.HomeSportDataModel
import net.sgztech.timeboat.provide.dataModel.UserLoginModel
import okhttp3.MediaType

class HomeSportRepository :BaseRepository(){

    suspend fun getSportData(): HttpResponseModel<HomeSportDataModel> {
        var url = HttpRequestConfig.homePageSport
        val params: MutableMap<String, String> = HashMap()
        val userInfo = SharedPreferencesUtil.getInstance().getString(Constants.USER_INFO)
        val userModel = jsonUtil.getObject(userInfo , UserLoginModel::class.java)

        if(userModel!=null){
            params["token"] = userModel.token
            params["userUUID"] = userModel.userUUID
        }

        val bleInfo = SharedPreferencesUtil.getInstance().getString(Constants.BLE_DEVICE_INFO)
        val bleModel = jsonUtil.getObject(bleInfo , BleDeviceInfoModel::class.java)
        if(bleModel!=null){
            params["imei"] = bleModel.imei
        }
        val request = JSON.toJSONString(params)
        val requestCall =OkHttpUtils
            .postString()
            .url(url)
            .content(request)
            .mediaType(MediaType.parse("application/json;charset=utf-8"))
            .addHeader("Content-Type" ,"application/json; charset=UTF-8")
            .build()

        val responseModel= await(requestCall ,object :Parser<HomeSportDataModel>{
            override fun onParse(response: String): HttpResponseModel<HomeSportDataModel> {
                return jsonUtil.parseRespondModel(
                    response,
                    HomeSportDataModel::class.java
                )
            }
        })
        return responseModel
    }


}