package net.sgztech.timeboat.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import com.bonlala.widget.view.StringScrollPicker
import net.sgztech.timeboat.R
import kotlin.math.min


/**
 * Created by Admin
 *Date 2023/2/22
 */
class TimeSelectorDialog : AppCompatDialog ,View.OnClickListener{

    private var onSelectListener : SignalSelectListener ?= null

    fun setOnListener(listener : SignalSelectListener){
        this.onSelectListener = listener
    }

    //小时
    private var hourPicker : StringScrollPicker ?= null
    //分钟
    private var minuterPicker : StringScrollPicker ?= null
    private var timeCancelTv : TextView ?= null
    private var timeSelectConfirmTv : TextView ?= null


    private var hourPosition = 0
    private var minutePosition = 0

    constructor(context: Context) : super (context){

    }


    constructor(context: Context,theme : Int) : super (context,theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_time_selector_layout)

        initViews()


        setDataSource()
    }


    private fun initViews(){
        hourPicker = findViewById(R.id.hourPicker)
        minuterPicker = findViewById(R.id.minuterPicker)
        timeCancelTv = findViewById(R.id.timeCancelTv)
        timeSelectConfirmTv = findViewById(R.id.timeSelectConfirmTv)


        timeCancelTv?.setOnClickListener(this)
        timeSelectConfirmTv?.setOnClickListener(this)
    }



    //设置数据
    fun setDataSource(){
        val hourList = mutableListOf<String>()
        for(i in 0..23){
            hourList.add(String.format("%02d",i))
        }

        hourPicker?.data = hourList as List<String>

        val minuteList = mutableListOf<String>()
        for(n in 0..59){
            minuteList.add(String.format("%02d",n))
        }


        minuterPicker?.data = minuteList as List<String>


        hourPicker?.setOnSelectedListener { scrollPickerView, position -> hourPosition = position }

        minuterPicker?.setOnSelectedListener { scrollPickerView, position ->
            minutePosition = position
        }

    }

    override fun onClick(p0: View?) {
        val id = p0?.id

        when(id){
            R.id.timeCancelTv->{
                dismiss()
            }

            R.id.timeSelectConfirmTv->{
                dismiss()
                val hourStr = hourPicker?.data?.get(hourPosition)
                val minuteStr = minuterPicker?.data?.get(minutePosition)
                onSelectListener?.onSignalSelect(hourStr as String, minuteStr as String)
            }
        }
    }


    interface SignalSelectListener {
        fun onSignalSelect(hour: String,minute : String)
    }
}