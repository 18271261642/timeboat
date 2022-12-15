package net.sgztech.timeboat.ui.repository

import com.alibaba.fastjson.JSON
import com.imlaidian.okhttp.OkHttpUtils
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import com.imlaidian.utilslibrary.utils.DateUtil
import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil
import com.imlaidian.utilslibrary.utils.jsonUtil
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.Constants.Companion.dailyType
import net.sgztech.timeboat.config.Constants.Companion.monthlyType
import net.sgztech.timeboat.config.Constants.Companion.weeklyType
import net.sgztech.timeboat.config.Constants.Companion.yearlyType
import net.sgztech.timeboat.config.HttpRequestConfig
import net.sgztech.timeboat.config.HttpRequestConfig.Companion.bloodOxygenDaily
import net.sgztech.timeboat.config.HttpRequestConfig.Companion.bloodOxygenMonthly
import net.sgztech.timeboat.config.HttpRequestConfig.Companion.bloodOxygenWeekly
import net.sgztech.timeboat.config.HttpRequestConfig.Companion.bloodOxygenYearly
import net.sgztech.timeboat.provide.dataModel.*
import okhttp3.MediaType

class BloodOxygenRepository :BaseRepository(){
    suspend fun getBloodOx(date :String,type :Int): HttpResponseModel<BloodOxygenBean> {
        var url = ""
        when(type){
            dailyType -> url =bloodOxygenDaily
            weeklyType -> url =bloodOxygenWeekly
            monthlyType -> url =bloodOxygenMonthly
            yearlyType -> url = bloodOxygenYearly
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

        val responseModel= await(requestCall ,object :Parser<BloodOxygenBean>{
            override fun onParse(response: String): HttpResponseModel<BloodOxygenBean> {
                return jsonUtil.parseRespondModel(
                    response,
                    BloodOxygenBean::class.java
                )
            }
        })
        return responseModel
    }

}