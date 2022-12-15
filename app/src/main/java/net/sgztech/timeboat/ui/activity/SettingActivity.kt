package net.sgztech.timeboat.ui.activity

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.device.ui.viewBinding.viewBinding
import com.imlaidian.utilslibrary.UtilsApplication
import com.imlaidian.utilslibrary.utils.UToast
import net.sgztech.timeboat.R
import net.sgztech.timeboat.databinding.ActivitySettingBinding
import net.sgztech.timeboat.util.GlideCacheUtils


class SettingActivity:BaseActivity(){

    private val settingBinding :ActivitySettingBinding by viewBinding()

    override fun getLayoutId(): Int {
       return  R.layout.activity_setting
    }

    override fun initBindView() {

        settingBinding.settingTitleBar.titleName.text="设置"
        settingBinding.settingTitleBar.backArrow.setOnClickListener(this)
        settingBinding.cleanCacheLayout.setOnClickListener(this)
        settingBinding.permissionLayout.setOnClickListener(this)
    }

    override fun widgetClick(v: View) {
        super.widgetClick(v)
        when(v.id){

            R.id.back_arrow ->{
                finish()
            }
            R.id.clean_cache_layout ->{
                var content = UtilsApplication.getInstance().applicationContext
                var size :String?=null
                launchOnUI {


                    size = GlideCacheUtils.instance?.getCacheSize(content)
                   GlideCacheUtils.instance?.clearImageAllCache(content)

                }

                UToast.showShortToast("已清理了" +size )
            }
            R.id.permission_layout->{
                val intent: Intent =Intent()
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }
    }
}