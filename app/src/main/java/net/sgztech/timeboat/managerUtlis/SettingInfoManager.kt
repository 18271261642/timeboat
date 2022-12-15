package net.sgztech.timeboat.managerUtlis

import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil
import com.imlaidian.utilslibrary.utils.jsonUtil
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.netty.NettyConstant.Client.READ_DATA_TIMEOUT_MILLISECOND
import net.sgztech.timeboat.provide.dataModel.BleDeviceInfoModel
import net.sgztech.timeboat.provide.dataModel.UserLoginModel

class SettingInfoManager {
    var accessToken = ""
    var usePhone = ""
    var isFirstInApp = false
    var bleInfo: BleDeviceInfoModel? = null
    var macAddress: String? = ""
    var deviceImei: String? = ""
    var userModel: UserLoginModel? = null
    var bleNameList = "QT20"
    var tcpPort = ""
    var tcpHost = ""
    var heartBeatInterval = READ_DATA_TIMEOUT_MILLISECOND

    companion object {
        val instance: SettingInfoManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            SettingInfoManager()
        }
    }

    fun checkLocalInfo() {
        val userInfo = SharedPreferencesUtil.getInstance().getString(Constants.USER_INFO)
        userModel = jsonUtil.getObject(userInfo, UserLoginModel::class.java)
        if (userModel != null) {
            accessToken = userModel!!.token
        } else {
            accessToken = ""
        }
        if (userModel != null) {
            usePhone = userModel!!.phone
        } else {
            usePhone = ""
        }

        val info = SharedPreferencesUtil.getInstance().getString(Constants.BLE_DEVICE_INFO)
        bleInfo = jsonUtil.getObject(info, BleDeviceInfoModel::class.java)
        if (bleInfo != null) {
            macAddress = bleInfo!!.mac
            deviceImei = bleInfo!!.imei
        } else {
            macAddress = ""
            deviceImei = ""
        }
    }

    fun refreshUserInfo(userModel: UserLoginModel) {
        if (userModel != null) {
            val userInfo = jsonUtil.getObject(userModel)
            accessToken = userModel.token
            usePhone = userModel.phone
            this.userModel = userModel
            SharedPreferencesUtil.getInstance().setString(Constants.USER_INFO, userInfo)
        }

    }

    fun cleanUserInfo() {
        this.userModel = null
        accessToken = ""
        usePhone = ""
        SharedPreferencesUtil.getInstance().setString(Constants.USER_INFO, "")

    }

    fun isAccessTokenNotEmpty(): Boolean {
        return accessToken.isNotEmpty()
    }

    fun isPhoneEmpty(): Boolean {
        return usePhone.isEmpty()
    }


    fun isFirstIntoApp(): Boolean {

        isFirstInApp =
            SharedPreferencesUtil.getInstance().getBoolean(Constants.FIRST_INTO_APP, true)

        return isFirstInApp
    }

    fun refreshBleModel(bleDevice: BleDeviceInfoModel) {
        macAddress = bleDevice.mac
        val shortMac = bleDevice.mac.replace(":", "")
        bleDevice.shortMac = shortMac
        SharedPreferencesUtil.getInstance()
            .setString(Constants.BLE_DEVICE_INFO, jsonUtil.getObject(bleDevice))
        bleInfo = bleDevice
    }

    fun getBleModel(): BleDeviceInfoModel? {
        if (bleInfo == null) {
            val info = SharedPreferencesUtil.getInstance().getString(Constants.BLE_DEVICE_INFO)
            bleInfo = jsonUtil.getObject(info, BleDeviceInfoModel::class.java)
            if (bleInfo != null) {
                macAddress = bleInfo!!.mac
            }

        }
        return bleInfo
    }

    fun cleanBleModel() {
        this.bleInfo = null
        macAddress = ""
        deviceImei = ""
        SharedPreferencesUtil.getInstance().setString(Constants.BLE_DEVICE_INFO, "")
    }


    fun changeHeartBeatInterval(time: Int): Long {
        if (time != 0) {
            heartBeatInterval = time * 1000L
        }

        return heartBeatInterval
    }
}