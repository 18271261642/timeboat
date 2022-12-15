package net.sgztech.timeboat.provide.viewModel

import androidx.lifecycle.MutableLiveData
import com.device.ui.extension.VmLiveData
import com.device.ui.viewModel.BaseViewModel
import net.sgztech.timeboat.provide.dataModel.BleDeviceInfoModel
import net.sgztech.timeboat.provide.dataModel.TotalStepBean
import net.sgztech.timeboat.provide.dataModel.TotalStepModel
import net.sgztech.timeboat.ui.repository.DeleteDeviceRepository
import net.sgztech.timeboat.ui.repository.StepCountRepository

class StepCountViewModel :BaseViewModel(){

    private val totalStepRepository by lazy { StepCountRepository() }

    val stepData: VmLiveData<TotalStepBean> = MutableLiveData()

    fun getStepData(date:String,type :Int){
        launchVmRequest({
            totalStepRepository.getStepCount(date,type)
        }, stepData)

    }

}