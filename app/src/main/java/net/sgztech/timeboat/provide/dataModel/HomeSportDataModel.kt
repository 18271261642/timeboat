package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable
@Keep
class HomeSportDataModel: Serializable  {
    var bloodOxygen: BloodOxygenModel?=null
    var heartRate :HeartRateModel?=null
    var sleepMonitorState : SleepMonitorInfoModel?=null
    var totalStep :TotalStepModel?=null
    var bodyTemperature : BodyTemperatureModel ?=null
    var weekSportInfo = WeekSportInfoModel()
}