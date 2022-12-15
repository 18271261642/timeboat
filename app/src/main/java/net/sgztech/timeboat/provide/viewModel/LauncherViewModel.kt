package net.sgztech.timeboat.provide.viewModel

import androidx.lifecycle.MutableLiveData
import com.device.ui.extension.VmLiveData
import com.device.ui.viewModel.BaseViewModel
import com.device.ui.viewModel.viewStatus.VmState
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import net.sgztech.timeboat.provide.dataModel.AdInfoModel
import net.sgztech.timeboat.provide.dataModel.DeviceConfigModel
import net.sgztech.timeboat.ui.repository.DeviceConfigRepository
import net.sgztech.timeboat.ui.repository.LauncherAdRepository

class LauncherViewModel :BaseViewModel(){
    private val launcherRepository by lazy { LauncherAdRepository() }
    val launcherAdListData: VmLiveData<List<AdInfoModel>> = MutableLiveData()
    fun adListData() {

        launchVmRequest({
            launcherRepository.getLauncherAdList()
        }, launcherAdListData)

    }


}