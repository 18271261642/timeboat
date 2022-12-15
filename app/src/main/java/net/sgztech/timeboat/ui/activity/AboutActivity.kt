package net.sgztech.timeboat.ui.activity

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.device.ui.viewBinding.viewBinding
import com.device.ui.viewModel.common.vmObserver
import com.imlaidian.utilslibrary.utils.SystemTool
import com.imlaidian.utilslibrary.utils.UToast
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.Constants.Companion.REPOSITORY_RESULT_TOKEN_INVALID
import net.sgztech.timeboat.databinding.ActivityAboutAppBinding
import net.sgztech.timeboat.managerUtlis.SettingInfoManager
import net.sgztech.timeboat.provide.viewModel.AboutAppViewModel
import net.sgztech.timeboat.ui.activity.WebViewActivity.Companion.appHistory
import net.sgztech.timeboat.ui.activity.WebViewActivity.Companion.privateAgree
import net.sgztech.timeboat.ui.activity.WebViewActivity.Companion.serverAgree

class AboutActivity:BaseActivity() {
    private val aboutBinging :ActivityAboutAppBinding by viewBinding()
    private val aboutViewModel :AboutAppViewModel by viewModels()
    private var appHistoryUrl =""

    override fun getLayoutId(): Int {
        return  R.layout.activity_about_app
    }

    override fun initBindView() {
       aboutBinging.aboutTitleBar.backArrow.setOnClickListener(this)
       aboutBinging.aboutTitleBar.titleName.text="关于Time Boat"
       aboutBinging.privacyAgreementLayout.setOnClickListener(this)
       aboutBinging.serviceAgreementLayout.setOnClickListener(this)
       aboutBinging.appHistoryLayout.setOnClickListener(this)
       aboutBinging.cancellationAgreementLayout.setOnClickListener(this)
    }

    override fun initData() {
        super.initData()
        aboutViewModel.versionHistory()
        aboutBinging.appVersion.text=SystemTool.getAppVersion(this) + "(" +SystemTool.getAppVersionCode(this) + ")"
    }

    override fun startObserve() {
        super.startObserve()
        aboutViewModel.versionHistoryData.vmObserver(this){

            onAppLoading = {

            }

            onAppSuccess ={
                if(it!=null&& it.url!=null){
                    appHistoryUrl = it.url
                }
            }

            onAppError ={msg, errorCode ->
                if(REPOSITORY_RESULT_TOKEN_INVALID==errorCode){
                    launchOnUI {
                        cleanInfo()
                    }
                    val intent = Intent(this@AboutActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    intent.putExtra(Constants.QUITE_CANCEL_ACCOUNT, Constants.QUITE_TO_HOME_PAGE)
                    startActivity(intent)
//                    exitOutAccount<MainActivity>()
                }
                UToast.showShortToast(msg +"，退出登录账号")
            }

            onAppComplete ={

            }
        }
    }

    private suspend fun cleanInfo() {
        SettingInfoManager.instance.cleanUserInfo()
        SettingInfoManager.instance.cleanBleModel()
    }


    override fun widgetClick(v: View) {
        super.widgetClick(v)
        when(v.id){
            R.id.back_arrow -> finish()
            R.id.privacy_agreement_layout -> {
                showAgreeType(privateAgree)
            }
            R.id.service_agreement_layout -> {
                showAgreeType(serverAgree)
            }
            R.id.app_history_layout ->{
                if(appHistoryUrl!=null && appHistoryUrl.isNotEmpty()){
                    showAppHistory(appHistoryUrl)
                }else{
                    UToast.showShortToast("请稍再试")
                }

            }
            R.id.cancellation_agreement_layout ->{
                val intent = Intent(this ,CancelAgreeActivity::class.java)
                startActivity(intent)
            }

        }
    }

    fun showAgreeType(type :Int){
        val intent = Intent(this@AboutActivity ,WebViewActivity::class.java)
        intent.putExtra(Constants.WEB_VIEW_URL_TYPE, type)

        startActivity(intent)
    }

    fun showAppHistory(url :String){
        val intent = Intent(this@AboutActivity ,WebViewActivity::class.java)
        intent.putExtra(Constants.WEB_VIEW_URL_TYPE, appHistory)
        intent.putExtra(Constants.WEB_VIEW_URL ,url)
        startActivity(intent)
    }
}