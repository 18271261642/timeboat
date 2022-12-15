package net.sgztech.timeboat.provide.viewModel

import androidx.lifecycle.MutableLiveData
import com.device.rxble.RxBleClient
import com.device.ui.extension.VmLiveData
import com.device.ui.viewModel.BaseViewModel
import com.device.ui.viewModel.viewStatus.BaseUiStatus
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import com.imlaidian.utilslibrary.utils.LogUtil
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers.io
import net.sgztech.timeboat.TimeBoatApplication
import net.sgztech.timeboat.bleCommand.DeviceInfoDataModel
import net.sgztech.timeboat.managerUtlis.BleServiceManager
import net.sgztech.timeboat.managerUtlis.SettingInfoManager
import net.sgztech.timeboat.provide.dataModel.BleDeviceInfoModel
import net.sgztech.timeboat.provide.viewModel.ScanBleViewModel.Companion.bleStatusPermission
import net.sgztech.timeboat.provide.viewStatus.DeviceStatus
import net.sgztech.timeboat.ui.repository.AddDeviceRepository
import java.util.*


class DeviceViewModel : ScanBleViewModel() {
    //关联UI数据
    private var TAG = DeviceViewModel::class.java.simpleName
    var deviceLiveData: MutableLiveData<DeviceStatus> = MutableLiveData<DeviceStatus>();

    var devicePermissionData: MutableLiveData<BaseUiStatus> = MutableLiveData<BaseUiStatus>();
    val deviceUiStatus =  DeviceStatus()
    val permisionStatus =  BaseUiStatus()
    private var flowDisposable: Disposable? = null
    private val bleListenId =UUID.randomUUID().toString()
    var addDeviceData: VmLiveData<BleDeviceInfoModel> = MutableLiveData()
    private val addDeviceRepository by lazy { AddDeviceRepository() }
    // 业务逻辑
    fun initData() {
        BleServiceManager.instance.addListener(bleListenId,object  : BleServiceManager.UpdateUIListen{
            override fun refreshUi(what: Int, msg: Any?, describe: Any?) {
                LogUtil.d(TAG, "refreshUi 1")
                deviceUiStatus.what=what
                deviceUiStatus.obj =msg
                deviceUiStatus.describe =describe
                deviceLiveData.postValue(deviceUiStatus)
                LogUtil.d(TAG, "refreshUi 2")

            }
        })

        TimeBoatApplication.rxBleClient.observeStateChanges()
        .observeOn(io())
        .subscribe {
            checkStatus(it)
        } .let { flowDisposable = it }

    }

    private fun checkStatus(state : RxBleClient.State){
        permisionStatus.what=bleStatusPermission
        permisionStatus.obj =state
        devicePermissionData.postValue(permisionStatus)

    }

    fun addDevice(imei :String,mac :String  ,name :String?){
            launchVmRequest({
                addDeviceRepository(imei ,mac ,name)
            }, addDeviceData)

    }

    private suspend fun addDeviceRepository(imei :String,mac :String  ,name :String?): HttpResponseModel<BleDeviceInfoModel> {
       val respond = addDeviceRepository.addDevice(imei ,mac ,name )
        if(respond.isSuccess){
            if(respond.data!=null){
                SettingInfoManager.instance.refreshBleModel(respond.data)
            }else{
                LogUtil.d(TAG, "addDeviceRepository data null")
            }
        }
        return respond
    }

    override fun onCleared() {
        super.onCleared()
        if(flowDisposable!=null){
            flowDisposable!!.dispose()
        }
        BleServiceManager.instance.removeListener(bleListenId);
    }

}