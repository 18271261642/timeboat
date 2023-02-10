package net.sgztech.timeboat.ui.newui

import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.device.ui.baseUi.baseActivity.BaseActivity
import net.sgztech.timeboat.R

/**
 * Created by Admin
 *Date 2023/1/13
 */
class BleCallActivity : BaseActivity() {

    private var showBleCallImgView : ImageView ?= null

    //title
    private var title_name : TextView?= null
    //返回
    private var back_arrow : ImageView?= null

    override fun getLayoutId(): Int {
       return R.layout.activity_ble_call_layout
    }

    override fun initBindView() {
        showBleCallImgView = findViewById(R.id.showBleCallImgView)
        back_arrow = findViewById(R.id.back_arrow)
        title_name = findViewById(R.id.title_name)


    }

    override fun initData() {
        super.initData()
        back_arrow?.setOnClickListener { finish() }
        title_name?.text = "蓝牙通话"

        showBleCallImgView?.let { Glide.with(this).load(R.drawable.ble_call).into(it) }

    }
}