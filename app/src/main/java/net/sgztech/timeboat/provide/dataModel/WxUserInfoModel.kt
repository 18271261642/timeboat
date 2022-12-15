package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable
@Keep
class WxUserInfoModel :Serializable{
    var openId :String? =null
    var nickname :String? =null
    var sex :Int =0
    var province :String? =null
    var city :String? =null
    var headImgUrl :String? =null
    var unionid :String? =null
    var errCode :Int =0
    var errMsg :String? =null
}