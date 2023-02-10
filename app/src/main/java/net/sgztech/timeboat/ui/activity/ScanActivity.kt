package net.sgztech.timeboat.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.device.rxble.RxBleClient
import com.device.rxble.exceptions.BleScanException
import com.device.rxble.scan.ScanResult
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.device.ui.viewBinding.viewBinding
import com.imlaidian.utilslibrary.utils.LogUtil
import com.imlaidian.utilslibrary.utils.UToast
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants.Companion.MAC_Address
import net.sgztech.timeboat.databinding.ActivityScanBinding
import net.sgztech.timeboat.managerUtlis.BleServiceManager
import net.sgztech.timeboat.provide.viewModel.ScanBleViewModel
import net.sgztech.timeboat.provide.viewModel.ScanBleViewModel.Companion.disposeScanBleDevices
import net.sgztech.timeboat.provide.viewModel.ScanBleViewModel.Companion.scanBleDevicesError
import net.sgztech.timeboat.provide.viewModel.ScanBleViewModel.Companion.scanBleDevicesSuccess
import net.sgztech.timeboat.provide.viewModel.ScanBleViewModel.Companion.scanBlePermission
import net.sgztech.timeboat.provide.viewModel.ScanBleViewModel.Companion.startScanBleDevice
import net.sgztech.timeboat.ui.adapter.ScanResultsAdapter
import net.sgztech.timeboat.util.*
import net.sgztech.timeboat.util.requestScanPermission
import net.sgztech.timeboat.util.showError

/**
 * 扫描页面
 */
class ScanActivity : BaseActivity() {

    private val TAG =ScanActivity::class.java.simpleName
    private val binding : ActivityScanBinding by viewBinding()
    private val isTestBleMode =false
    private var reconnectAddress :String?=null
    private val scanBleViewModel: ScanBleViewModel by viewModels()


    private val resultsAdapter =
        ScanResultsAdapter { result ,position->
            LogUtil.d(TAG, "onConnectToggleClick mac=" +result.bleDevice.macAddress  + ",index=" + position)
            if(isTestBleMode){
                val intent  = Intent(this@ScanActivity ,BleCommandActivity::class.java)
                intent.putExtra(MAC_Address ,result.bleDevice.macAddress )
                startActivity(intent)
            }else{
                BleServiceManager.instance.setBleDevice(result.bleDevice.macAddress )
            }
            scanBleViewModel.onPause()
            finish()
        }


    override fun getLayoutId(): Int {
        return  R.layout.activity_scan
    }

    override fun initBindView() {
//        binding.addDevice.titleName.text= "添加设备"
//        binding.addDevice.backArrow.setOnClickListener(this)
        reconnectAddress = intent.getStringExtra(MAC_Address)


    }

    fun isLocationEnabled(): Boolean {
        var enabled = false
        try {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            enabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                locationManager.isLocationEnabled
            } else {
                Settings.Secure.getInt(contentResolver,
                    Settings.Secure.LOCATION_MODE) != Settings.Secure.LOCATION_MODE_OFF
            }
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
            Log.e(TAG, "Cannot check GPS status: " + e.message, e)
        }
        return enabled
    }


    fun checkLocation() {
        if(!isLocationEnabled()){
            enableLocation.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    //打开定位
    val  enableLocation =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == Activity.RESULT_OK) {
                UToast.showShortToast(if (isLocationEnabled()) "定位已打开" else "定位未打开")
            }
        }

    override fun initData() {
        configureResultList()
        scanBleViewModel.liveData.observe(this@ScanActivity, androidx.lifecycle.Observer {
            when (it.what) {
                     scanBlePermission -> requestScanPermission(it.obj as RxBleClient)

                     scanBleDevicesSuccess -> {
                         resultsAdapter.addScanResult(it.obj as ScanResult)
//                         dismissLoadingDialog()
                     }

                     scanBleDevicesError ->  {
                         if (it.obj is BleScanException) showError(it.obj as BleScanException )
                     }

                      startScanBleDevice ->   {
                          LogUtil.d(TAG, "updateScanState startScan" )
//                          showLoadingDialog("正在扫描蓝牙设备")
                     }

                     disposeScanBleDevices -> {
                         if(resultsAdapter.itemCount==0){
//                             dismissLoadingDialog()
                             UToast.showShortToast("扫描结束,未发现设备")
                             binding.scanStatus.text="扫描结束,未发现设备"
                             binding.progressBar.isVisible=false
                         }
                     }

            }
        })
        if(reconnectAddress!=null &&reconnectAddress!!.isNotEmpty()){
            if(reconnectAddress!=null){
                scanBleViewModel.macAddress = reconnectAddress!!
            }
        }
        scanBleViewModel.onScanToggleClick()
    }


    private fun configureResultList() {
        with(binding.scanResults) {
            setHasFixedSize(true)
            itemAnimator = null
            adapter = resultsAdapter
        }
    }

    override fun widgetClick(v: View) {
        super.widgetClick(v)
        if(v.id==R.id.back_arrow){
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (isScanPermissionGranted(requestCode, grantResults)) {
            scanBleViewModel.onScanToggleClick()
        }else{
            LogUtil.d(TAG, "onRequestPermissionsResult not allow" )
            checkLocation()
        }
    }




    override fun onPause() {
        super.onPause()
        // Stop scanning in onPause callback.
        scanBleViewModel.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        scanBleViewModel.onPause()
    }
}