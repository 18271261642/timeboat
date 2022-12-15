package net.sgztech.timeboat.ui.repository

import com.alibaba.fastjson.JSON
import com.imlaidian.okhttp.OkHttpUtils
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil
import com.imlaidian.utilslibrary.utils.jsonUtil
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.HttpRequestConfig
import net.sgztech.timeboat.provide.dataModel.BleDeviceInfoModel
import net.sgztech.timeboat.provide.dataModel.UserLoginModel
import okhttp3.MediaType

class DeleteDeviceRepository :BaseRepository() {

    suspend fun delDevice(imei:String,mac :String ,name:String?): HttpResponseModel<BleDeviceInfoModel> {
        val params: MutableMap<String, String> = HashMap()
        params["imei"] = imei
        params["mac"] = mac
        if(name==null){
            params["name"] = ""
        }else{
            params["name"] = name
        }

        val userInfo = SharedPreferencesUtil.getInstance().getString(Constants.USER_INFO)
        val userModel = jsonUtil.getObject(userInfo , UserLoginModel::class.java)
        if(userModel!=null){
            params["token"] = userModel.token
        }else{
            params["token"] = ""
        }
        val request = JSON.toJSONString(params)
        val requestCall = OkHttpUtils
            .postString()
            .url(HttpRequestConfig.deleteDevices)
            .content(request)
            .mediaType(MediaType.parse("application/json;charset=utf-8"))
            .addHeader("Content-Type" ,"application/json; charset=UTF-8")
            .build()

        val responseModel = await(requestCall ,object :Parser<BleDeviceInfoModel>{
            override fun onParse(response: String): HttpResponseModel<BleDeviceInfoModel> {
                return jsonUtil.parseRespondModel<BleDeviceInfoModel>(response ,
                    BleDeviceInfoModel::class.java)
            }
        })


        return  responseModel
    }
}