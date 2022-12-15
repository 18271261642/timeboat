package net.sgztech.timeboat.provide.viewModel

import androidx.lifecycle.MutableLiveData
import com.device.ui.extension.VmLiveData
import com.device.ui.viewModel.BaseViewModel
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import net.sgztech.timeboat.managerUtlis.SettingInfoManager
import net.sgztech.timeboat.provide.dataModel.AdInfoModel
import net.sgztech.timeboat.provide.dataModel.BleDeviceInfoModel
import net.sgztech.timeboat.provide.dataModel.DevicesListRepository
import net.sgztech.timeboat.provide.dataModel.HomeSportDataModel
import net.sgztech.timeboat.ui.repository.HomePageRepository
import net.sgztech.timeboat.ui.repository.HomeSportRepository

class HomeViewModel : BaseViewModel() {

    private val homePageRepository by lazy { HomePageRepository() }
    private val sportRepository by lazy { HomeSportRepository() }
    private val deviceListRepository by lazy { DevicesListRepository() }
    val homeAdListData: VmLiveData<List<AdInfoModel>> = MutableLiveData()
    val deviceData: VmLiveData<List<BleDeviceInfoModel>> = MutableLiveData()
    val homeSportData: VmLiveData<HomeSportDataModel> = MutableLiveData()


    fun adListData(){

        launchVmRequest({
            homePageRepository.getAdList()
        }, homeAdListData)
    }

    fun sportData(){

        launchVmRequest({
            sportRepository.getSportData()
        }, homeSportData)
    }

    fun deviceListData(){

        launchVmRequest({
            deviceInfo()
        }, deviceData)

    }

    private suspend fun deviceInfo() : HttpResponseModel<List<BleDeviceInfoModel>>{
        var deviceInfo =deviceListRepository.getDeviceList()
        if(deviceInfo.isSuccess){
            var devices= deviceInfo.data
            if(devices!=null && devices.isNotEmpty()){
                SettingInfoManager.instance.refreshBleModel(devices[0])
            }
        }
        return deviceInfo
    }

}