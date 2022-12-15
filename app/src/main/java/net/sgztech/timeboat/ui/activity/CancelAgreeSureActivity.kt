package net.sgztech.timeboat.ui.activity

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.device.ui.viewBinding.viewBinding
import com.device.ui.viewModel.common.vmObserver
import com.imlaidian.utilslibrary.utils.LogUtil
import com.imlaidian.utilslibrary.utils.UToast
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.Constants.Companion.REPOSITORY_RESULT_TOKEN_INVALID
import net.sgztech.timeboat.databinding.ActivityCancleAgreeSureBinding
import net.sgztech.timeboat.managerUtlis.SettingInfoManager
import net.sgztech.timeboat.provide.viewModel.UnsubscribeUerInfoViewModel

class CancelAgreeSureActivity :BaseActivity() {
    private val TAG = CancelAgreeSureActivity::class.java.simpleName
    private val cancelBinding :ActivityCancleAgreeSureBinding by viewBinding()
    private val unSubscribeViewModel :UnsubscribeUerInfoViewModel by viewModels()
    private var checkAgree =false
    private var cancelAgreeUrl =""
    override fun getLayoutId(): Int {
       return  R.layout.activity_cancle_agree_sure
    }

    override fun initBindView() {
        cancelBinding.cancelTitleBar.titleName.text="注销确认"
        cancelBinding.cancelTitleBar.backArrow.setOnClickListener(this)
        cancelBinding.checkBox.setOnClickListener(this)
        cancelBinding.cancelBtn.setOnClickListener(this)
        cancelBinding.sureBtn.setOnClickListener(this)
        checkAgree = cancelBinding.checkBox.isChecked
        cancelBinding.sureBtn.isEnabled =false
        cancelBinding.cancelBtn.isEnabled =true
        cancelBinding.cancelAgreeInfo.setOnClickListener(this)
    }

    override fun initData() {
        super.initData()
        unSubscribeViewModel.unSubscribeAgree()
    }

    override fun startObserve() {
        super.startObserve()
        unSubscribeViewModel.cancelData.vmObserver(this){
            onAppSuccess = {
                cancelSuccess()
            }
            onAppError = { msg, errorCode ->
                if(errorCode==REPOSITORY_RESULT_TOKEN_INVALID){

                }
                UToast.showShortToast("error:" +msg)
            }
            onAppComplete ={
                LogUtil.d(TAG, "onAppComplete")
            }
            onAppLoading ={
                LogUtil.d(TAG, "onAppLoading")
            }
        }

        unSubscribeViewModel.agreementData.vmObserver(this){
            onAppSuccess = {
                if(it!=null){
                    cancelAgreeUrl =it.url
                }
            }
            onAppError = {msg ,errorCode->
                UToast.showShortToast(msg)
            }
            onAppComplete ={
                LogUtil.d(TAG, "onAppComplete")
            }
            onAppLoading ={
                LogUtil.d(TAG, "onAppLoading")
            }
        }
    }


    private  fun cancelSuccess(){
        UToast.showShortToast("注销账号成功")
//        exitOutAccount<MainActivity>()
        launchOnUI {
            cleanUserinfo()
            cleanBleInfo()
        }
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra(Constants.QUITE_CANCEL_ACCOUNT, Constants.QUITE_TO_HOME_PAGE)
        startActivity(intent)
    }
    private suspend fun cleanUserinfo() {
        SettingInfoManager.instance.cleanUserInfo()
    }

    private suspend fun  cleanBleInfo(){
        SettingInfoManager.instance.cleanBleModel()

    }

    override fun widgetClick(v: View) {
        super.widgetClick(v)
        when(v.id){
            R.id.back_arrow -> finish()

            R.id.sure_btn -> {
                if(checkAgree){
                    unSubscribeViewModel.unSubscribe()
                }else{
                    UToast.showShortToast("请勾选注销协议")
                }

            }
            R.id.cancel_btn ->{
                   finish()
            }
            R.id.cancel_agree_info ->{
                if(cancelAgreeUrl.isNotEmpty()){
                    showCancelAgree(cancelAgreeUrl)
                }else{
                    UToast.showShortToast("请稍重试")
                }

            }
            R.id.check_box ->{
                if(checkAgree){
                    cancelBinding.checkBox.isChecked =false
                    checkAgree =false

                }else{
                    cancelBinding.checkBox.isChecked =true
                    checkAgree = true
                }
                if(checkAgree){
                    cancelBinding.sureBtn.setBackgroundResource(R.drawable.btn_corner_orange_large)
                    cancelBinding.cancelBtn.setBackgroundResource(R.drawable.btn_warning_read)
                    cancelBinding.sureBtn.isEnabled =true
                }else{
                    cancelBinding.cancelBtn.setBackgroundResource(R.drawable.btn_corner_orange_large)
                    cancelBinding.sureBtn.setBackgroundResource(R.drawable.btn_warning_read)
                    cancelBinding.sureBtn.isEnabled =false
                }

            }
        }
    }

    fun showCancelAgree(url :String){
        val intent = Intent(this@CancelAgreeSureActivity ,WebViewActivity::class.java)
        intent.putExtra(Constants.WEB_VIEW_URL_TYPE, WebViewActivity.cancelAgree)
        intent.putExtra(Constants.WEB_VIEW_URL ,url)
        startActivity(intent)
    }
}