package net.sgztech.timeboat.provide.viewModel

import androidx.lifecycle.MutableLiveData
import com.device.ui.extension.VmLiveData
import com.device.ui.viewModel.BaseViewModel
import net.sgztech.timeboat.provide.dataModel.BodyTemperatureBean
import net.sgztech.timeboat.provide.dataModel.HeartRateBean
import net.sgztech.timeboat.ui.repository.BodyTemperatureRepository
import net.sgztech.timeboat.ui.repository.HeartRateRepository

class HeartRateViewModel :BaseViewModel() {

    private val heartRateRepository by lazy { HeartRateRepository() }
    val heartRateData: VmLiveData<HeartRateBean> = MutableLiveData()

    private val bodyTemperatureRepository by lazy { BodyTemperatureRepository() }
    val bodyTemperatureData: VmLiveData<BodyTemperatureBean> = MutableLiveData()

    fun getHeartRateData(date:String,type :Int){
        launchVmRequest({
            heartRateRepository.getHeartRate(date,type)
        }, heartRateData)
    }

    fun getBodyTemperatureData(date:String,type :Int){
        launchVmRequest({
            bodyTemperatureRepository.getBodyTemperature(date,type)
        }, bodyTemperatureData)
    }

}