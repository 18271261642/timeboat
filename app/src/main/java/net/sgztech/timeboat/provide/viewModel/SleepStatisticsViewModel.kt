package net.sgztech.timeboat.provide.viewModel

import androidx.lifecycle.MutableLiveData
import com.device.ui.extension.VmLiveData
import com.device.ui.viewModel.BaseViewModel
import net.sgztech.timeboat.provide.dataModel.SleepDailyListBean
import net.sgztech.timeboat.provide.dataModel.SleepStatisticsListBean
import net.sgztech.timeboat.ui.repository.SleepDailyRepository
import net.sgztech.timeboat.ui.repository.SleepStatisticsRepository

class SleepStatisticsViewModel : BaseViewModel(){

    private val sleepStatisticsRepository by lazy { SleepStatisticsRepository() }

    val sleepStatisticsData: VmLiveData<SleepStatisticsListBean> = MutableLiveData()


    fun getSleepStatisticsData(date:String ,type :Int){
        launchVmRequest({
            sleepStatisticsRepository.getSleepStatisticsData(date,type)
        }, sleepStatisticsData)

    }

}