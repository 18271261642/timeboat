package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class TotalStepInfoModel:Serializable{
   var totalCalorie = 0
   var totalDistance =0
   var totalStepCount=0
   var averageCalorie =0
   var averageDistance =0
   var averageStepCount =0

}