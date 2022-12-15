package net.sgztech.timeboat.provide.viewModel

import androidx.lifecycle.MutableLiveData
import com.device.ui.extension.VmLiveData
import com.device.ui.viewModel.BaseViewModel
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import net.sgztech.timeboat.managerUtlis.SettingInfoManager
import net.sgztech.timeboat.provide.dataModel.BleDeviceInfoModel
import net.sgztech.timeboat.ui.repository.DeleteDeviceRepository

class DeleteDeviceViewModel : BaseViewModel() {
    private val delDeviceRepository by lazy { DeleteDeviceRepository() }

    val delDeviceData: VmLiveData<BleDeviceInfoModel> = MutableLiveData()

    fun deleteDevice(imei:String  ,mac:String ,name :String?){

        launchVmRequest({
            delDevice(imei ,mac,name)
        }, delDeviceData)

    }

    private suspend  fun delDevice(imei:String  ,mac:String ,name :String?) : HttpResponseModel<BleDeviceInfoModel> {
       val respond = delDeviceRepository.delDevice(imei ,mac,name)
        if(respond.isSuccess){
            SettingInfoManager.instance.cleanBleModel()
        }
        return respond
    }

}