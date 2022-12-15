package net.sgztech.timeboat.ui.repository

import com.alibaba.fastjson.JSON
import com.imlaidian.okhttp.OkHttpUtils
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import com.imlaidian.utilslibrary.utils.LogUtil
import com.imlaidian.utilslibrary.utils.jsonUtil
import net.sgztech.timeboat.config.HttpRequestConfig
import net.sgztech.timeboat.provide.dataModel.WxUserInfoModel
import okhttp3.MediaType

class WxLoginRepository :BaseRepository(){
    private val tag =WxLoginRepository::class.java.simpleName
    suspend fun getData(code:String): HttpResponseModel<WxUserInfoModel>{
        val params: MutableMap<String, String> = HashMap()
        params["code"] =code
        val request = JSON.toJSONString(params)
        LogUtil.d(tag , "WxLoginRepository request=" +request)
        var requestCall = OkHttpUtils
            .postString()
            .url(HttpRequestConfig.wechatLoginUrl)
            .content(request)
            .mediaType(MediaType.parse("application/json;charset=utf-8"))
            .addHeader("Content-Type" ,"application/json; charset=UTF-8")
            .build()

        val responseModel = await(requestCall,object : BaseRepository.Parser<WxUserInfoModel> {
            override fun onParse(response: String): HttpResponseModel<WxUserInfoModel> {
                return jsonUtil.parseRespondModel<WxUserInfoModel>(response ,
                    WxUserInfoModel::class.java)
            }
        })
        return responseModel
    }

}