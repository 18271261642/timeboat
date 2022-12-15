package net.sgztech.timeboat.ui.repository

import com.alibaba.fastjson.JSON
import com.imlaidian.okhttp.OkHttpUtils
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import com.imlaidian.utilslibrary.utils.DateUtil
import com.imlaidian.utilslibrary.utils.LogUtil
import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil
import com.imlaidian.utilslibrary.utils.jsonUtil
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.Constants.Companion.dailyType
import net.sgztech.timeboat.config.Constants.Companion.monthlyType
import net.sgztech.timeboat.config.Constants.Companion.weeklyType
import net.sgztech.timeboat.config.Constants.Companion.yearlyType
import net.sgztech.timeboat.config.HttpRequestConfig
import net.sgztech.timeboat.provide.dataModel.*
import okhttp3.MediaType

class StepCountRepository:BaseRepository(){

    private val TAG = StepCountRepository::class.java.simpleName

    suspend fun getStepCount(date:String,type: Int): HttpResponseModel<TotalStepBean> {
        var url = ""
        when(type){
            dailyType -> url = HttpRequestConfig.totalStepDaily
            weeklyType -> url = HttpRequestConfig.totalStepWeekly
            monthlyType -> url = HttpRequestConfig.totalStepMonthly
            yearlyType -> url = HttpRequestConfig.totalStepYearly
            else -> {
                url = HttpRequestConfig.totalStepDaily
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

        val responseModel= await(requestCall ,object :
            BaseRepository.Parser<TotalStepBean> {
            override fun onParse(response: String): HttpResponseModel<TotalStepBean> {
                return jsonUtil.parseRespondModel(
                    response,
                    TotalStepBean::class.java
                )
            }
        })
        return responseModel
    }

}