package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable
@Keep
class TotalStepModel :Serializable{
   // 日周月
   var id =0
   var calorie =0
   var stepCount= 0
   var distance =0
   var deviceTime =""
   //周月数据
   var date =""

   //年数据
   var month =0
   var totalCalorie =0
   var totalDistance =0
   var totalStepCount =0

}