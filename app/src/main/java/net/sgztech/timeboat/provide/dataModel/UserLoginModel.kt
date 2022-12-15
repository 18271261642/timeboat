package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable
@Keep
class UserLoginModel :Serializable {
    var  avatarUrl: String?=null
    var  birthday: String?=null
    //性别  性别,1-男/2-女
    var  gender: Int = 0
    //身高,单位CM
    var height: Int = 0
    //是否新用户,0-老用户/1-新用户
    var isNewUser: Int = 0
    //用户昵称
    var  nickname: String?=null
    var  openId: String?=null
    //用户电话
    var  phone: String=""
    //登录凭证
    var  token: String =""
    //用户UUID
    var  userUUID: String=""
    //用户名称
    var  username: String=""
    //体重,单位KG
    var  weight: String=""
}