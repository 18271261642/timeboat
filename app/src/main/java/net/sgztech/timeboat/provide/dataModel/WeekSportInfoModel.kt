package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable
@Keep
class WeekSportInfoModel  : Serializable {
    var totalTime =0
    var totalCalorie =0
    var dayList = ArrayList<WeekSportItemModel>()
}