package net.sgztech.timeboat.provide.viewModel

import androidx.lifecycle.MutableLiveData
import com.device.ui.extension.VmLiveData
import com.device.ui.viewModel.BaseViewModel
import net.sgztech.timeboat.provide.dataModel.SleepDailyListBean
import net.sgztech.timeboat.provide.dataModel.SleepDailyModel
import net.sgztech.timeboat.ui.repository.SleepDailyRepository
import net.sgztech.timeboat.ui.repository.SleepStatisticsRepository

class SleepDailyViewModel :BaseViewModel(){
    private val sleepDailyRepository by lazy { SleepDailyRepository() }
    val sleepDailyData: VmLiveData<SleepDailyListBean> = MutableLiveData()

    fun getSleepDailyData(date:String ){
        launchVmRequest({
            sleepDailyRepository.getSleepDailyData(date)
        }, sleepDailyData)

    }


}