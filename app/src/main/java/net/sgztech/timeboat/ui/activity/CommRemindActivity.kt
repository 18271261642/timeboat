package net.sgztech.timeboat.ui.activity

import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.bonlala.widget.view.SwitchButton
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.sgztech.timeboat.R
import net.sgztech.timeboat.bean.CommRemindBean
import net.sgztech.timeboat.bean.DbManager
import net.sgztech.timeboat.ui.dialog.TimeSelectorDialog
import net.sgztech.timeboat.ui.utils.MmkvUtils
import net.sgztech.timeboat.util.BikeUtils
import kotlin.math.min
import kotlin.reflect.jvm.internal.impl.descriptors.impl.DeclarationDescriptorVisitorEmptyBodies

/**
 * Created by Admin
 *Date 2023/2/22
 */
class CommRemindActivity : BaseActivity() {

    //title
    private var title_name : TextView?= null
    //返回
    private var back_arrow : ImageView?= null
    private var save_user_info : TextView ?= null
    //开始时间
    private var comRemindStartTimeTv : TextView ?= null
    //结束时间
    private var comRemindEndTimeTv : TextView ?= null
    //开关
    private var commRemindSwitch : SwitchCompat?= null

    private var commRemindStartLayout : ConstraintLayout ?= null
    private var commRemindEndLayout : ConstraintLayout ?= null


    private var commRemindBean : CommRemindBean ?= null

    override fun getLayoutId(): Int {
        return R.layout.activity_comm_remind_layout
    }

    override fun initBindView() {
        back_arrow = findViewById(R.id.back_arrow)
        title_name = findViewById(R.id.title_name)
        save_user_info = findViewById(R.id.save_user_info)
        commRemindStartLayout = findViewById(R.id.commRemindStartLayout)
        commRemindEndLayout = findViewById(R.id.commRemindEndLayout)
        comRemindStartTimeTv = findViewById(R.id.comRemindStartTimeTv)
        comRemindEndTimeTv = findViewById(R.id.comRemindEndTimeTv)
        commRemindSwitch = findViewById(R.id.commRemindSwitch)
    }


    override fun initData() {
        super.initData()

        save_user_info?.visibility = View.VISIBLE
        save_user_info?.text = "保存"
        back_arrow?.setOnClickListener {
            finish()
        }

        commRemindSwitch?.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if(commRemindBean == null){
                    commRemindBean = CommRemindBean()
                }
                commRemindBean?.switchStatus = if(p1) 1 else 0
            }

        })


        //保存
        save_user_info?.setOnClickListener {
            saveCommData()
            finish()
        }

        //开始时间
        commRemindStartLayout?.setOnClickListener {
            showDialogSelector(0)
        }

        //结束时间
        commRemindEndLayout?.setOnClickListener {
            showDialogSelector(1)
        }


        val code = intent.getIntExtra("code",-1)
        title_name?.text = showTitle(code)
        showTypeData(code)
    }


    //保存数据
    private fun saveCommData(){
        if(commRemindBean == null){
            commRemindBean = CommRemindBean()
        }

        commRemindBean?.type?.let { DbManager.getInstance().saveCommRemindForType(it,commRemindBean) }
    }


    //显示时间选择
    private fun showDialogSelector(code : Int){

        val hourValue = if(code == 0) commRemindBean?.startHour else commRemindBean?.endHour
        val minuteValue = if(code == 0) commRemindBean?.endHour else commRemindBean?.endMinute

        var hourPosition = 0
        var minutePosition = 0

        for(i in 0..23){
            if(hourValue == i){
                hourPosition = i
            }
        }

        for(n in 0..59){
            if(minuteValue == n){
                minutePosition = n
            }
        }


        val dialog = TimeSelectorDialog(this,R.style.edit_AlertDialog_style)
        dialog.show()
        dialog.setChoosePosition(hourPosition,minutePosition)
        dialog.setOnListener(object : TimeSelectorDialog.SignalSelectListener{
            override fun onSignalSelect(hour: String, minute: String) {
               val time = "$hour:$minute"

                if(code == 0){
                    comRemindStartTimeTv?.text = time
                    if(commRemindBean == null){
                        commRemindBean = CommRemindBean()
                    }
                    commRemindBean?.startHour = hour.toInt()
                    commRemindBean?.startMinute = minute.toInt()
                }else{
                    comRemindEndTimeTv?.text = time
                    if(commRemindBean == null){
                        commRemindBean = CommRemindBean()
                    }
                    commRemindBean?.endHour = hour.toInt()
                    commRemindBean?.endMinute = minute.toInt()
                }
            }

        })

        val window = dialog.window
        val attributeSet = window?.attributes
        attributeSet?.gravity = Gravity.BOTTOM
        window?.attributes = attributeSet

    }


    //根据标识显示标题栏
    private fun showTitle(code : Int) : String{
        if(code == 0x00){   //久坐
            return "久坐提醒"
        }
        if(code == 0x01){
            return "喝水提醒"
        }
        if(code == 0x02){
            return "勿扰模式"
        }
        return "久坐提醒"
    }



    //根据类型查询展示数据
    private fun showTypeData(type : Int){
        val mac = MmkvUtils.getConnDeviceMac()
        if(BikeUtils.isEmpty(mac)){
            return
        }

        var bean : CommRemindBean ?= null
        bean = DbManager.getInstance().getDataForType(type,mac)
        Log.e("tg","----00--dbbb="+Gson().toJson(bean))
        if(bean == null){
            DbManager.getInstance().initCommRemind(type,mac)
        }
        GlobalScope.launch {
            delay(1000)
        }

        bean = DbManager.getInstance().getDataForType(type,mac)
        Log.e("tg","----111--dbbb="+Gson().toJson(bean))

        commRemindBean = bean
        commRemindBean?.type = type
        commRemindBean?.deviceMac = mac
        comRemindStartTimeTv?.text = bean.hourAndMinute

        comRemindEndTimeTv?.text = bean.endHourAndMinute
        commRemindSwitch?.isChecked = bean.switchStatus == 1
    }
}