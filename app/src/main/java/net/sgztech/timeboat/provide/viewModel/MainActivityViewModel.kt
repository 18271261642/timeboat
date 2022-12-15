package net.sgztech.timeboat.provide.viewModel

import androidx.lifecycle.MutableLiveData
import com.device.ui.extension.VmLiveData
import com.device.ui.viewModel.BaseViewModel
import net.sgztech.timeboat.provide.dataModel.DeviceConfigModel
import net.sgztech.timeboat.ui.repository.DeviceConfigRepository

class MainActivityViewModel :BaseViewModel(){
    private val configRepository by lazy { DeviceConfigRepository() }

    fun configData() {

        launchVmRequest({
            configRepository.configQuery()
        }, configData)

    }

    val configData :   VmLiveData<DeviceConfigModel> = MutableLiveData()
}