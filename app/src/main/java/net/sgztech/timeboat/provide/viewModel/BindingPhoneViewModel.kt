package net.sgztech.timeboat.provide.viewModel

import androidx.lifecycle.MutableLiveData
import com.device.ui.extension.VmLiveData
import com.device.ui.viewModel.BaseViewModel
import com.device.ui.viewModel.viewStatus.VmState
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil
import com.imlaidian.utilslibrary.utils.jsonUtil
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.managerUtlis.SettingInfoManager
import net.sgztech.timeboat.provide.dataModel.UserLoginModel
import net.sgztech.timeboat.ui.activity.MainActivity.Companion.bindingPhoneAction
import net.sgztech.timeboat.ui.repository.BindingPhoneRepository

class BindingPhoneViewModel:BaseViewModel() {

    private val bindingPhoneRepository by lazy { BindingPhoneRepository() }

    val userLoginData: VmLiveData<UserLoginModel> = MutableLiveData()

    fun bindingPhone(phone:String ,code :String){

        launchVmRequest({
            bindingPhoneData(phone ,code)
        }, userLoginData)

    }

    suspend fun  bindingPhoneData(phone:String ,code :String ): HttpResponseModel<UserLoginModel> {
            val userData = bindingPhoneRepository.bindingPhoneData(phone,code)
            if(userData.isSuccess){
                SettingInfoManager.instance.refreshUserInfo(userData.data)
            }
            return userData
    }

}