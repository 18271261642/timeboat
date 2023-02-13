package net.sgztech.timeboat.ui.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blala.blalable.Utils
import com.blala.blalable.listener.WriteBackDataListener
import com.blala.blalable.timeboat.OnTimeBoatSyncQrcodeListener
import com.blala.blalable.timeboat.TimeBoatConstance
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.google.gson.Gson
import com.hjq.shape.view.ShapeTextView
import com.imlaidian.utilslibrary.utils.LogUtil
import net.sgztech.timeboat.R
import net.sgztech.timeboat.TimeBoatApplication
import net.sgztech.timeboat.adapter.DialAdapter
import net.sgztech.timeboat.bean.DialBean
import net.sgztech.timeboat.listeners.OnCommItemClickListener
import net.sgztech.timeboat.ui.newui.ConnStatus
import net.sgztech.timeboat.ui.utils.ReadUtils
import kotlin.experimental.and

/**
 * 表盘市场页面
 */
class DialActivity : BaseActivity() {

    private val TAG = "DialActivity"

    //预览的图片
    private var dialPreviewImgView : ImageView ?= null
    //rv
    private var dialRecyclerView : RecyclerView ?= null

    //title
    private var title_name : TextView ?= null
    private var save_user_info : TextView ?= null
    private var back_arrow : ImageView ?= null

    //发送表盘
    private var dialSendTv : ShapeTextView ?= null
    //进度
    private var dialScheduleTv : TextView ?= null

    private var list : MutableList<DialBean> ?= null
    private var adapter : DialAdapter ?= null


    //选择的表盘下标id,从0开始
    private var selectDialIndex = 0


    //是否正在升级
    private var isDialUpgrade = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    override fun getLayoutId(): Int {
       return R.layout.activity_dial_layout
    }

    override fun initBindView() {

        dialScheduleTv = findViewById(R.id.dialScheduleTv)
        dialSendTv = findViewById(R.id.dialSendTv)
        title_name = findViewById(R.id.title_name)
        save_user_info = findViewById(R.id.save_user_info)
        back_arrow = findViewById(R.id.back_arrow)
        dialPreviewImgView = findViewById(R.id.dialPreviewImgView)
        dialRecyclerView = findViewById(R.id.dialRecyclerView)
        val gridLayoutManager = GridLayoutManager(this,3)
        dialRecyclerView?.layoutManager = gridLayoutManager

        list = mutableListOf()
        adapter = DialAdapter(this@DialActivity,list)
        dialRecyclerView?.adapter = adapter

        adapter?.setOnItemClick(onItemClick)

        initImgs()

        title_name?.text = "表盘中心"
        save_user_info?.visibility = View.GONE
        back_arrow?.setOnClickListener { finish() }

        val options = RequestOptions.bitmapTransform(RoundedCorners(45))
        dialPreviewImgView?.let {
            Glide.with(this@DialActivity).load(R.drawable.dial_10_bg).apply(options).into(
                it
            )
        }

    }


    override fun initData() {
        super.initData()

        save_user_info?.setOnClickListener {
            TimeBoatApplication.timeBoatApplication.getBleOperate()?.sendTimeBoatStartDial()
        }

        title_name?.setOnClickListener {
            TimeBoatApplication.timeBoatApplication.getBleOperate()?.sendTimeBoatEndDial()
        }

        dialSendTv?.setOnClickListener {
           //判断是否连接
            if(TimeBoatApplication.timeBoatApplication.connStatus != ConnStatus.CONNECTED){
                Toast.makeText(this@DialActivity,"设备未连接,请连接设备!",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startDial()
        }

    }


    //开始发送表盘
    private fun startDial(){
        isDialUpgrade = true
        dialSendTv?.isClickable = false
        dialScheduleTv?.text = "同步中.."

        TimeBoatApplication.timeBoatApplication.getBleOperate()?.sendTimeBoatStartDial(selectDialIndex,object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
                LogUtil.e(TAG, "-------111-开始同步=" + Utils.formatBtArrayToString(data))
                if (data != null) {
                    val isTrue =
                        data.size == 14 && (data[0] and 0xFF.toByte()).toInt() == -86 && (data[1] and 0xFF.toByte()).toInt() == 71 && (data[2] and 0xFF.toByte()).toInt() == 6
                    LogUtil.e(
                        TAG,
                        "---------是否相同=" + isTrue + " " + (data[0] and 0xFF.toByte()).toInt()
                    )
                }

                //aa470600f0f0f0f0030000000000
                //aa 47 06 00 f0 f0 f0 f0 03 00 00 00 00 00
                if (data != null) {
                    if (data.size == 14 && (data[0] and 0xFF.toByte()).toInt() == -86 && (data[1] and 0xFF.toByte()).toInt() == 71 && (data[2] and 0xFF.toByte()).toInt() == 6) {
                        sendToDial()
                    }
                }
            }

        })

    }

    private fun sendToDial(){
        LogUtil.e(TAG,"------选择的ID="+selectDialIndex)
        val inputStream = this.resources.openRawResource(getRawId(selectDialIndex))

        val array = inputStream.readBytes()
        val listArray = mutableListOf<ByteArray>()

        val count = array.size / 175
        val remainder = array.size % 175


        for(i in 0 until count){
            val tempArray = ByteArray(175)
            System.arraycopy(array,i * 175,tempArray,0,tempArray.size)
            listArray.add(tempArray)

        }

        if(remainder != 0){
            val tempArray = ByteArray(remainder)
            System.arraycopy(array,count * 175,tempArray,0,remainder)
            listArray.add(tempArray)
        }

        LogUtil.e("----RAW=","---RAW内容="+array.size+" "+listArray.size+" "+count+" "+remainder+"\n"+Gson().toJson(listArray.get(listArray.size-1))+"\n"+Gson().toJson(listArray.get(0)))

        // val tt = ReadUtils().readFromByteFile()

        val resultList = mutableListOf<ByteArray>()

        listArray.forEachIndexed { index, bytes ->
            val tempResultArray = ByteArray(bytes.size+7)

            tempResultArray[0] = 0x04
            tempResultArray[1] = (index and 0xff).toByte()
            tempResultArray[2] = (index shr 0x08 and 0xff).toByte()

            System.arraycopy(bytes,0,tempResultArray,7,bytes.size)
            resultList.add(tempResultArray)
        }

        inputStream.close()

        LogUtil.e("TAG","------reesult="+Gson().toJson(resultList.get(0))+"\n"+Gson().toJson(resultList.get(resultList.size-1)))
        TimeBoatApplication.timeBoatApplication.getBleOperate()?.sendTimeBoatDial(resultList,object : OnTimeBoatSyncQrcodeListener{
            override fun onSyncStatus(countSchedule: Int, current: Int) {
                dialScheduleTv?.text = "发送进度: "+(current+1)+"/"+countSchedule

                //同步失败
                if(current == -1 && countSchedule == -1){
                    isDialUpgrade = false
                    dialScheduleTv?.text = "表盘发送失败,请重新尝试!"
                    dialSendTv?.isClickable = true
                }

                //同步完成
                if(countSchedule == (current+1) && count != 0){
                    isDialUpgrade = false
                    dialSendTv?.isClickable = true
                    endSync()
                }
            }

        })
    }

    private fun endSync(){
        TimeBoatApplication.timeBoatApplication.getBleOperate()?.sendTimeBoatEndDial(object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
                LogUtil.e(TAG,"-------结束同步="+Utils.formatBtArrayToString(data))
                //aa 47 02 00 f0 f0 f0 f0 05 01

                //aa470200f0f0f0f00501
                if(data != null){
                    if(data.size == 10 && data[0].toInt() == -86 && data[2].toInt() == 2 && data[8].toInt() == 5 && data[9].toInt() == 1){
                        dialScheduleTv?.text = "同步完成!"
                        dialSendTv?.isClickable = true
                    }
                }
            }

        })
    }






    private val onItemClick : OnCommItemClickListener =
        OnCommItemClickListener { position ->
            if(isDialUpgrade){

                Toast.makeText(this@DialActivity,"正在同步表盘,请同步完再尝试!",Toast.LENGTH_SHORT).show()
                return@OnCommItemClickListener
            }
            dialScheduleTv?.text = " "

            val resourceId = list?.get(position)?.resourceId
            selectDialIndex = position
            list?.forEach {
                it.isChecked = false
            }
             list?.get(position)?.isChecked = true
            adapter?.notifyDataSetChanged()
            val options = RequestOptions.bitmapTransform(RoundedCorners(45))
            dialPreviewImgView?.let {
                Glide.with(this@DialActivity).load(resourceId).apply(options).into(
                    it
                )
            }
        }

    private fun initImgs(){
        val imgList = arrayListOf<Int>(R.drawable.dial_10_bg,R.drawable.dial_12_bg,R.drawable.dial_13_bg,R.drawable.dial_14_bg,R.drawable.dial_15_bg,R.drawable.dial_16_bg,
        R.drawable.dial_17_bg,R.drawable.dial_18_bg,R.drawable.dial_19_bg,R.drawable.dial_20_bg)
        list?.clear()
        imgList.forEach {
            val bean = DialBean(it,false)
            list?.add(bean)
        }
        list?.get(0)?.isChecked = true
        adapter?.notifyDataSetChanged()
    }



    //根据position获取
    private fun getRawId(position : Int) : Int{
        if(position == 0){
            return R.raw.watch_10
        }

        if(position == 1){
            return R.raw.watch_12
        }

        if(position == 2){
            return R.raw.watch_13
        }
        if(position == 3){
            return R.raw.watch_14
        }
        if(position == 4){
            return R.raw.watch_15
        }

        if(position == 5){
            return R.raw.watch_16
        }

        if(position == 6){
            return R.raw.watch_17
        }
        if(position == 7){
            return R.raw.watch_18
        }
        if(position == 8){
            return R.raw.watch_19
        }

        if(position == 9){
            return R.raw.watch_20
        }


        return R.raw.watch_10
    }
}