package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import org.json.JSONObject
import java.io.Serializable

@Keep
class AuthorCodeModel : Serializable {
    var phone :String? = null
    var code: String? = null
}