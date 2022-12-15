package net.sgztech.timeboat.provide.viewModel

import androidx.lifecycle.MutableLiveData
import com.device.ui.extension.VmLiveData
import com.device.ui.viewModel.BaseViewModel
import com.device.ui.viewModel.viewStatus.VmState
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import net.sgztech.timeboat.ui.repository.AuthorCodeRepository

import net.sgztech.timeboat.provide.dataModel.AuthorCodeModel
import net.sgztech.timeboat.ui.activity.MainActivity.Companion.bindingPhoneAction
import net.sgztech.timeboat.ui.activity.MainActivity.Companion.loginAction


class AuthorCodeViewModel : BaseViewModel() {
    //关联UI数据

    val authorCodeData: VmLiveData<AuthorCodeModel> = MutableLiveData()
    private val authorCodeRepository by lazy { AuthorCodeRepository() }

    fun authorCode(phone :String,actionType :Int){
        launchVmRequest({
            getAuthorCodeInfo(phone,actionType)
        }, authorCodeData)
    }


    suspend fun getAuthorCodeInfo(phone :String,actionType: Int):HttpResponseModel<AuthorCodeModel>{
        if(actionType ==loginAction){
            return authorCodeRepository.getData(phone)
        }else if(actionType ==bindingPhoneAction){
            return authorCodeRepository.bindingSmsData(phone)
        }  else{
            return authorCodeRepository.getData(phone)
        }

    }

}