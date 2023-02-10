package net.sgztech.timeboat.ui.activity

import android.widget.ImageView
import android.widget.TextView
import com.device.ui.baseUi.baseActivity.BaseActivity
import kotlinx.android.synthetic.main.activity_device_set_unit_layout.*
import net.sgztech.timeboat.R
import net.sgztech.timeboat.TimeBoatApplication
import net.sgztech.timeboat.ui.dialog.HeightSelectDialog

/**
 * 单位设置，公英制和温度单位
 * Created by Admin
 *Date 2023/1/11
 */
class DeviceUnitSetActivity : BaseActivity() {

    //title
    private var title_name : TextView ?= null
    //返回
    private var back_arrow : ImageView ?= null


    override fun getLayoutId(): Int {
       return R.layout.activity_device_set_unit_layout
    }

    override fun initBindView() {
        back_arrow = findViewById(R.id.back_arrow)
        title_name = findViewById(R.id.title_name)
        //公英制
        unitKmSettingBar.setOnClickListener {
            showDialogSelect(true)
        }

        //温度
        unitTempSettingBar.setOnClickListener {
            showDialogSelect(false)
        }

        back_arrow?.setOnClickListener {
            finish()
        }
    }

    override fun initData() {
        super.initData()
        title_name?.text = "单位设置"
    }


    private fun showDialogSelect(isKm : Boolean){
        val list = mutableListOf<String>()
        if(isKm){
            list.add("公制")
            list.add("英制")
        }else{
            list.add("℃")
            list.add("℉")
        }

        val dialog = HeightSelectDialog.Builder(this,list)
            .setTitleTx(if(isKm) "选择公英制" else "选择温度单位")
            .setUnitShow(false,"")
            .setSignalSelectListener {
                if(isKm){
                    unitKmSettingBar.rightText = it
                }else{
                    unitTempSettingBar.rightText = it
                }

//                TimeBoatApplication.timeBoatApplication.getBleOperate()?.setDeviceUnitAndTemp()
            }.show()
    }
}