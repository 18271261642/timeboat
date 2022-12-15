package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class HeartRateBean:Serializable {
    var list =ArrayList<HeartRateModel>()
    var info= HeartRateInfoModel()
}