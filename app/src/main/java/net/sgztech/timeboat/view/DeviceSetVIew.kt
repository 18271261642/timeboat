package net.sgztech.timeboat.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import net.sgztech.timeboat.R
import net.sgztech.timeboat.listeners.OnCommItemClickListener

/**
 * 设备设置view
 */
class DeviceSetVIew : LinearLayout,OnClickListener{


    private var onItemClick : OnCommItemClickListener?= null
    fun setOnSetListener(onListener : OnCommItemClickListener){
        this.onItemClick = onListener
    }


    //查找手表
    private var deviceFindWatchLayout : LinearLayout ?= null
    //蓝牙通话
    private var deviceBleCallLayout : LinearLayout ?= null
    //遥控拍照
    private var deviceTakePhotoLayout : LinearLayout ?= null
    //单位
    private var deviceUnitLayout : LinearLayout ?= null
    //消息通知
    private var deviceNotifyLayout : LinearLayout ?= null
    //关于设备
    private var deviceAboutLayout : LinearLayout ?= null



    constructor(context: Context) : super (context){

    }


    constructor(context: Context,attribute : AttributeSet) : super (context,attribute){
        initViews(context)
    }

    constructor(context: Context, attribute: AttributeSet, defStyleAttr : Int) : super (context,attribute,defStyleAttr){
        initViews(context)
    }

    private fun initViews(context: Context){
        val view = LayoutInflater.from(context).inflate(R.layout.item_device_set_layout,this,true)

        //查找手表
        deviceFindWatchLayout = view.findViewById(R.id.deviceFindWatchLayout)
        deviceBleCallLayout  = view.findViewById(R.id.deviceBleCallLayout)
        deviceTakePhotoLayout  = view.findViewById(R.id.deviceTakePhotoLayout)
        deviceUnitLayout  = view.findViewById(R.id.deviceUnitLayout)
        deviceNotifyLayout  = view.findViewById(R.id.deviceNotifyLayout)
        deviceAboutLayout  = view.findViewById(R.id.deviceAboutLayout)


        deviceFindWatchLayout?.setOnClickListener(this)
        deviceBleCallLayout?.setOnClickListener(this)
        deviceTakePhotoLayout?.setOnClickListener(this)
        deviceUnitLayout?.setOnClickListener(this)
        deviceNotifyLayout?.setOnClickListener(this)
        deviceAboutLayout?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val id = v?.id
        when(id){
            R.id.deviceFindWatchLayout->{
                onItemClick?.onItemClick(0x00)
            }
            //蓝牙通话
            R.id.deviceBleCallLayout->{
                onItemClick?.onItemClick(0x01)
            }
            //遥控拍照
            R.id.deviceTakePhotoLayout->{
                onItemClick?.onItemClick(0x02)
            }

            //单位
            R.id.deviceUnitLayout->{
                onItemClick?.onItemClick(0x03)
            }

            //消息通知
            R.id.deviceNotifyLayout->{
                onItemClick?.onItemClick(0x04)
            }

            //关于
            R.id.deviceAboutLayout->{
                onItemClick?.onItemClick(0x05)
            }
        }
    }
}