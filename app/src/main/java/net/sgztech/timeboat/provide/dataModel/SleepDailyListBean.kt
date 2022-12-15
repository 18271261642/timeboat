package net.sgztech.timeboat.provide.dataModel

import java.io.Serializable

class SleepDailyListBean :Serializable{

    var info = SleepMonitorInfoModel()

    var list = ArrayList<SleepDailyModel>()

    var deepSleep =ArrayList<SleepDailyModel>()

    var lightSleep =ArrayList<SleepDailyModel>()

    var soberSleep =ArrayList<SleepDailyModel>()
}