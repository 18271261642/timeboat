package net.sgztech.timeboat.provide.dataModel

import com.alibaba.fastjson.JSON
import com.imlaidian.okhttp.OkHttpUtils
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil
import com.imlaidian.utilslibrary.utils.jsonUtil
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.HttpRequestConfig
import net.sgztech.timeboat.ui.repository.BaseRepository
import okhttp3.MediaType

class DevicesListRepository :BaseRepository(){

    suspend fun getDeviceList(): HttpResponseModel<List<BleDeviceInfoModel>> {
        val url = HttpRequestConfig.devicesList
        val params: MutableMap<String, String> = HashMap()
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
            .url(url)
            .content(request)
            .mediaType(MediaType.parse("application/json;charset=utf-8"))
            .addHeader("Content-Type" ,"application/json; charset=UTF-8")
            .build()

        val responseModel= await(requestCall ,object : BaseRepository.Parser<List<BleDeviceInfoModel>> {
            override fun onParse(response: String): HttpResponseModel<List<BleDeviceInfoModel>> {
                return jsonUtil.parseRespondListModel(
                    response,
                    BleDeviceInfoModel::class.java
                )
            }
        })
        return responseModel
    }
}