package net.sgztech.timeboat.provide.viewModel

import androidx.lifecycle.MutableLiveData
import com.device.ui.extension.VmLiveData
import com.device.ui.viewModel.BaseViewModel
import net.sgztech.timeboat.provide.dataModel.AdInfoModel
import net.sgztech.timeboat.provide.dataModel.VersionHistoryModel
import net.sgztech.timeboat.ui.repository.HomePageRepository
import net.sgztech.timeboat.ui.repository.VersionHistoryRepository

class AboutAppViewModel :BaseViewModel(){

    private val versionHistoryRepository by lazy { VersionHistoryRepository() }

    val versionHistoryData: VmLiveData<VersionHistoryModel> = MutableLiveData()


    fun versionHistory(){

        launchVmRequest({
            versionHistoryRepository.versionHistory()
        }, versionHistoryData)
    }
}