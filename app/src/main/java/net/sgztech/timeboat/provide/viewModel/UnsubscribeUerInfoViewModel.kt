package net.sgztech.timeboat.provide.viewModel

import androidx.lifecycle.MutableLiveData
import com.device.ui.extension.VmLiveData
import com.device.ui.viewModel.BaseViewModel
import net.sgztech.timeboat.provide.dataModel.AuthorCodeModel
import net.sgztech.timeboat.provide.dataModel.UnSubscribeAgreeModel
import net.sgztech.timeboat.provide.dataModel.VersionHistoryModel
import net.sgztech.timeboat.ui.repository.UnSubscribeAgreementRepository
import net.sgztech.timeboat.ui.repository.UnsubscribeUerInfoRepository
import net.sgztech.timeboat.ui.repository.VersionHistoryRepository

class UnsubscribeUerInfoViewModel:BaseViewModel() {

    private val cancelRepository by lazy { UnsubscribeUerInfoRepository() }
    private val agreeRepository by lazy { UnSubscribeAgreementRepository() }
    val cancelData: VmLiveData<Any> = MutableLiveData()
    val agreementData :VmLiveData<UnSubscribeAgreeModel>  = MutableLiveData()

    fun  unSubscribe(){
        launchVmRequest({
            cancelRepository.unsubscribe()
        }, cancelData)
    }


    fun  unSubscribeAgree(){
        launchVmRequest({
            agreeRepository.agreeUrl()
        }, agreementData)
    }
}