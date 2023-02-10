package net.sgztech.timeboat.ui.newui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.blala.blalable.Utils
import com.blala.blalable.listener.WriteBackDataListener
import com.blala.blalable.timeboat.OnTimeBoatSyncQrcodeListener
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.google.gson.Gson
import com.hjq.shape.view.ShapeTextView
import com.imlaidian.utilslibrary.utils.LogUtil
import net.sgztech.timeboat.R
import net.sgztech.timeboat.TimeBoatApplication
import net.sgztech.timeboat.ui.utils.MmkvUtils
import net.sgztech.timeboat.util.BikeUtils
import kotlin.experimental.and

/**
 * 同步表盘二维码
 * Created by Admin
 *Date 2023/1/12
 */
class QrcodeDialActivity : BaseActivity(){


    private val tags = "QrcodeDialActivity"


    //title
    private var title_name : TextView?= null
    //返回
    private var back_arrow : ImageView?= null

    //选择设备的按钮
    private var qrcodeSelectDeviceTv : ShapeTextView ?= null
    //已经选的name
    private var qrcodeSelectDeviceNameTv : TextView ?= null
    //已经连接的mac
    private var qrcodeSelectDeviceMacTv : TextView ?= null
    //进度
    private var qrcodeSyncScheduleTv : TextView ?= null
    //开始按钮
    private var qrcodeSyncBtnTv : ShapeTextView ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_qrcode_dial_layout
    }

    override fun initBindView() {
        back_arrow = findViewById(R.id.back_arrow)
        title_name = findViewById(R.id.title_name)
        qrcodeSelectDeviceTv = findViewById(R.id.qrcodeSelectDeviceTv)
        qrcodeSelectDeviceNameTv = findViewById(R.id.qrcodeSelectDeviceNameTv)
        qrcodeSelectDeviceMacTv = findViewById(R.id.qrcodeSelectDeviceMacTv)
        qrcodeSyncScheduleTv = findViewById(R.id.qrcodeSyncScheduleTv)
        qrcodeSyncBtnTv = findViewById(R.id.qrcodeSyncBtnTv)



    }

    override fun initData() {
        super.initData()

        title_name?.text = "发送二维码表盘"
        qrcodeSyncBtnTv?.setOnClickListener (this)
        qrcodeSelectDeviceTv?.setOnClickListener(this)
        back_arrow?.visibility = View.GONE
    }


    override fun onResume() {
        super.onResume()

        LogUtil.e(tags,"-------状态="+TimeBoatApplication.timeBoatApplication.connStatus)

        showConn()

        qrcodeSyncScheduleTv?.text = ""
    }


    private fun showConn(){

        val mac = MmkvUtils.getConnDeviceMac()
        val name = MmkvUtils.getConnDeviceName()

        qrcodeSelectDeviceNameTv?.text = "名称: "+(if(BikeUtils.isEmpty(name)) "--" else name)
        qrcodeSelectDeviceMacTv?.text = "Mac: "+(if(BikeUtils.isEmpty(mac)) "--" else mac)

        val isConn =TimeBoatApplication.timeBoatApplication.connStatus == ConnStatus.CONNECTED

        qrcodeSyncBtnTv?.isClickable = isConn

    }


    override fun onClick(v: View) {
        super.onClick(v)

        val id = v.id
        when (id){
            //选择连接设备
            R.id.qrcodeSelectDeviceTv->{
                startActivity(Intent(this@QrcodeDialActivity,NewScanDeviceActivity::class.java))
            }

            //开始同步
            R.id.qrcodeSyncBtnTv->{
                startSync()
            }
        }
    }


    //开始同步
    private fun startSync(){
        qrcodeSyncBtnTv?.isClickable = false
        qrcodeSyncScheduleTv?.text = "同步中.."
        TimeBoatApplication.timeBoatApplication.getBleOperate()?.sendTimeBoatStartDial(object :
            WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
                LogUtil.e(tags,"-------111-开始同步="+Utils.formatBtArrayToString(data))
                if(data != null){
                    val isTrue = data.size == 14 && (data[0] and 0xFF.toByte()).toInt() == -86 && (data[1] and 0xFF.toByte()).toInt() == 71  && (data[2] and 0xFF.toByte()).toInt() == 6
                    LogUtil.e(tags,"---------是否相同="+isTrue +" "+(data[0] and 0xFF.toByte()).toInt())
                }

                //aa470600f0f0f0f0030000000000
                //aa 47 06 00 f0 f0 f0 f0 03 00 00 00 00 00
                if (data != null) {
                    if(data.size == 14 && (data[0] and 0xFF.toByte()).toInt() == -86 && (data[1] and 0xFF.toByte()).toInt() == 71  && (data[2] and 0xFF.toByte()).toInt() == 6){
                        sendToDial()
                    }

                }
            }

        })
    }




    private fun endSync(){
        TimeBoatApplication.timeBoatApplication.getBleOperate()?.sendTimeBoatEndDial(object : WriteBackDataListener{
            override fun backWriteData(data: ByteArray?) {
                LogUtil.e(tags,"-------结束同步="+Utils.formatBtArrayToString(data))
                //aa 47 02 00 f0 f0 f0 f0 05 01

                //aa470200f0f0f0f00501
                if(data != null){
                    if(data.size == 10 && data[0].toInt() == -86 && data[2].toInt() == 2 && data[8].toInt() == 5 && data[9].toInt() == 1){
                        qrcodeSyncScheduleTv?.text = "同步完成,请选择下一个设备!"
                        reInit()

                        qrcodeSyncBtnTv?.isClickable = false
                    }
                }
            }

        })
    }

    private fun reInit(){
        showConn()
        TimeBoatApplication.timeBoatApplication.getBleOperate()?.disConnYakDevice()

        MmkvUtils.saveConnDeviceName(null)
        MmkvUtils.saveConnDeviceMac(null)
    }


    private fun sendToDial(){
        val inputStream = this.resources.openRawResource(R.raw.qrcode)

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

        LogUtil.e("----RAW=","---RAW内容="+array.size+" "+listArray.size+" "+count+" "+remainder+"\n"+ Gson().toJson(listArray.get(listArray.size-1))+"\n"+ Gson().toJson(listArray.get(0)))

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

        LogUtil.e("TAG","------reesult="+ Gson().toJson(resultList.get(0))+"\n"+ Gson().toJson(resultList.get(resultList.size-1)))
        TimeBoatApplication.timeBoatApplication.getBleOperate()?.sendTimeBoatDial(resultList,object : OnTimeBoatSyncQrcodeListener{
            override fun onSyncStatus(countSchedule: Int, current: Int) {
                qrcodeSyncScheduleTv?.text = "发送进度: "+(current+1) +" /" +countSchedule
                //同步完了
                if(countSchedule == (current+1) && count != 0){
                    endSync()
                }
            }

        })
    }


    override fun onDestroy() {
        super.onDestroy()


        MmkvUtils.saveConnDeviceName(null)
        MmkvUtils.saveConnDeviceMac(null)

        TimeBoatApplication.timeBoatApplication.getBleOperate()?.disConnYakDevice()
    }


}