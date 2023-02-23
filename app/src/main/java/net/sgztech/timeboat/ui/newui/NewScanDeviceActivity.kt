package net.sgztech.timeboat.ui.newui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blala.blalable.listener.BleConnStatusListener
import com.blala.blalable.listener.ConnStatusListener
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.hjq.permissions.XXPermissions
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import net.sgztech.timeboat.R
import net.sgztech.timeboat.TimeBoatApplication
import net.sgztech.timeboat.ui.utils.MmkvUtils
import net.sgztech.timeboat.util.BikeUtils
import net.sgztech.timeboat.util.BonlalaUtils
import java.util.*

/**
 * 扫描页面
 * Created by Admin
 *Date 2023/1/9
 */
class NewScanDeviceActivity : BaseActivity(),OnItemClickListener{


    //adapter
    private var scanDeviceAdapter : ScanDeviceAdapter ?= null
    private var scan_results : RecyclerView ?= null
    private var listData : MutableList<BleBean> ?= null
    //用于去重的list
    private var repeatList : MutableList<String> ?= null
    //title
    private var title_name : TextView ?= null
    private var back_arrow : ImageView ?= null

    private var handlers : Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 0x00){
                TimeBoatApplication.timeBoatApplication.getBleOperate()?.stopScanDevice()
            }
            if(msg.what == 0x01){   //连接超时
                //connTimeOut()
            }
        }
    }

    override fun getLayoutId(): Int {
        return  R.layout.activity_scan
    }

    override fun initBindView() {

        back_arrow = findViewById(R.id.back_arrow)
        title_name = findViewById(R.id.title_name)
        repeatList = ArrayList<String>()
        scan_results = findViewById(R.id.scan_results)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        listData =ArrayList<BleBean>()
        scanDeviceAdapter = ScanDeviceAdapter(this,listData)
        scan_results?.adapter = scanDeviceAdapter
        scanDeviceAdapter?.setOnItemClickListener(this)


        verifyScanFun()
    }


    override fun initData() {
        super.initData()

        title_name?.text = "搜索设备"
        back_arrow?.setOnClickListener {
            finish()
        }
    }

    private fun verifyScanFun(){

        //判断蓝牙是否开启
        if(!BikeUtils.isBleEnable(this)){
            BikeUtils.openBletooth(this)
            return
        }


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            // Android 版本大于等于 Android12 时
            // 只包括蓝牙这部分的权限，其余的需要什么权限自己添加
            XXPermissions.with(this).permission(arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE)).request { permissions, all ->
                //verifyScanFun()
            }
        } else {
            // Android 版本小于 Android12 及以下版本
            XXPermissions.with(this).permission(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)).request { permissions, all ->
//                verifyScanFun()
            }
        }


        //判断权限
        val isPermission = ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if(!isPermission){
            XXPermissions.with(this).permission(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)).request { permissions, all ->
//                verifyScanFun()
            }
            // ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),0x00)
            return
        }

        //判断蓝牙是否打开
        val isOpenBle = BonlalaUtils.isOpenBlue(this@NewScanDeviceActivity)
        if(!isOpenBle){
            BonlalaUtils.openBluetooth(this)
            return
        }

        startScanDevice()
    }

    override fun onIteClick(position: Int) {
        val bleMac = listData?.get(position)?.bluetoothDevice?.address
        val bleName =  listData?.get(position)?.bluetoothDevice?.name
        handlers.sendEmptyMessageDelayed(0x00,500)
        showLoadingDialog("连接中...")
        TimeBoatApplication.timeBoatApplication.getConnStatusService()?.connDeviceBack(bleName,bleMac
        ) { mac, status ->
            MmkvUtils.saveConnDeviceMac(bleMac)
            MmkvUtils.saveConnDeviceName(bleName)
            dismissLoadingDialog()
            //连接成功
            finish()

        }

    }

    //搜索
    private fun startScanDevice(){
        listData?.clear()
        repeatList?.clear()


        TimeBoatApplication.timeBoatApplication.getBleOperate()?.scanBleDevice(object : SearchResponse {
            override fun onSearchStarted() {
                Log.e("sanc","-----开始扫描")
            }

            override fun onDeviceFounded(p0: SearchResult) {
               // scanStatusTv.text = resources.getString(R.string.string_scan_ing)

                val bleName = p0.name
                if(BikeUtils.isEmpty(bleName) || bleName.equals("NULL") || BikeUtils.isEmpty(p0.address))
                    return
//                if(!bleName.lowercase(Locale.ROOT).contains("w561b") && !bleName.lowercase(Locale.ROOT).contains("w560b"))
//                    return
                if(repeatList?.contains(p0.address) == true)
                    return
                //判断少于40个设备就不添加了
                if(repeatList?.size!! >40){
                    return
                }

                repeatList?.add(p0.address)
                listData?.add(BleBean(p0.device,p0.rssi))
                listData?.sortBy {
                    Math.abs(it.rssi)
                }
                scanDeviceAdapter?.notifyDataSetChanged()

            }

            override fun onSearchStopped() {
              //  scanStatusTv.text = resources.getString(R.string.string_scan_complete)

            }

            override fun onSearchCanceled() {
               // scanStatusTv.text = resources.getString(R.string.string_scan_complete)

            }

        },1500 * 1000,1)
    }


    override fun onDestroy() {
        super.onDestroy()

        TimeBoatApplication.timeBoatApplication.getBleOperate()?.stopScanDevice()
    }

}