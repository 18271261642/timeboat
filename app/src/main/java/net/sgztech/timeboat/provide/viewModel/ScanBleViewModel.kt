package net.sgztech.timeboat.provide.viewModel

import android.os.ParcelUuid
import androidx.lifecycle.MutableLiveData
import com.device.rxble.scan.ScanFilter
import com.device.rxble.scan.ScanResult
import com.device.rxble.scan.ScanSettings
import com.device.ui.viewModel.BaseViewModel
import com.imlaidian.utilslibrary.utils.LogUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import net.sgztech.timeboat.TimeBoatApplication
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.managerUtlis.SettingInfoManager
import net.sgztech.timeboat.provide.viewStatus.ScanBleStatus
import java.util.concurrent.TimeUnit

open class ScanBleViewModel :BaseViewModel() {
    private val TAG  = ScanBleViewModel::class.java.simpleName
    var macAddress =""
    //关联UI数据
    var liveData: MutableLiveData<ScanBleStatus> = MutableLiveData<ScanBleStatus>();
    var uiStatus =  ScanBleStatus()
    val scanTimeOut = 60L
    //更新ui相关操作
    companion object {
        //蓝牙搜索
        val scanBlePermission = 1000
        //开始扫描
        val startScanBleDevice = 1001
        //扫描成功
        val scanBleDevicesSuccess = 1002
        //扫描出错
        val scanBleDevicesError = 1003
        val disposeScanBleDevices =1004
        var bleStatusPermission =1005

        // 蓝牙连接

        var connectDiscovery = 1007

        val readError = 1009
        val writeError = 1010
        val notificationError=1011

        // 初始化
        val isInit = 2000
        // 正在连接
        val isConnecting = 2001
        // 已连接
        val isConnectSuccess = 2002
        //连接失败
        val connectFailed = 2003
        //断开连接，
        val autoDisconnect = 2004

        //自动重新连接，
        val reconnect = 2005
        val blePermission = 2006
        // 手动断开的链接，不自动重连
        val manualDisconnect = 2004

    }

    private val rxBleClient = TimeBoatApplication.rxBleClient
    private var scanDisposable: Disposable? = null
    private val isScanning: Boolean
        get() = scanDisposable != null


    fun scanBleDevices(): Observable<ScanResult> {
         val scanSettings = ScanSettings.Builder()
             .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
             .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
             .build()

        val gattServiceUUid = ParcelUuid.fromString(Constants.GATT_Service_UUID)

            if(macAddress!=null && macAddress.isNotEmpty()){
                val scanFilter =ScanFilter.Builder().setDeviceAddress(macAddress).build()
                return rxBleClient.scanBleDevices(scanSettings, scanFilter).take(scanTimeOut,TimeUnit.SECONDS)
            }else{
                try {
                    val bleNameList = SettingInfoManager.instance.bleNameList
                    LogUtil.d(TAG , "scanBleDevices bleNameList =$bleNameList")
                    val nameList: Array<String> = bleNameList.split(",".toRegex()).toTypedArray()
                    if (nameList != null && nameList.isNotEmpty()) {
                        val length =nameList.size
                        val scanFilterList= arrayOfNulls<ScanFilter>(length)
                        for(i in nameList.indices ){
                            val scanFilter = ScanFilter.Builder().setDeviceName(nameList[i]).build()
                            scanFilterList[i]=scanFilter
                        }
                        return rxBleClient.scanBleDevices(scanSettings, *scanFilterList).take(scanTimeOut,TimeUnit.SECONDS)
                    }else{
                        val scanFilter =ScanFilter.Builder().setDeviceName(Constants.BLE_NAME_LIST).build()
                        return rxBleClient.scanBleDevices(scanSettings, scanFilter).take(scanTimeOut,TimeUnit.SECONDS)
                    }

                }catch (e:Exception){
                    val scanFilter =ScanFilter.Builder().setDeviceName(Constants.BLE_NAME_LIST).build()
                    return rxBleClient.scanBleDevices(scanSettings, scanFilter).take(scanTimeOut,TimeUnit.SECONDS)
                }
            }
     }

    fun onBackGroundToggleClick(mac :String){
        macAddress =mac
        onScanToggleClick()
    }

     fun onScanToggleClick() {
        if (isScanning) {
            scanDisposable?.dispose()
        } else {
            if (rxBleClient.isScanRuntimePermissionGranted) {
                scanBleDevices()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        startScanBle()
                    }
                    .doFinally {
                        LogUtil.d(TAG , "scan finish")
                        dispose()
                    }
                    .subscribe({
                        uiStatus.what = scanBleDevicesSuccess
                        uiStatus.obj=it
                        updateUIStatus(uiStatus) }, {
                        LogUtil.d(TAG , "scan error")
                        uiStatus.what = scanBleDevicesError
                        uiStatus.obj=it
                        updateUIStatus(uiStatus)
                      })
                    .let { scanDisposable = it }
            } else {
                uiStatus.what = scanBlePermission
                uiStatus.obj = rxBleClient
                updateUIStatus(uiStatus)
            }
        }
    }

    private fun updateUIStatus(uiStatus: ScanBleStatus ){
        liveData.postValue(uiStatus)
    }

    private fun  startScanBle(){
        uiStatus.what = startScanBleDevice
        updateUIStatus(uiStatus)
    }


    private fun dispose() {
        scanDisposable = null
        uiStatus.what = disposeScanBleDevices
        updateUIStatus(uiStatus)
    }

    fun onPause() {
        // Stop scanning in onPause callback.
        if (isScanning) scanDisposable?.dispose()
    }

    override fun onCleared() {
        super.onCleared()
        dispose()
    }
}