package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Keep
data class WxLoginModel(
    @SerializedName("accessToken")
    val accessToken: String?,
    @SerializedName("errCode")
    val errCode: Int?,
    @SerializedName("errMsg")
    val errMsg: String?,
    @SerializedName("expiresIn")
    val expiresIn: Int?,
    @SerializedName("openId")
    val openId: String?,
    @SerializedName("refreshToken")
    val refreshToken: String?,
    @SerializedName("scope")
    val scope: String?,
    @SerializedName("unionId")
    val unionId: String?
):Serializable