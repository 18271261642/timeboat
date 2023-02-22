package net.sgztech.timeboat.ui.activity

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blala.blalable.Utils
import com.bonlala.base.BaseDialog
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.google.gson.Gson
import net.sgztech.timeboat.R
import net.sgztech.timeboat.adapter.AlarmAdapterJava
import net.sgztech.timeboat.bean.AlarmBean
import net.sgztech.timeboat.bean.DbManager
import net.sgztech.timeboat.ui.dialog.SelectDialog
import net.sgztech.timeboat.ui.dialog.TimeDialog
import java.util.HashMap
import kotlin.experimental.and

/**
 * Created by Admin
 *Date 2023/2/22
 */
class AlarmListActivity : BaseActivity() {

    //title
    private var title_name : TextView?= null
    //返回
    private var back_arrow : ImageView?= null

    private var recyclerView : RecyclerView ?= null
    private var list : MutableList<AlarmBean> ?= null

    private var adapter : AlarmAdapterJava ?= null

    private  var timeDialog : TimeDialog.Builder ?= null

    override fun getLayoutId(): Int {
        return R.layout.activity_alarm_list_layout
    }

    override fun initBindView() {
        back_arrow = findViewById(R.id.back_arrow)
        title_name = findViewById(R.id.title_name)
        recyclerView = findViewById(R.id.alarmRecyclerView)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView?.layoutManager = linearLayoutManager

        list = ArrayList<AlarmBean>()

        adapter = AlarmAdapterJava(this,list)
        recyclerView?.adapter = adapter

        back_arrow?.setOnClickListener {
            finish()
        }
        title_name?.text = "闹钟"
    }

    override fun initData() {
        super.initData()
        initAlarmData()

        showAlarm()
    }


    //初始化闹钟，默认添加3个闹钟
    private fun initAlarmData(){
        DbManager.getInstance().initAlarm()
    }


    //显示闹钟
    private fun showAlarm(){
        val alarmList = DbManager.getInstance().allAlarm
        if(alarmList != null){
            list?.clear()
            list?.addAll(alarmList)
            adapter?.notifyDataSetChanged()
        }
    }

    private fun showDialogSelect(code : Int){
        val bean = list?.get(code)
        timeDialog = bean?.repeat?.toInt()?.let {
            bean?.hour?.let { it1 ->
                TimeDialog.Builder(this)
                    .setIgnoreSecond()
                    .isShowRepeat(true)
                    .setTitle(resources.getString(R.string.string_alarm))
                    .setWeekRepestValue(it)
                    .setChooseRepeat(getRepeat(bean.repeat))
                    .setHour(it1)
                    .setMinute(bean.minute)
                    .setListener(object : TimeDialog.OnListener{
                        override fun onSelected(dialog: BaseDialog?, hour: Int, minute: Int, second: Int) {
                            dialog?.dismiss()
                            val timeStr = String.format("%02d",hour)+":"+String.format("%02d",minute)

                            bean.hour = hour
                            bean.minute = minute
                            bean.repeat = timeDialog?.weekRepestValue?.toByte()!!
                            bean.isOpen = true
                            adapter?.notifyDataSetChanged()

//                            adapter?.getItem(code)?.let { setChooseAlarm(it) }

                        }

                        override fun onClickRepeatClick() {
//                            showWeekRepeat(alarmList.get(code).repeat)
                        }
                    })
            }
        }
        timeDialog?.create()?.show()

    }



    private val weekList = mutableListOf<String>()

    private var weekMap = HashMap<Int,Byte>()

    private fun showWeekRepeat(repeat : Byte){
        weekList.clear()
        weekMap.clear()

        weekList.add(resources.getString(R.string.sun))
        weekList.add(resources.getString(R.string.mon))
        weekList.add(resources.getString(R.string.tue))
        weekList.add(resources.getString(R.string.wed))
        weekList.add(resources.getString(R.string.thu))
        weekList.add(resources.getString(R.string.fri))
        weekList.add(resources.getString(R.string.sat))

        weekMap[0] = 1
        weekMap[1] = 2
        weekMap[2] = 4
        weekMap[3] = 8
        weekMap[4] = 16
        weekMap[5] = 32
        weekMap[6] = 64

        val repeatList = mutableListOf<Int>()
        val array = intArrayOf(1, 2, 4, 8, 16, 32, 64)

        if(repeat.toInt() == 0){
            repeatList.add(-1)
        }else{
            for (i in array.indices) {
                val v = (repeat and array[i].toByte()).toInt()
                if(v == array[i]){
                    repeatList.add(i)
                }
            }
        }

        val weekDialog = SelectDialog.Builder(this)
            .setList(weekList)
            .setSelect(repeatList)
            .setListener { dialog, data ->
                val tempWMap = HashMap<Int,Int>()
                val stringBuilder = StringBuilder()
                dialog.dismiss()
                data.forEach {
                    if(it.key == -1){
                        tempWMap[-1] = 0
                        stringBuilder.append(it.value)
                    }else{
                        tempWMap[it.key] = 0
                        stringBuilder.append(it.value)
                    }
                }
                //  Log.e("AA", "-----周=" + data.toString())
//                timeDialog?.setChooseRepeat(stringBuilder.toString())
//                saveAlarm(tempWMap)
            }
            .create().show()
    }



    var stringBuilder = java.lang.StringBuilder()
    private fun getRepeat(repeat: Byte): String? {
        val repeatStr = ""
        stringBuilder.delete(0, stringBuilder.length)
        //转bit
        val bitStr = Utils.byteToBit(repeat)
        val repeatArray = Utils.byteToBitOfArray(repeat)
        if (repeat.toInt() == 0) {
            return resources.getString(R.string.once)
        }

        //[0, 0, 0, 1, 0, 0, 0, 1] 周四，周日
        if (repeat.toInt() == 127) {  //每天
            return resources.getString(R.string.every_day)
        }
        //周末
        if ((repeat and 0xff.toByte()).toInt() == 65) {
            return resources.getString(R.string.wenkend_day)
        }
        if ((repeat and 0xff.toByte()).toInt() == 62) {  //工作日
            return resources.getString(R.string.work_day)
        }
        if (repeatArray[7].toInt() == 1) {    //周日
            stringBuilder.append(resources.getString(R.string.sun))
        }
        if (repeatArray[6].toInt() == 1) {    //周一
            stringBuilder.append(resources.getString(R.string.mon))
        }
        if (repeatArray[5].toInt()  == 1) {    //周二
            stringBuilder.append(resources.getString(R.string.tue))
        }
        if (repeatArray[4].toInt()  == 1) {    //周三
            stringBuilder.append(resources.getString(R.string.wed))
        }
        if (repeatArray[3].toInt()  == 1) {    //周四
            stringBuilder.append(resources.getString(R.string.thu))
        }
        if (repeatArray[2].toInt()  == 1) {    //周五
            stringBuilder.append(resources.getString(R.string.fri))
        }
        if (repeatArray[1].toInt()  == 1) {    //周六
            stringBuilder.append(resources.getString(R.string.sat))
        }
        return stringBuilder.toString()
    }
}