package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable
@Keep
class AdInfoModel:Serializable {
    var adText =""
    //广告类型，0-文字/1-图片/2-视频
    var adType =0
    var adUrl =""
    var duration = 0
    var orderNumber =0
}