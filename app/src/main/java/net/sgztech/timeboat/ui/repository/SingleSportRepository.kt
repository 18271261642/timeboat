package net.sgztech.timeboat.ui.repository

import com.alibaba.fastjson.JSON
import com.imlaidian.okhttp.OkHttpUtils
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil
import com.imlaidian.utilslibrary.utils.jsonUtil
import net.sgztech.timeboat.bleCommand.CommandType.Companion.BADMINTON
import net.sgztech.timeboat.bleCommand.CommandType.Companion.BASKETBALL
import net.sgztech.timeboat.bleCommand.CommandType.Companion.FOOTBALL
import net.sgztech.timeboat.bleCommand.CommandType.Companion.HIKING
import net.sgztech.timeboat.bleCommand.CommandType.Companion.MOUNTAINEERING
import net.sgztech.timeboat.bleCommand.CommandType.Companion.RIDE
import net.sgztech.timeboat.bleCommand.CommandType.Companion.RUNNING
import net.sgztech.timeboat.bleCommand.CommandType.Companion.RUNNING_MACHINE
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.HttpRequestConfig
import net.sgztech.timeboat.provide.dataModel.BleDeviceInfoModel
import net.sgztech.timeboat.provide.dataModel.SingleSportItemModel
import net.sgztech.timeboat.provide.dataModel.SleepDailyListBean
import net.sgztech.timeboat.provide.dataModel.UserLoginModel
import okhttp3.MediaType

class SingleSportRepository :BaseRepository() {
    suspend fun getSportListData(sportType :Int ,startDate :String ,endDate :String): HttpResponseModel<List<SingleSportItemModel>> {
        var url = ""
        when(sportType){
            RUNNING.toInt() ->url = HttpRequestConfig.runList
            HIKING.toInt() -> url =HttpRequestConfig.hikingList
            RIDE.toInt()-> url =HttpRequestConfig.rideList
            MOUNTAINEERING.toInt() -> url =HttpRequestConfig.mountaineeringList
            RUNNING_MACHINE.toInt()->  url =HttpRequestConfig.runningMachineList
            BADMINTON.toInt() ->  url =HttpRequestConfig.badmintonlist
            BASKETBALL.toInt()->  url =HttpRequestConfig.basketballList
            FOOTBALL.toInt()-> url =HttpRequestConfig.footballlist
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
            params["beginDate"] =startDate
            params["endDate"] = endDate

        var bleInfo = SharedPreferencesUtil.getInstance().getString(Constants.BLE_DEVICE_INFO)
        val bleModel = jsonUtil.getObject(bleInfo , BleDeviceInfoModel::class.java)
        if(bleModel!=null){
            params["imei"] = bleModel.imei
        }else{
            params["imei"] = ""
        }

        val request = JSON.toJSONString(params)
        val requestCall = OkHttpUtils
            .postString()
            .url(url)
            .content(request)
            .mediaType(MediaType.parse("application/json;charset=utf-8"))
            .addHeader("Content-Type" ,"application/json; charset=UTF-8")
            .build()

        val responseModel= await(requestCall ,object : BaseRepository.Parser<List<SingleSportItemModel>> {
            override fun onParse(response: String): HttpResponseModel<List<SingleSportItemModel>> {
                return jsonUtil.parseRespondListModel(
                    response,
                    SingleSportItemModel::class.java
                )
            }
        })
        return responseModel
    }

}