package net.sgztech.timeboat.ui.repository

import com.alibaba.fastjson.JSON
import com.imlaidian.okhttp.OkHttpUtils
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil
import com.imlaidian.utilslibrary.utils.jsonUtil
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.Constants.Companion.monthlyType
import net.sgztech.timeboat.config.Constants.Companion.weeklyType
import net.sgztech.timeboat.config.Constants.Companion.yearlyType
import net.sgztech.timeboat.config.HttpRequestConfig
import net.sgztech.timeboat.config.HttpRequestConfig.Companion.sleepMonitorYearly
import net.sgztech.timeboat.provide.dataModel.BleDeviceInfoModel
import net.sgztech.timeboat.provide.dataModel.SleepDailyListBean
import net.sgztech.timeboat.provide.dataModel.SleepStatisticsListBean
import net.sgztech.timeboat.provide.dataModel.UserLoginModel
import okhttp3.MediaType

class SleepStatisticsRepository:BaseRepository() {

    suspend fun getSleepStatisticsData(date :String ,type :Int): HttpResponseModel<SleepStatisticsListBean> {
        var url = HttpRequestConfig.sleepMonitorDaily
        when(type){
            weeklyType ->url= HttpRequestConfig.sleepMonitorWeekly
            monthlyType -> url =HttpRequestConfig.sleepMonitorMonthly
            yearlyType -> url =HttpRequestConfig.sleepMonitorYearly
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

        var bleInfo = SharedPreferencesUtil.getInstance().getString(Constants.BLE_DEVICE_INFO)
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

        val responseModel= await(requestCall ,object :Parser<SleepStatisticsListBean>{
            override fun onParse(response: String): HttpResponseModel<SleepStatisticsListBean> {
                return jsonUtil.parseRespondModel(
                    response,
                    SleepStatisticsListBean::class.java
                )
            }
        })
        return responseModel
    }
}