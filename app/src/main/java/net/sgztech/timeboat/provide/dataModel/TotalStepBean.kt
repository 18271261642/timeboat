package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable
@Keep
class TotalStepBean :Serializable{
    var list = ArrayList<TotalStepModel>()
    var info =TotalStepInfoModel()
}