package net.sgztech.timeboat.provide.viewModel

import androidx.lifecycle.MutableLiveData
import com.device.ui.extension.VmLiveData
import com.device.ui.viewModel.BaseViewModel
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import net.sgztech.timeboat.managerUtlis.SettingInfoManager
import net.sgztech.timeboat.provide.dataModel.UserLoginModel
import net.sgztech.timeboat.ui.repository.UpdateUserInfoRepository

class ImproveInfoViewModel :BaseViewModel(){

    private val improveInfoRepository by lazy { UpdateUserInfoRepository() }

    val userInfoData: VmLiveData<UserLoginModel> = MutableLiveData()

    fun improveInfoDate(avatarUrl :String?  ,birthday:String? ,gender :Int?  ,height:String? , nickname:String? ,weight :String?){

        launchVmRequest({
            updateUserInfo(avatarUrl ,birthday ,gender  ,height , nickname ,weight)
        }, userInfoData)

    }

    suspend fun  updateUserInfo(avatarUrl :String?  ,birthday:String? ,gender :Int?  ,height:String? , nickname:String? ,weight :String?): HttpResponseModel<UserLoginModel> {
        val userData = improveInfoRepository.updateUserData(avatarUrl ,birthday ,gender  ,height , nickname ,weight)
        if(userData.isSuccess){
            SettingInfoManager.instance.refreshUserInfo(userData.data)
        }
        return userData
    }

}