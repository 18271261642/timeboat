package net.sgztech.timeboat.ui.activity

import android.widget.ImageView
import android.widget.TextView
import com.blala.blalable.listener.WriteBackDataListener
import com.device.ui.baseUi.baseActivity.BaseActivity
import net.sgztech.timeboat.R
import net.sgztech.timeboat.TimeBoatApplication
import net.sgztech.timeboat.ui.utils.MmkvUtils

/**
 * 关于设备
 */
class AboutDeviceActivity : BaseActivity(){

    //title
    private var title_name : TextView?= null
    //返回
    private var back_arrow : ImageView?= null

    private var device_imei : TextView ?= null
    private var name : TextView ?= null



    override fun getLayoutId(): Int {
       return R.layout.activity_about_device_layout
    }

    override fun initBindView() {

        device_imei = findViewById(R.id.device_imei)
        name = findViewById(R.id.name)
        back_arrow = findViewById(R.id.back_arrow)
        title_name = findViewById(R.id.title_name)

        back_arrow?.setOnClickListener {
            finish()
        }
    }

    override fun initData() {
        super.initData()
        title_name?.text = "关于设备"

        name?.text = MmkvUtils.getConnDeviceName()
        device_imei?.text = MmkvUtils.getConnDeviceMac()

        TimeBoatApplication.timeBoatApplication.getBleOperate()?.getDeviceVersionData(object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {

            }

        })
    }
}