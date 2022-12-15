package net.sgztech.timeboat.ui.repository

import com.alibaba.fastjson.JSON
import com.imlaidian.okhttp.OkHttpUtils
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import com.imlaidian.utilslibrary.utils.DateUtil
import com.imlaidian.utilslibrary.utils.LogUtil
import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil
import com.imlaidian.utilslibrary.utils.jsonUtil
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.Constants.Companion.BLE_DEVICE_INFO
import net.sgztech.timeboat.config.HttpRequestConfig
import net.sgztech.timeboat.provide.dataModel.*
import okhttp3.MediaType

class HeartRateRepository :BaseRepository(){
    private val TAG =HeartRateRepository::class.java.simpleName
    suspend fun getHeartRate(date :String ,type :Int): HttpResponseModel<HeartRateBean> {
        var url = ""
        when(type){
            Constants.dailyType -> url = HttpRequestConfig.heartRateDaily
            Constants.weeklyType -> url = HttpRequestConfig.heartRateWeekly
            Constants.monthlyType -> url = HttpRequestConfig.heartRateMonthly
            Constants.yearlyType -> url = HttpRequestConfig.heartRateYearly
            else -> {
                url = HttpRequestConfig.heartRateDaily
                LogUtil.d(TAG ,"type error type=" +type)
            }
        }

        val params: MutableMap<String, String> = HashMap()
        val userInfo = SharedPreferencesUtil.getInstance().getString(Constants.USER_INFO)
        val userModel = jsonUtil.getObject(userInfo , UserLoginModel::class.java)

        if(userModel!=null){
            params["token"] = userModel.token
            params["userUUID"] = userModel.userUUID
        }else{
            params["token"] = ""
            params["userUUID"] = ""
        }

        var bleInfo = SharedPreferencesUtil.getInstance().getString(BLE_DEVICE_INFO)
        val bleModel = jsonUtil.getObject(bleInfo , BleDeviceInfoModel::class.java)
        if(bleModel!=null){
            params["imei"] = bleModel.imei
        }else{
            params["imei"] = ""
        }

        params["date"] = date


        val request = JSON.toJSONString(params)
        val requestCall = OkHttpUtils
            .postString()
            .url(url)
            .content(request)
            .mediaType(MediaType.parse("application/json;charset=utf-8"))
            .addHeader("Content-Type" ,"application/json; charset=UTF-8")
            .build()

        val responseModel= await(requestCall ,object :Parser<HeartRateBean>{
            override fun onParse(response: String): HttpResponseModel<HeartRateBean> {
                return jsonUtil.parseRespondModel(
                    response,
                    HeartRateBean::class.java
                )
            }
        })
        return responseModel
    }

}