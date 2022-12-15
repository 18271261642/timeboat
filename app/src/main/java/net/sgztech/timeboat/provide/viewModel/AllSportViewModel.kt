package net.sgztech.timeboat.provide.viewModel

import androidx.lifecycle.MutableLiveData
import com.device.ui.extension.VmLiveData
import com.device.ui.viewModel.BaseViewModel
import net.sgztech.timeboat.provide.dataModel.AllSportBean
import net.sgztech.timeboat.provide.dataModel.BloodOxygenBean
import net.sgztech.timeboat.ui.repository.AllSportRepository
import net.sgztech.timeboat.ui.repository.BloodOxygenRepository

class AllSportViewModel :BaseViewModel(){

    private val allSportRepository by lazy { AllSportRepository() }

    val allSportData: VmLiveData<AllSportBean> = MutableLiveData()

    fun getAllSportData(date:String ,type :Int){

        launchVmRequest({
            allSportRepository.getAllSportData(date,type)
        }, allSportData)

    }
}