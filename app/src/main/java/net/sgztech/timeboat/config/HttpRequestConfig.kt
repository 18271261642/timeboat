package net.sgztech.timeboat.config

import net.sgztech.timeboat.config.Constants.Companion.isReleaseEnv

class HttpRequestConfig {

    companion object{

        //正式环境：
        val httpsServerRelease= "https://app-pro.sgztech.net"
        //测试环境：
        val httpsServerDebug= "https://app-pro.sgztech.net"

        const val loginWxUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?"+"appid=%s&secret=%s&code=%s&grant_type=authorization_code";


        fun getHttpsEnv():String{
            return if(isReleaseEnv()){
                httpsServerRelease
            }else{
                httpsServerDebug
            }
        }
        //主页广告
         val homeAdListUrl =  getHttpsEnv()+"/mobile-app-api/ad/home-list"
        //启动页广告
        val launcherAdListUrl =  getHttpsEnv()+"/mobile-app-api/ad/launch-list"

         //发送登录短信
         val sendLoginSms  =  getHttpsEnv()+ "/mobile-app-api/user/send-login-sms"
        //更新用户信息
         val updateUserInfo = getHttpsEnv() + "/mobile-app-api/user/update-user-info"

        //发送绑定手机短信
        val sendBindingPhoneSms = getHttpsEnv() +"/mobile-app-api/user/send-bind-phone-sms"
        //绑定手机
        val bindingPhone = getHttpsEnv() +"/mobile-app-api/user/bind-phone"
        //查询用户信息
        val queryUserInfo = getHttpsEnv() + "/mobile-app-api/user/query-user-info"

        //获取微信登录信息
        val wechatLoginUrl = getHttpsEnv() + "/mobile-app-api/user/query-wechat-info"
        // 用户登录
        val userLoginUrl = getHttpsEnv() + "/mobile-app-api/user/login"

        //添加设备
        val addDevices = getHttpsEnv() + "/mobile-app-api/device/add"

        //设备删除
        val deleteDevices = getHttpsEnv() + "/mobile-app-api/device/delete"

        //设备列表
        val devicesList = getHttpsEnv() + "/mobile-app-api/device/list"

        //心率历史数据

        val heartRateDaily =  getHttpsEnv()+"/mobile-app-api/heart-rate/daily"
        val heartRateWeekly =  getHttpsEnv()+"/mobile-app-api/heart-rate/weekly"
        val heartRateMonthly =  getHttpsEnv()+"/mobile-app-api/heart-rate/monthly"
        val heartRateYearly =  getHttpsEnv()+"/mobile-app-api/heart-rate/yearly"
        //睡眠历史数据
        val sleepMonitor =  getHttpsEnv()+"/mobile-app-api/sleep-monitor/history"
        val sleepMonitorDaily =  getHttpsEnv()+"/mobile-app-api/sleep-monitor/daily"
        val sleepMonitorWeekly =  getHttpsEnv()+"/mobile-app-api/sleep-monitor/weekly"
        val sleepMonitorMonthly =  getHttpsEnv()+"/mobile-app-api/sleep-monitor/monthly"
        val sleepMonitorYearly =  getHttpsEnv()+"/mobile-app-api/sleep-monitor/yearly"
        //血氧历史数据
        val bloodOxygen =  getHttpsEnv()+"/mobile-app-api/blood-oxygen/history"
        val bloodOxygenDaily =  getHttpsEnv()+"/mobile-app-api/blood-oxygen/daily"
        val bloodOxygenWeekly =  getHttpsEnv()+"/mobile-app-api/blood-oxygen/weekly"
        val bloodOxygenMonthly =  getHttpsEnv()+"/mobile-app-api/blood-oxygen/monthly"
        val bloodOxygenYearly =  getHttpsEnv()+"/mobile-app-api/blood-oxygen/yearly"

        //体温相关接口
        val bodyTemperatureDaily =  getHttpsEnv()+"/mobile-app-api/body-temperature/daily"
        val bodyTemperatureWeekly =  getHttpsEnv()+"/mobile-app-api/body-temperature/weekly"
        val bodyTemperatureMonthly =  getHttpsEnv()+"/mobile-app-api/body-temperature/monthly"
        val bodyTemperatureYearly =  getHttpsEnv()+"/mobile-app-api/body-temperature/yearly"

        //总步数历史数据查询
        val totalStepDaily =  getHttpsEnv()+"/mobile-app-api/total-step/daily"
        val totalStepWeekly =  getHttpsEnv()+"/mobile-app-api/total-step/weekly"
        val totalStepMonthly =  getHttpsEnv()+"/mobile-app-api/total-step/monthly"
        val totalStepYearly =  getHttpsEnv()+"/mobile-app-api/total-step/yearly"

        //所有运动数据
        val allSportWeekly =  getHttpsEnv()+"/mobile-app-api/sport-data/weekly"
        val allSportMonthly =  getHttpsEnv()+"/mobile-app-api/sport-data/monthly"
        val allSportYearly =  getHttpsEnv()+"/mobile-app-api/sport-data/yearly"

        //注销用户
        val unsubscribeUserInfo =  getHttpsEnv()+"/mobile-app-api/user/unsubscribe-user-info"
        //注销协议
        val unsubscribeUserAgree =  getHttpsEnv()+"/mobile-app-api/user/unsubscribe-agreement"
        //历史版本信息查询
        val versionHistory =  getHttpsEnv()+"/mobile-app-api/version/history-info"
        //设备信息查询
        val homePageSport =  getHttpsEnv()+"/mobile-app-api/terminal-data/query"
        //配置信息相关接口
        val configUrlQuery = getHttpsEnv()+"/mobile-app-api/config-info/query"
        //运动-徒步
        var hikingList = getHttpsEnv()+"/mobile-app-api/hiking/list"
        //运动-登山
        var mountaineeringList = getHttpsEnv()+"/mobile-app-api/mountaineering/list"


        //运动-跑步机相关接口
        var  runningMachineList  = getHttpsEnv()+"/mobile-app-api/running-machine/list"

        // 跑步
        var  runList  = getHttpsEnv()+"/mobile-app-api/run/list"

        //运动-骑行相关接口
        var  rideList  = getHttpsEnv()+"/mobile-app-api/ride/list"

        //运动-篮球相关接口
        var basketballList = getHttpsEnv()+"/mobile-app-api/basketball/list"

       // 运动-羽毛球相关接口
       var  badmintonlist  = getHttpsEnv()+"/mobile-app-api/badminton/list"
        //足球
       var  footballlist  = getHttpsEnv()+"/mobile-app-api/football/list"




    }
}