package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable
@Keep
class WalkDayModel : Serializable {
    var id :Int = 0
    var walkDate :String =""
    val walkCount :Int  = 0
}