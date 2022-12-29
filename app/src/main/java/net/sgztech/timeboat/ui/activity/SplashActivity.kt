package net.sgztech.timeboat.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.device.ui.viewBinding.viewBinding
import com.device.ui.viewModel.common.vmObserver
import com.imlaidian.utilslibrary.utils.LogUtil
import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil
import com.imlaidian.utilslibrary.utils.UToast
import com.imlaidian.utilslibrary.utils.jsonUtil
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.databinding.WelcomeActivityBinding
import net.sgztech.timeboat.managerUtlis.SettingInfoManager
import net.sgztech.timeboat.provide.dataModel.AdInfoModel
import net.sgztech.timeboat.provide.viewModel.LauncherViewModel
import net.sgztech.timeboat.ui.dialog.OnPrivacyListener
import net.sgztech.timeboat.ui.dialog.PrivacyDialogView


class SplashActivity:BaseActivity() {
    private val TAG = SplashActivity::class.java.simpleName

    private var adList:List<AdInfoModel>? =null
    private val launcherBinding: WelcomeActivityBinding by viewBinding()
    private val  launcherViewModel :LauncherViewModel by viewModels()
    override fun getLayoutId(): Int {
        return R.layout.welcome_activity
    }

    override fun initBindView() {
        if (!SettingInfoManager.instance.isFirstIntoApp()) {
            launcherViewModel.adListData()
        }
    }

    override fun initDataFromThread() {
        super.initDataFromThread()
        try {
            val adListJson = SharedPreferencesUtil.getInstance().getString(Constants.LAUNCHER_AD_LIST)
            adList = jsonUtil.getObjects<AdInfoModel>(adListJson,AdInfoModel::class.java)
        }catch (e :Exception){
            e.printStackTrace()
        }

    }

    override fun threadDataInit() {
        super.threadDataInit()
        if (!SettingInfoManager.instance.isFirstIntoApp()) {
            if(adList!=null && adList!!.isNotEmpty()){
                Glide
                    .with(this@SplashActivity)
                    .load(adList!![0].adUrl)
                    .fitCenter()
                    .placeholder(R.mipmap.welecom_bg)
                    .into(launcherBinding.launcherIv);
            }
        }

    }

    override fun initData(){
        jumpToMain()
    }

    private fun jumpToMain(){

       if (SettingInfoManager.instance.isFirstIntoApp()) {
            showAgree()
           return
        }
        toMain()
    }



    private fun toMain(){
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable {
            val intent = Intent(this@SplashActivity ,MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }

    private fun showAgree(){
        val dialog = PrivacyDialogView(this,R.style.edit_AlertDialog_style)
        dialog.show()
        dialog.setCancelable(false)
        dialog.setOnPrivacyListener { isAgree ->
            dialog.dismiss()
            if (isAgree) {
                SharedPreferencesUtil.getInstance().setBoolean(Constants.FIRST_INTO_APP, false)
                val intent = Intent(this@SplashActivity ,MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                UToast.showLongToast("请注意:拒绝协议将导致功能不能使用!!")
                finish()
            }
        }

        val window = dialog.window
        val windowLayout = window?.attributes
        val metrics2: DisplayMetrics = resources.displayMetrics
        val widthW: Int = metrics2.widthPixels
        windowLayout?.width = widthW
        window?.attributes = windowLayout

    }





    override fun startObserve() {
        super.startObserve()
        launcherViewModel.launcherAdListData.vmObserver(this){

                onAppLoading = {
                    Log.d(TAG ,"开始")
                }

                onAppSuccess ={
                    if(it!=null&& it.size!! >0){
                        if(it[0].adType== 1){
                            Glide
                                .with(this@SplashActivity)
                                .load(it[0].adUrl)
                                .fitCenter()
                                .placeholder(R.mipmap.welecom_bg)
                                .into(launcherBinding.launcherIv);
                        }
                    }
                }

                onAppError ={msg,errorCode->
                    LogUtil.d(TAG ,"it error =" +msg)
                }

                onAppComplete = {
                    LogUtil.d(TAG ,"结束")
                }
        }

    }
}