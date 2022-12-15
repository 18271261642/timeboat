package net.sgztech.timeboat.provide.viewModel

import androidx.lifecycle.MutableLiveData
import com.device.ui.extension.VmLiveData
import com.device.ui.viewModel.BaseViewModel
import net.sgztech.timeboat.provide.dataModel.BloodOxygenBean
import net.sgztech.timeboat.provide.dataModel.BloodOxygenModel

import net.sgztech.timeboat.ui.repository.BloodOxygenRepository

class BloodOxViewModel : BaseViewModel() {

    private val bloodOxRepository by lazy { BloodOxygenRepository() }

    val bloodOxData: VmLiveData<BloodOxygenBean> = MutableLiveData()

    fun getBloodOxData(date:String ,type :Int){

        launchVmRequest({
            bloodOxRepository.getBloodOx(date,type)
        }, bloodOxData)

    }


}