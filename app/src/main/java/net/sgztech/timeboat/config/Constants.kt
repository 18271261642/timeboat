package net.sgztech.timeboat.config

class Constants {
    companion object {
        var BLE_NAME_LIST = "QT20"
        const val MAC_Address = "macAddress"
        const val PHONE_NUMBER = "phone_number"
        const val USER_INFO = "user_info"
        const val QUITE_CANCEL_ACCOUNT = "quite_cancel_account"
        const val WEB_VIEW_URL_TYPE = "web_view_url_type"
        const val WEB_VIEW_URL = "web_view_url"
        const val WEB_VIEW_TITLE = "web_view_title"

        const val  DEL_DEVICES_CODE = "del_devices_code"
        const val  DEL_DEVICES_STATUS = "del_devices_status"
        const val QUITE_TO_HOME_PAGE = "quite_home_page"
        const val LAUNCHER_AD_LIST = "launcher_ad_list"
        const val FIRST_INTO_APP = "first_in_to_app"
        const val BLE_DEVICE_INFO = "ble_device_info"
        const val FRAGMENT_LABEL_MAIN = "main"
        const val FRAGMENT_LABEL_ABOUT_ME = "about_me"
        const val FRAGMENT_LABEL_DEVICES = "devices"
        const val SAVE_FOLDER = "sgz"
        const val HOME_SPORT_DATA_TYPE ="home_sport_data_type"

        const val SPORT_TYPE_START_DATE = "sport_type_start_date"
        const val SPORT_TYPE_END_DATE = "sport_type_end_date"
        const val SPORT_TYPE_NAME= "sport_type_name"
        const val SPORT_TYPE_VALUE= "sport_type_value"
        const val SPORT_TYPE_ICON_URL = "sport_type_icon_url"


        // ble head 长度为8
        const val HEADER_CMD_LENGTH = 8

        const val REPOSITORY_RESULT_TOKEN_INVALID = -4

        const val MAX_CALORIE = 140   // 1400千卡
        const val MAX_DISTANCE = 10  //10公里
        const val MAX_STEP_COUNT = 6000   //6000步
        //微信
        const val WEIXIN_SCOPE = "snsapi_userinfo"
        const val WEIXIN_APPID = "wx2e4457735a32b970"

        const val PERMISSIONS_REQUEST_STORAGE = 1
        const val PERMISSIONS_NETWORK_STATE = 2
        const val PERMISSIONS_WIFI_STATE = 3
        const val PERMISSIONS_ACCESS_FINE_LOCATION = 4
        const val PERMISSIONS_ACCESS_COARSE_LOCATION = 5
        const val FRAGMENT_ARG_OBJECT = "fragment_arg"
        const val FRAGMENT_ARG_TYPE = "fragment_arg_type"

        //服务名称 18E0
        const val GATT_Service = "GATT_Service";
        const val GATT_Service_UUID = "000018e0-0000-1000-8000-00805f9b34fb"

        //手机端向手表写入命令或者推送同步信息
        const val Write_Char = "write_char";
        const val Write_Char_UUID = "00002ae0-0000-1000-8000-00805f9b34fb"

        //手表向手机发送通知手机端需要读取取数据
        const val Notification_Char = "notification_char";
        const val Notification_Char_UUID = "00002ae1-0000-1000-8000-00805f9b34fb"

        //手机端读取手表数据的端口
        const val Read_Char = "read_char"
        const val Read_Char_UUID = "00002ae2-0000-1000-8000-00805f9b34fb"
        const val  oneMinute = 60*1000
        const val  oneHour = 60* oneMinute
        const val  oneDay = 24*oneHour
        const val  oneWeek = 7*oneDay
        const val  dailyType = 1
        const val  weeklyType = 2
        const val  monthlyType = 3
        const val  yearlyType = 4


        //正式的环境
        const val APK_VERSION_RELEASE = 0

        //预发布环境
        const val APK_VERSION_PRE_RELEASE = 1

        //测试环境
        const val APK_VERSION_DEBUG = 2


        var APK_VERSION_PATTERN: Int = APK_VERSION_RELEASE

        fun isReleaseEnv(): Boolean {
            return APK_VERSION_PATTERN == APK_VERSION_RELEASE
        }

        fun isPreEnv(): Boolean {
            return APK_VERSION_PATTERN == APK_VERSION_PRE_RELEASE
        }

        fun isDebugEnv(): Boolean {
            return APK_VERSION_PATTERN == APK_VERSION_DEBUG
        }


    }

}