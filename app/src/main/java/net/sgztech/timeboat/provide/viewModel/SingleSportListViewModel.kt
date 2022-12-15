package net.sgztech.timeboat.provide.viewModel

import androidx.lifecycle.MutableLiveData
import com.device.ui.extension.VmLiveData
import com.device.ui.viewModel.BaseViewModel
import net.sgztech.timeboat.provide.dataModel.SingleSportItemModel
import net.sgztech.timeboat.ui.repository.SingleSportRepository

class SingleSportListViewModel:BaseViewModel() {
    private val singleSportRepository by lazy { SingleSportRepository() }

    val sportListData: VmLiveData<List<SingleSportItemModel>> = MutableLiveData()

    fun getSportData(startDate:String,endDate:String,type :Int){
        launchVmRequest({
            singleSportRepository.getSportListData(type,startDate,endDate)
        }, sportListData)

    }
}