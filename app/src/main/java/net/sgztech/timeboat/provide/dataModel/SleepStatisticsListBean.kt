package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable
@Keep
class SleepStatisticsListBean:Serializable {
    var info = SleepMonitorInfoModel()

    var list = ArrayList<SleepStatisticsModel>()
}