package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable
@Keep
class BloodOxygenBean :Serializable{
    var list =ArrayList<BloodOxygenModel>()
    var info= BloodOxygenInfoModel()
}