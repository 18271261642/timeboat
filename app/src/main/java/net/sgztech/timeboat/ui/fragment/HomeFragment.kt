package net.sgztech.timeboat.ui.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.device.rxble.RxBleConnection
import com.device.ui.baseUi.baseFragment.BaseFragment
import com.device.ui.viewBinding.viewBinding
import com.device.ui.viewModel.common.vmObserver
import com.imlaidian.utilslibrary.utils.LogUtil
import com.imlaidian.utilslibrary.utils.UToast
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.Constants.Companion.HOME_SPORT_DATA_TYPE
import net.sgztech.timeboat.config.Constants.Companion.MAX_CALORIE
import net.sgztech.timeboat.config.Constants.Companion.MAX_DISTANCE
import net.sgztech.timeboat.config.Constants.Companion.MAX_STEP_COUNT
import net.sgztech.timeboat.databinding.FragmentHomeBinding
import net.sgztech.timeboat.managerUtlis.*
import net.sgztech.timeboat.provide.dataModel.BleConnectEvent
import net.sgztech.timeboat.provide.dataModel.ExitAccountStatusEvent
import net.sgztech.timeboat.provide.dataModel.HomeSportDataModel
import net.sgztech.timeboat.provide.viewModel.HomeViewModel
import net.sgztech.timeboat.ui.activity.*
import net.sgztech.timeboat.ui.activity.HeatRtOrTempActivity.Companion.heartRateType
import net.sgztech.timeboat.ui.activity.HeatRtOrTempActivity.Companion.temperatureType
import net.sgztech.timeboat.ui.activity.WalkDataActivity.Companion.calorieType
import net.sgztech.timeboat.ui.activity.WalkDataActivity.Companion.distanceType
import net.sgztech.timeboat.ui.activity.WalkDataActivity.Companion.stepCountType
import net.sgztech.timeboat.ui.adapter.BannerListAdapter
import net.sgztech.timeboat.util.checkLocationPermission
import net.sgztech.timeboat.util.initLocationPermission
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


class HomeFragment : BaseFragment(), OnRefreshListener, OnLoadMoreListener {
    private var TAG = HomeFragment::class.java.simpleName
    private val homeBinding: FragmentHomeBinding by viewBinding()
    private val homeViewModel: HomeViewModel by viewModels()
    var localRequest = true
    private val resultsAdapter =
        BannerListAdapter { result, position ->

//                val intent  = Intent(activity , BleCommandActivity::class.java)
//                intent.putExtra(Constants.MAC_Address ,result.adUrl )
//                startActivity(intent)
        }

    fun newInstance(): HomeFragment {
        val fragment: HomeFragment =
            HomeFragment()
        fragment.setLabel(Constants.FRAGMENT_LABEL_MAIN)
        fragment.setMainPage(true)
        return fragment
    }


    override fun getLayoutResId(): Int {
        return R.layout.fragment_home
    }


    private fun configureResultList() {
        with(homeBinding.bannerList) {
            setHasFixedSize(true)
            itemAnimator = null
            adapter = resultsAdapter
        }
        homeViewModel.adListData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: BleConnectEvent) {
        var status = event.connectStatus
        updateBleStatusUi(status)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ExitAccountStatusEvent) {
        resetHomePageUi()
    }

    private fun resetHomePageUi(){

        homeBinding.totalStep.text = "--"
        homeBinding.killoMeterCount.text ="--"
        homeBinding.hotCount.text = "--"
        homeBinding.walkCount.text = "--"
        homeBinding.mulProgress.calorieProgress.setProgress(0, true)
        homeBinding.mulProgress.distanceProgress.setProgress(0, true)
        homeBinding.mulProgress.stepProgress.setProgress(0, true)

        homeBinding.sleepTime.text = "-时-分"
        homeBinding.shallowSleepTime.text = "-时-分"
        homeBinding.deepSleepTime.text = "-时-分"
        homeBinding.sleepTotalAllDayProgress.isInvisible =true
        homeBinding.shallowSleepProgress.isInvisible =true
        homeBinding.deepSleepProgress.isInvisible =true


        homeBinding.heartMinCount.text = "--"
        homeBinding.bloodOx.text = "--"
        homeBinding.tempValue.text = "--"

        homeBinding.monday.progress  =0
        homeBinding.tuesday.progress  =0
        homeBinding.wednesday.progress  =0
        homeBinding.thursday.progress  =0
        homeBinding.friday.progress  =0
        homeBinding.saturday.progress  =0
        homeBinding.sunday.progress  =0
        homeBinding.sportDescribe.text="暂无运动，请继续努力呀~"

        homeBinding.weekTotalTime.text="0秒"


        homeBinding.lngData.text = "--"
        homeBinding.latData.text = "--"
        homeBinding.altitudeData.text = "--"
    }

    private fun updateConnect() {
        val status = BleServiceManager.instance.getBleConnectStatus()
        updateBleStatusUi(status)
    }

    private fun updateBleStatusUi(status :RxBleConnection.RxBleConnectionState?){
        when (status) {
            RxBleConnection.RxBleConnectionState.CONNECTING -> homeBinding.deviceStatus.text =
                "智能手表正在连接"
            RxBleConnection.RxBleConnectionState.CONNECTED -> homeBinding.deviceStatus.text =
                "智能手表已连接"
            RxBleConnection.RxBleConnectionState.DISCONNECTED -> homeBinding.deviceStatus.text =
                "智能手表已断开"
            RxBleConnection.RxBleConnectionState.DISCONNECTING -> homeBinding.deviceStatus.text =
                "智能手表已断开"
            else -> {
                homeBinding.deviceStatus.text = "智能手表未连接"
            }
        }
    }

    private fun getSportData() :Boolean{
        var refresh =false
        val bleInfo = SettingInfoManager.instance.bleInfo
        if (bleInfo != null) {
            if (bleInfo.imei != null && bleInfo.imei.isNotEmpty()) {
                homeViewModel.sportData()
                refresh = true
            }

        }
        return refresh
    }

    fun getDeviceListData() {
        if (SettingInfoManager.instance.isAccessTokenNotEmpty()) {
            homeViewModel.deviceListData()
            localRequest = activity!!.checkLocationPermission()
            if (localRequest) {
//                LocationAddressManager.instance.getLatLng()
                activity?.initLocationPermission()
            }
        }
    }

    override fun initBindView() {
        EventBus.getDefault().register(this);
        homeBinding.progressLayout.setOnClickListener(this)
        homeBinding.distanceLayout.setOnClickListener(this)
        homeBinding.calorieLayout.setOnClickListener(this)
        homeBinding.stepLayout.setOnClickListener(this)
        homeBinding.sleepLayout.setOnClickListener(this)
        homeBinding.heartRateLayout.setOnClickListener(this)
        homeBinding.bloodOxLayout.setOnClickListener(this)
        homeBinding.bodyTemperatureLayout.setOnClickListener(this)
//        refreshUi =bindView<>(R.id.refreshLayout)
        homeBinding.refreshLayout.setOnRefreshListener(this)
        homeBinding.weekSport.setOnClickListener(this)

    }

    override fun initData() {
        homeViewModel.homeAdListData.vmObserver(this) {
            onAppLoading = {
                LogUtil.d(TAG, "开始")
            }
            onAppSuccess = {
                if (it != null) {
                    resultsAdapter.refreshData(it)
                }else{
                    homeBinding.refreshLayout.finishRefresh(true);
                }

            }
            onAppError = { msg, errorCode ->
                if (errorCode == Constants.REPOSITORY_RESULT_TOKEN_INVALID) {
                    quiteLogin()
                    UToast.showShortToast("请重新登录")
                    homeBinding.refreshLayout.finishRefresh(true);
                } else {
                    UToast.showShortToast(msg)
                    homeBinding.refreshLayout.finishRefresh(true);
                }
            }
            onAppComplete = {
                if (!SettingInfoManager.instance.isAccessTokenNotEmpty()) {
                    homeBinding.refreshLayout.finishRefresh(true);
                }
                LogUtil.d(TAG, "onAppComplete 完成")
            }
        }

        homeViewModel.deviceData.vmObserver(this) {

            onAppLoading = {
                LogUtil.d(TAG, "开始")
            }
            onAppSuccess = {
                if (it != null) {
                   if( !getSportData()){
                       homeBinding.refreshLayout.finishRefresh(true);
                   }
                }else{
                    homeBinding.refreshLayout.finishRefresh(true);
                }
            }
            onAppError = { msg, errorCode ->
                LogUtil.d(TAG, "error it=" + msg)
                UToast.showShortToast(msg)
                if (errorCode == Constants.REPOSITORY_RESULT_TOKEN_INVALID) {
                    quiteLogin()
                    UToast.showShortToast("请重新登录")
                } else {
                    UToast.showShortToast(msg)
                }
                homeBinding.refreshLayout.finishRefresh(true);
            }
            onAppComplete = {
                LogUtil.d(TAG, "onAppComplete 完成")

            }
        }


        homeViewModel.homeSportData.vmObserver(this) {

            onAppLoading = {
                LogUtil.d(TAG, "开始")
            }
            onAppSuccess = {
                if (it != null) {
                    updatePortData(it)
                }

            }
            onAppError = { msg, errorCode ->
                LogUtil.d(TAG, "error it=" + msg)
                UToast.showShortToast(msg)
                if (errorCode == Constants.REPOSITORY_RESULT_TOKEN_INVALID) {
                    quiteLogin()
                    UToast.showShortToast("请重新登录")
                } else {
                    UToast.showShortToast(msg)
                }
            }
            onAppComplete = {
                LogUtil.d(TAG, "onAppComplete 完成")
                homeBinding.refreshLayout.finishRefresh(true);
            }


        }

        BleServiceManager.instance.registerNotify(object: BleServiceManager.NotifyInfoListen{
            override fun notify(notify: ByteArray, describe: String) {
                delayUpdateDeviceData()
            }
        })

        configureResultList()
        getDeviceListData()

    }

    private fun quiteLogin(){
        SettingInfoManager.instance.cleanUserInfo()
        SettingInfoManager.instance.cleanBleModel()
        //蓝牙断开连接
        BleServiceManager.instance.disconnectAndClean()
    }

    private fun delayUpdateDeviceData(){
        LogUtil.d(TAG, "delayUpdateDeviceData")
        val t = Timer()
        t.schedule(object : TimerTask() {
            override fun run() {
                LogUtil.d(TAG, "delayUpdateDeviceData ")
                if (SettingInfoManager.instance.isAccessTokenNotEmpty()) {
                    homeViewModel.deviceListData()
                }
                t.cancel()
            }
        }, 2000)
    }

    private fun updatePortData(data: HomeSportDataModel?) {
//        var dateNew = false
        if (data != null) {
            if (data.totalStep != null) {
//                dateNew = true
                val calorie = (data.totalStep!!.calorie.toDouble() / 1000)
                val distance = (data.totalStep!!.distance.toDouble() / 1000)
                val stepCount = data.totalStep!!.stepCount
                val calRatio = (calorie * 100 / MAX_CALORIE).toInt()
                val distanceRatio = (distance * 100 / MAX_DISTANCE).toInt()
                val stepRatio = stepCount * 100 / MAX_STEP_COUNT
                homeBinding.totalStep.text = "" + stepCount
                homeBinding.killoMeterCount.text = StringsUtils.getFormatDecimal(distance, 2)
                homeBinding.hotCount.text = StringsUtils.getFormatDecimal(calorie, 2)
                homeBinding.walkCount.text = "" + stepCount
                homeBinding.mulProgress.calorieProgress.setProgress(calRatio, true)
                homeBinding.mulProgress.distanceProgress.setProgress(distanceRatio, true)
                homeBinding.mulProgress.stepProgress.setProgress(stepRatio, true)
            }

            if (data.sleepMonitorState != null) {
                homeBinding.sleepTotalAllDayProgress.isVisible =true
                homeBinding.shallowSleepProgress.isVisible =true
                homeBinding.deepSleepProgress.isVisible =true
//                dateNew = true
                val totalMinute = data.sleepMonitorState!!.totalMinute
                val totalDeepSleepMinute = data.sleepMonitorState!!.totalDeepSleepMinute
                val totalLightSleepMinute = data.sleepMonitorState!!.totalLightSleepMinute
                val totalSoberMinute = data.sleepMonitorState!!.totalSoberMinute


//                val totalDeepSleepMinute = 3*60
//                val totalLightSleepMinute = 4*60
//                val totalSoberMinute =7*60
                homeBinding.sleepTime.text = minuteToHour(totalMinute)
                homeBinding.shallowSleepTime.text = minuteToHour(totalLightSleepMinute)
                homeBinding.deepSleepTime.text = minuteToHour(totalDeepSleepMinute)

                val allDayProgressLength = homeBinding.sleepTotalAllDayProgress.width

                var shallowProgressRatio = 0
                if (totalMinute > 0) {
                    shallowProgressRatio = allDayProgressLength * totalLightSleepMinute / totalMinute
                }

                homeBinding.shallowSleepProgress.layoutParams.width = shallowProgressRatio

                var deepProgressLength = 0
                if (totalMinute > 0) {
                    deepProgressLength =allDayProgressLength * totalDeepSleepMinute / totalMinute
                }

                homeBinding.deepSleepProgress.layoutParams.width = deepProgressLength


                homeBinding.shallowSleepProgress.isVisible =true
                homeBinding.deepSleepProgress.isVisible =true
            }else{
                homeBinding.sleepTotalAllDayProgress.isGone =true
                homeBinding.shallowSleepProgress.isGone =true
                homeBinding.deepSleepProgress.isGone =true
            }

            if (data.heartRate != null) {
//                dateNew = true
                val heartRate = data.heartRate!!.heartRate
//               val heartRate = 80
                homeBinding.heartMinCount.text = "" + heartRate
            }

            if (data.bloodOxygen != null) {
//                dateNew = true
                val bloodOx = data.bloodOxygen!!.bloodOxygen
//               val bloodOx= 70
                homeBinding.bloodOx.text = "" + bloodOx
            }

            if (data.bodyTemperature != null) {
//                dateNew = true
                val bodyTp = data.bodyTemperature!!.bodyTemperature
                homeBinding.tempValue.text = "" + bodyTp
            }
//            homeBinding.dateStatus.isVisible =dateNew

            if(data.weekSportInfo!=null){

                val dayList =data.weekSportInfo.dayList

                var  count = 0
                for(i in dayList.indices){
                    var time =dayList[i].useTime
                    if(time>0){
                        count++
                    }
                    when(i){
                        0 ->{
                            if(time>0){
                                homeBinding.monday.progress  =100
                            }else{
                                homeBinding.monday.progress  =0
                            }
                        }
                        1-> {
                            if(time>0){
                                homeBinding.tuesday.progress  =100
                            }else{
                                homeBinding.tuesday.progress  =0
                            }
                        }
                        2-> {
                            if(time>0){
                                homeBinding.wednesday.progress  =100
                            }else{
                                homeBinding.wednesday.progress  =0
                            }
                        }
                        3-> {
                            if(time>0){
                                homeBinding.thursday.progress  =100
                            }else{
                                homeBinding.thursday.progress  =0
                            }
                        }
                        4-> {
                            if(time>0){
                                homeBinding.friday.progress  =100
                            }else{
                                homeBinding.friday.progress  =0
                            }
                        }
                        5-> {
                            if(time>0){
                                homeBinding.saturday.progress  =100
                            }else{
                                homeBinding.saturday.progress  =0
                            }

                        }
                        6-> {
                            if(time>0){
                                homeBinding.sunday.progress  =100
                            }else{
                                homeBinding.sunday.progress  =0
                            }
                        }
                    }
                }
                if(count >0){
                    homeBinding.sportDescribe.text="共运动"+ count+ "天，很棒哦~"
                }else{
                    homeBinding.sportDescribe.text="暂无运动，请继续努力呀~"
                }


                homeBinding.weekTotalTime.text=secondToHour(data.weekSportInfo.totalTime)
            }

            homeBinding.lngData.text = LocationAddressManager.instance.longitude
            homeBinding.latData.text = LocationAddressManager.instance.latitude
            homeBinding.altitudeData.text = LocationAddressManager.instance.altitude
        }else{
//            homeBinding.dateStatus.isVisible =false
        }
        updateConnect()
    }

    private fun secondToHour(totalSecond: Int): String {

        val second = totalSecond % 60
        val totalMinute = totalSecond / 60
        val minute = totalMinute % 60
        val hour = totalMinute / 60
        val time = StringBuffer()
        if (hour > 0) {
            val hourString = "" + hour + "小时"
            time.append(hourString)
        }
        if (minute > 0) {
            val minuteString = "" + minute + "分"
            time.append(minuteString)
        }

        val secondString = "" + second + "秒"
        time.append(secondString)

        return time.toString()
    }

    private fun minuteToHour(totalMinute: Int): String {
        val hour = totalMinute / 60
        val minute = totalMinute % 60
        val time = StringBuffer()
        if (hour > 0) {
            val hourString = "" + hour + "小时"
            time.append(hourString)
        }
        if (minute > 0) {
            var minuteString = "" + minute + "分"
            time.append(minuteString)
        }
        return time.toString()
    }

    override fun widgetClick(v: View) {
        super.widgetClick(v)
        if (SettingInfoManager.instance.isAccessTokenNotEmpty()) {
            if (SettingInfoManager.instance.isPhoneEmpty()) {
                (activity as MainActivity).showBinding()
            } else {
                onClickEvent(v)
            }
        } else {
            (activity as MainActivity).showLogin()
        }
    }

    private fun onClickEvent(v: View) {

        when (v.id) {
            R.id.step_layout,
            R.id.progress_layout -> {
                val intent = Intent(activity, WalkDataActivity::class.java)
                intent.putExtra(HOME_SPORT_DATA_TYPE, stepCountType)
                activity!!.startActivity(intent)
            }

            R.id.distance_layout -> {
                val intent = Intent(activity, WalkDataActivity::class.java)
                intent.putExtra(HOME_SPORT_DATA_TYPE, distanceType)
                activity!!.startActivity(intent)
            }

            R.id.calorie_layout -> {
                val intent = Intent(activity, WalkDataActivity::class.java)
                intent.putExtra(HOME_SPORT_DATA_TYPE, calorieType)
                activity!!.startActivity(intent)
            }
            R.id.sleep_layout -> {
                val intent = Intent(activity, SleepDataActivity::class.java)
                activity!!.startActivity(intent)
            }

            R.id.blood_ox_layout -> {
                val intent = Intent(activity, BloodOxDataActivity::class.java)
                activity!!.startActivity(intent)
            }

            R.id.heart_rate_layout -> {
                val intent = Intent(activity, HeatRtOrTempActivity::class.java)
                intent.putExtra(HOME_SPORT_DATA_TYPE, heartRateType)
                activity!!.startActivity(intent)
            }

            R.id.body_temperature_layout -> {
                val intent = Intent(activity, HeatRtOrTempActivity::class.java)
                intent.putExtra(HOME_SPORT_DATA_TYPE, temperatureType)
                activity!!.startActivity(intent)
            }

            R.id.week_sport -> {
                val intent = Intent(activity, AllSportDataActivity::class.java)
                activity!!.startActivity(intent)

            }
        }
    }


    override fun onRefresh(refreshLayout: RefreshLayout) {
        getHttpData()
        updateConnect()
        if( SettingInfoManager.instance.isAccessTokenNotEmpty()&& BleServiceManager.instance.isBleConnect()){
            BLESendCommandManager.instance.sendSyncTimeCMD()
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        homeViewModel.adListData()
        getDeviceListData()

    }

    fun getHttpData() {
        homeViewModel.adListData()
        getDeviceListData()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.onCleared()
        EventBus.getDefault().unregister(this);

        BleServiceManager.instance.unRegisterNotify()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == Constants.PERMISSIONS_ACCESS_COARSE_LOCATION && grantResults.size > 0 && grantResults.size > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            UToast.showShortToast("您没有打开定位授权，请在设置中打开")
        } else if (requestCode == Constants.PERMISSIONS_ACCESS_COARSE_LOCATION && grantResults.size > 0 && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            LocationAddressManager.instance.getLatLng()
            activity?.initLocationPermission()
        }

        if (requestCode == Constants.PERMISSIONS_ACCESS_FINE_LOCATION && grantResults.size > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            UToast.showShortToast("您没有打开定位授权，请在设置中打开")
        } else if (requestCode == Constants.PERMISSIONS_ACCESS_FINE_LOCATION && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LocationAddressManager.instance.getLatLng()
        }

    }


}