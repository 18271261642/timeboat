package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable
@Keep
class SingleSportItemModel :Serializable{
    var actualDistance =0
    var actualUseTime =0
    var calorie =0
    var date =""
    var totalStep = 0
}