package net.sgztech.timeboat.provide.viewModel


import androidx.lifecycle.MutableLiveData
import com.device.ui.extension.VmLiveData
import com.device.ui.viewModel.BaseViewModel
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel
import com.imlaidian.utilslibrary.utils.LogUtil
import net.sgztech.timeboat.managerUtlis.SettingInfoManager
import net.sgztech.timeboat.managerUtlis.ThirdWxLoginManager
import net.sgztech.timeboat.provide.dataModel.UserLoginModel
import net.sgztech.timeboat.provide.dataModel.WxUserInfoModel
import net.sgztech.timeboat.ui.repository.UserLoginRepository
import net.sgztech.timeboat.ui.repository.WxLoginRepository

class LoginViewModel :BaseViewModel(){
    private val TAG =LoginViewModel::class.java.simpleName
    private val wxLoginRepository by lazy { WxLoginRepository() }
    private val userLoginRepository by lazy { UserLoginRepository() }
    val loginWxData: VmLiveData<WxUserInfoModel> = MutableLiveData()
    val userLoginData: VmLiveData<UserLoginModel> = MutableLiveData()

    fun login(loginType :Int  ,phone:String? ,code :String?  ,avatarUrl:String? , openId:String?, nickname:String?){

        launchVmRequest({
            userLogin(loginType ,phone,code,avatarUrl,openId,nickname)
        }, userLoginData)
    }


   suspend fun  userLogin(loginType :Int  ,phone:String? ,code :String?  ,avatarUrl:String? , openId:String?,nickname: String?): HttpResponseModel<UserLoginModel> {
        val userData = userLoginRepository.getData(loginType ,phone,code,avatarUrl,openId,nickname)
        if(userData.isSuccess){
            SettingInfoManager.instance.refreshUserInfo(userData.data)
        }
       return userData

    }

    fun registerWx(){
        ThirdWxLoginManager.instance.registerWxListen(object : ThirdWxLoginManager.WechatCodeListen{
            override fun getCode(code: String?) {
                LogUtil.d(TAG ,"code =" + code)
                if(code!=null){
                    getUseInfo(code)
                }else{
                    LogUtil.d(TAG ,"code null")
                }
            }
        })
        ThirdWxLoginManager.instance.registerToWeiXin()
        ThirdWxLoginManager.instance.login()
    }



    fun getUseInfo(code :String){
        launchVmRequest({
            wxLoginRepository.getData(code)
        }, loginWxData)
    }

    public override fun onCleared() {
        super.onCleared()
        ThirdWxLoginManager.instance.unRegisterWxListen()
    }

}