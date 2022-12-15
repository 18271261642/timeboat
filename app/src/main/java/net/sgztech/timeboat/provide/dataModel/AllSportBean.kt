package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class AllSportBean :Serializable {
    var beginDate = ""
    var endDate =""
    var dayList = ArrayList<AllSportStepModel>()
    var sportList = ArrayList<AllSportTypeDataInfoModel>()
    var monthList = ArrayList<AllSportStepModel>()

    var totalCalorie =0
    var totalCount =0
    var totalDistance =0
    var totalTime =0

}