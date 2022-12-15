package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class BodyTemperatureBean :Serializable{
    var list =ArrayList<BodyTemperatureModel>()
    var info= BodyTemperatureInfoModel()
}