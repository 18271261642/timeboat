package net.sgztech.timeboat.ui.activity

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.device.ui.viewBinding.viewBinding
import com.device.ui.viewModel.common.vmObserver
import com.imlaidian.utilslibrary.config.IntentConstant
import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil
import com.imlaidian.utilslibrary.utils.UToast
import net.sgztech.timeboat.R
import net.sgztech.timeboat.TimeBoatApplication
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.Constants.Companion.DEL_DEVICES_STATUS
import net.sgztech.timeboat.config.Constants.Companion.REPOSITORY_RESULT_TOKEN_INVALID
import net.sgztech.timeboat.databinding.ActivityDelDeviceBinding
import net.sgztech.timeboat.managerUtlis.SettingInfoManager
import net.sgztech.timeboat.provide.dataModel.BleDeviceInfoModel
import net.sgztech.timeboat.provide.viewModel.DeleteDeviceViewModel
import net.sgztech.timeboat.ui.utils.MmkvUtils

class DelDeviceActivity :BaseActivity() {
    companion object {
        val DEL_SUCCESS = 1
        val DEL_FAILED = 0
    }
    private val  delBinding :ActivityDelDeviceBinding by viewBinding()
    private val  delViewModel :DeleteDeviceViewModel by viewModels()
    private var title_name : TextView ?= null

    private var device_imei : TextView?= null
    private var name : TextView?= null

    override fun getLayoutId(): Int {
        return  R.layout.activity_del_device
    }

    override fun initBindView() {
        title_name = findViewById(R.id.title_name)
        delBinding.delDevice.setOnClickListener(this)
        device_imei = findViewById(R.id.device_imei)
        name = findViewById(R.id.name)
    }

    override fun initData() {
        super.initData()
//        delBinding.delDeviceTitleBar.titleName.text ="删除设备"
//        delBinding.delDeviceTitleBar.backArrow.setOnClickListener(this)
//        val bleInfo = SettingInfoManager.instance.getBleModel()
//
//        if(bleInfo!=null){
//            delBinding.name.text =bleInfo.name
//            delBinding.deviceImei.text =bleInfo.imei
//        }

        title_name?.text = "删除设备"
        name?.text = MmkvUtils.getConnDeviceName()
        device_imei?.text = MmkvUtils.getConnDeviceMac()
    }

    override fun startObserve() {
        super.startObserve()

        delViewModel.delDeviceData.vmObserver(this){
            onAppLoading ={

            }
            onAppSuccess ={
                UToast.showShortToast("删除成功")
                delStatus(DEL_SUCCESS)

            }
            onAppError = {msg ,errorCode ->
                if(errorCode ==REPOSITORY_RESULT_TOKEN_INVALID){
                    launchOnUI {
                        cleanInfo()
                    }
                    val intent = Intent(this@DelDeviceActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    intent.putExtra(Constants.QUITE_CANCEL_ACCOUNT, Constants.QUITE_TO_HOME_PAGE)
                    startActivity(intent)
//                    exitOutAccount<MainActivity>()
                }else{
                    UToast.showShortToast("删除失败")
                    delStatus(DEL_FAILED)
                }
                UToast.showShortToast(msg)
            }
            onAppComplete ={

            }
        }
    }

    private suspend fun cleanInfo() {
        SettingInfoManager.instance.cleanUserInfo()
        SettingInfoManager.instance.cleanBleModel()
    }

    private fun delStatus(status :Int){
        val intent = Intent()
        intent.putExtra(DEL_DEVICES_STATUS, status)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun widgetClick(v: View) {
        super.widgetClick(v)
        if(v.id==R.id.del_device){
//            val bleInfo = SettingInfoManager.instance.bleInfo
//            if(bleInfo!=null){
//                delViewModel.deleteDevice(bleInfo.imei ,bleInfo.mac ,bleInfo.name)
//            }

            TimeBoatApplication.timeBoatApplication.getBleOperate()?.disConnYakDevice()
            MmkvUtils.saveConnDeviceName(null)
            MmkvUtils.saveConnDeviceMac(null)
            finish()

        }else if(v.id==R.id.back_arrow){
            finish()
        }
    }
}