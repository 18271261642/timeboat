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
import net.sgztech.timeboat.config.HttpRequestConfig.Companion.allSportMonthly
import net.sgztech.timeboat.config.HttpRequestConfig.Companion.allSportWeekly
import net.sgztech.timeboat.config.HttpRequestConfig.Companion.allSportYearly
import net.sgztech.timeboat.provide.dataModel.AllSportBean
import net.sgztech.timeboat.provide.dataModel.BleDeviceInfoModel
import net.sgztech.timeboat.provide.dataModel.UserLoginModel
import okhttp3.MediaType

class AllSportRepository :BaseRepository(){
    suspend fun getAllSportData(date :String ,type :Int): HttpResponseModel<AllSportBean> {

        var url = ""
        when(type){
            weeklyType ->  url =allSportWeekly
            monthlyType -> url =allSportMonthly
            yearlyType -> url =allSportYearly
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

        val responseModel= await(requestCall ,object :Parser<AllSportBean>{
            override fun onParse(response: String): HttpResponseModel<AllSportBean> {
                return jsonUtil.parseRespondModel(
                    response,
                    AllSportBean::class.java
                )
            }
        })
        return responseModel
    }
}