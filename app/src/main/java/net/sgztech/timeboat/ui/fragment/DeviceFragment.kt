package net.sgztech.timeboat.ui.fragment

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.blala.blalable.BleConstant
import com.device.rxble.RxBleClient
import com.device.rxble.RxBleConnection
import com.device.rxble.RxBleDevice
import com.device.rxble.exceptions.BleScanException
import com.device.rxble.scan.ScanResult
import com.device.ui.baseUi.baseFragment.BaseFragment
import com.device.ui.viewBinding.findRootView
import com.device.ui.viewBinding.viewBinding
import com.device.ui.viewModel.common.vmObserver
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.imlaidian.utilslibrary.utils.LogUtil
import com.imlaidian.utilslibrary.utils.UToast
import net.sgztech.timeboat.R
import net.sgztech.timeboat.TimeBoatApplication
import net.sgztech.timeboat.TimeBoatApplication.Companion.rxBleClient
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.Constants.Companion.DEL_DEVICES_STATUS
import net.sgztech.timeboat.config.Constants.Companion.REPOSITORY_RESULT_TOKEN_INVALID
import net.sgztech.timeboat.databinding.FragmentDevicesBinding
import net.sgztech.timeboat.listeners.OnCommItemClickListener
import net.sgztech.timeboat.managerUtlis.BleServiceManager
import net.sgztech.timeboat.managerUtlis.SettingInfoManager
import net.sgztech.timeboat.provide.viewModel.DeviceViewModel
import net.sgztech.timeboat.provide.viewModel.ScanBleViewModel
import net.sgztech.timeboat.provide.viewModel.ScanBleViewModel.Companion.autoDisconnect
import net.sgztech.timeboat.provide.viewModel.ScanBleViewModel.Companion.bleStatusPermission
import net.sgztech.timeboat.provide.viewModel.ScanBleViewModel.Companion.connectDiscovery
import net.sgztech.timeboat.provide.viewModel.ScanBleViewModel.Companion.connectFailed
import net.sgztech.timeboat.provide.viewModel.ScanBleViewModel.Companion.isConnectSuccess
import net.sgztech.timeboat.provide.viewModel.ScanBleViewModel.Companion.isConnecting
import net.sgztech.timeboat.provide.viewModel.ScanBleViewModel.Companion.isInit
import net.sgztech.timeboat.provide.viewModel.ScanBleViewModel.Companion.scanBleDevicesError
import net.sgztech.timeboat.ui.activity.*
import net.sgztech.timeboat.ui.activity.DelDeviceActivity.Companion.DEL_SUCCESS
import net.sgztech.timeboat.ui.newui.*
import net.sgztech.timeboat.ui.utils.MmkvUtils
import net.sgztech.timeboat.util.*
import net.sgztech.timeboat.view.DeviceSetVIew
import java.util.*


class DeviceFragment : BaseFragment() {
    private val TAG = DeviceFragment::class.java.simpleName
    private val binding: FragmentDevicesBinding by viewBinding()
    private val deviceViewModel: DeviceViewModel by viewModels()
    private var deviceStatus = isInit

    //    private var hasBleDevice = false
    private var openAutoConnect = false
    private lateinit var bleManager: BluetoothManager

    private var deviceSetView: DeviceSetVIew? = null

    //???????????????
    private var reconnect_layout: RelativeLayout? = null

    //????????????
    private var add_device: LinearLayout? = null

    //???????????????
    private var deviceSetLayout: LinearLayout? = null

    //???????????????
    private var device_name: TextView? = null

    //mac
    private var device_imei: TextView? = null

    private var deviceDeviceTv : TextView ?= null

    //??????
    private var reconnect: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentFilter = IntentFilter()
        intentFilter.addAction(BleConstant.BLE_CONNECTED_ACTION)
        intentFilter.addAction(BleConstant.BLE_DIS_CONNECT_ACTION)
        intentFilter.addAction(BleConstant.BLE_SCAN_COMPLETE_ACTION)
        activity?.registerReceiver(broadcastReceiver,intentFilter)
    }


    fun newInstance(): DeviceFragment {
        val fragment: DeviceFragment = DeviceFragment()
        fragment.setLabel(Constants.FRAGMENT_LABEL_DEVICES)
        fragment.setMainPage(false)
        return fragment
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_devices
    }

    override fun initBindView() {
        deviceDeviceTv= view?.findViewById(R.id.deviceDeviceTv)
        reconnect = view?.findViewById(R.id.reconnect)
        device_imei = view?.findViewById(R.id.device_imei)
        device_name = view?.findViewById(R.id.device_name)
        deviceSetLayout = view?.findViewById(R.id.deviceSetLayout)
        add_device = view?.findViewById(R.id.add_device)
        reconnect_layout = view?.findViewById(R.id.reconnect_layout)
        binding.addDevice.setOnClickListener(this)
        binding.reconnectLayout.setOnClickListener(this)
        binding.reconnect.setOnClickListener(this)
        binding.deviceSetDialView.setOnClickListener(this)

        deviceDeviceTv?.setOnClickListener {
            startActivity(Intent(activity,QrcodeDialActivity::class.java))

        }


        bleManager = requireActivity().getSystemService(BLUETOOTH_SERVICE) as BluetoothManager

        activity?.let {
//            ActivityCompat.requestPermissions(
//                it,
//                arrayOf(
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.BLUETOOTH_SCAN
//                ),
//                0x00
//            )
        }

        deviceSetView = view?.findViewById(R.id.deviceSetView)
        deviceSetView?.setOnSetListener { position ->
            //????????????
            if (position == 0x00) {
                TimeBoatApplication.timeBoatApplication.getBleOperate()?.findTimeBoatDevice()
            }

            //????????????
            if (position == 0x01) {
                startActivity(Intent(activity, BleCallActivity::class.java))
            }
            //??????
            if (position == 0x02) {
                if (activity?.let {
                        XXPermissions.isGranted(
                            it,
                            arrayOf(
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        )
                    } == true) {
                    startActivity(Intent(activity, CamaraActivity::class.java))
                    return@setOnSetListener
                }
                XXPermissions.with(this).permission(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ).request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                        if (all) {
                            startActivity(Intent(activity, CamaraActivity::class.java))
                        }
                    }
                })

            }

            //????????????
            if (position == 0x03) {
                startActivity(Intent(activity, DeviceUnitSetActivity::class.java))
            }
            //????????????
            if (position == 0x05) {
                startActivity(Intent(activity, AboutDeviceActivity::class.java))
            }

            //??????
            if(position == 0x06){
               startActivity(Intent(activity,AlarmListActivity::class.java))
            }
            //??????
            if(position == 0x07){
                startToActivity(0)
            }
            //??????
            if(position == 0x08){
                startToActivity(1)
            }
            //??????
            if(position == 0x09){
                startToActivity(2)
            }
        }

    }


    private fun startToActivity(code : Int){
        val intent = Intent(activity,CommRemindActivity::class.java)
        intent.putExtra("code",code)
        startActivity(intent)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            checkBle()
            updateUi()
        }
    }

    override fun initDataFromThread() {
        SettingInfoManager.instance.getBleModel()
    }

    override fun threadDataInit() {
        super.threadDataInit()
        updateUi()
    }

    private fun updateUi() {

//        val bleInfo = SettingInfoManager.instance.getBleModel()
//        if(bleInfo!=null){
//            var macAddrress =bleInfo.mac
//            if(macAddrress!=null&&macAddrress.isNotEmpty()){
//                binding.reconnectLayout.isVisible =true
//                binding.addDevice.isGone=true
//                binding.deviceName.text= bleInfo.name
//                binding.deviceImei.text =bleInfo.imei
//                var status =  BleServiceManager.instance.getBleConnectStatus()
//                if(status!=null){
//                    when(status){
//                        RxBleConnection.RxBleConnectionState.CONNECTING -> updateConnectStatus("????????????" ,true)
//                        RxBleConnection.RxBleConnectionState.CONNECTED ->  updateConnectStatus("????????????" ,false)
//                        RxBleConnection.RxBleConnectionState.DISCONNECTED ->   updateConnectStatus("??????" ,true)
//                        RxBleConnection.RxBleConnectionState.DISCONNECTING -> updateConnectStatus("??????" ,true)
//
////                        isInit-> {
////                            updateConnectStatus("??????" ,true)
////                        }
////                        connectFailed ->{
////                            updateConnectStatus("????????????" ,true)
////                        }
////                        isConnectSuccess->{
////                            updateConnectStatus("????????????" ,false)
////                        }
////                        isConnecting-> {
////                            updateConnectStatus("????????????" ,true)
////                        }
////                        autoDisconnect->{
////                            updateConnectStatus("????????????" ,false)
////                        }
//                    }
//                }else{
//                    updateConnectStatus("??????" ,true)
//                }
//
//            }else{
//                binding.addDevice.isVisible=true
//                binding.reconnectLayout.isGone =true
//            }
//        }else{
//            binding.addDevice.isVisible=true
//            binding.reconnectLayout.isGone =true
//        }
    }

    override fun initData() {
        devicePermissionListen()
        deviceStatusListen()

        addDeviceListen()

        backGroundScanListen()
    }


    private fun devicePermissionListen() {

        deviceViewModel.devicePermissionData.observe(
            this@DeviceFragment,
            androidx.lifecycle.Observer {
                Log.d(TAG, "devicePermissionListen it=" + it.what)
                when (it.what) {
                    bleStatusPermission -> {
                        when (it.obj as RxBleClient.State) {
                            RxBleClient.State.READY -> {

                            }
                            RxBleClient.State.BLUETOOTH_NOT_AVAILABLE,
                            RxBleClient.State.BLUETOOTH_NOT_ENABLED,
                            RxBleClient.State.LOCATION_PERMISSION_NOT_GRANTED,
                            RxBleClient.State.LOCATION_SERVICES_NOT_ENABLED -> {
                                checkBle()
                            }

                        }
                    }

                }
            })


    }

    private fun deviceStatusListen() {
        deviceViewModel.deviceLiveData.observe(this@DeviceFragment, androidx.lifecycle.Observer {
            Log.d(TAG, "connectDiscovery it=" + it.what)
            when (it.what) {
                connectDiscovery -> {
                    if (it.obj is Int) {
                        deviceStatus = it.obj as Int
                        LogUtil.d(TAG, "connectDiscovery deviceStatus=" + deviceStatus)
                    }
                    when (it.obj) {
                        connectFailed -> {
//                            dismissLoadingDialog()
                            refreshReConnect()

                        }
                        isConnectSuccess -> {
                            UToast.showShortToast("????????????")
                            updateConnectStatus("????????????", false)
                            if (it.describe is RxBleDevice) {
                                val bleDevice = it.describe as RxBleDevice

                                deviceViewModel.addDevice("", bleDevice.macAddress, bleDevice.name)
                            }
//                            dismissLoadingDialog()
                        }
                        isConnecting -> {
                            updateConnectStatus("?????????", false)
                            //     UToast.showShortToast("???????????????")
                            // reloadingMessage("???????????????")

                        }
                        ScanBleViewModel.blePermission -> {
                            activity?.requestConnectionPermission(rxBleClient)

                        }
                        autoDisconnect -> {
                            //??????????????????
                            if (openAutoConnect) {
                                val repeatTimes = it.describe as Int
                                if (repeatTimes > 5) {
                                    if (BleServiceManager.instance.canConnect() && isOpenBluetooth()) {
                                        LogUtil.d(TAG, "devices background connect")

                                        backgroundScan(BleServiceManager.instance.getMacAddress())

                                        if (BleServiceManager.instance.canConnect()) {
                                            updateConnectStatus("????????????", false)
                                        }
                                    } else {
                                        LogUtil.d(TAG, "devices discovery connect")
                                        deviceStatus = isInit
                                        refreshReConnect()
                                    }
                                } else {
                                    if (isOpenBluetooth()) {
                                        delayReconnect(3000)
                                        if (BleServiceManager.instance.canConnect()) {
                                            updateConnectStatus("????????????", false)
                                        }
                                    } else {
                                        deviceStatus = isInit
                                        checkBle()
                                        refreshReConnect()
                                    }
                                }
                            } else {
                                deviceStatus = isInit
                                refreshReConnect()
                            }

                        }
                    }
                }
                bleStatusPermission -> {
                    when (it.obj as RxBleClient.State) {
                        RxBleClient.State.READY -> {

                        }
                        RxBleClient.State.BLUETOOTH_NOT_AVAILABLE,
                        RxBleClient.State.BLUETOOTH_NOT_ENABLED,
                        RxBleClient.State.LOCATION_PERMISSION_NOT_GRANTED,
                        RxBleClient.State.LOCATION_SERVICES_NOT_ENABLED -> {
                            checkBle()
                        }

                    }
                }

            }
        })

        deviceViewModel.initData()
    }

    private fun refreshReConnect() {
//        if(SettingInfoManager.instance.macAddress!=null && SettingInfoManager.instance.macAddress!!.isNotEmpty()){
//            if(BleServiceManager.instance.canConnect()){
//                updateConnectStatus("??????" ,true)
//                UToast.showShortToast("????????????")
//            }
//        }else{
//            binding.addDevice.isVisible=true
//            binding.reconnectLayout.isGone =true
//            UToast.showShortToast("????????????????????????")
//        }

    }


    private fun delayReconnect(time: Long) {
        LogUtil.d(TAG, "delayReconnect")
        val t = Timer()
        t.schedule(object : TimerTask() {
            override fun run() {
                LogUtil.d(TAG, "delayReconnect ")

                BleServiceManager.instance.reconnect()


                t.cancel()
            }
        }, time)
    }


    private fun addDeviceListen() {
        deviceViewModel.addDeviceData.vmObserver(this) {
            onAppLoading = {

            }
            onAppSuccess = {
                LogUtil.d(TAG, "devices imei=" + it?.imei + ",mac=" + it?.mac)
                if (it != null) {
                    binding.deviceName.text = it.name
                    binding.deviceImei.text = it.imei
                }
            }
            onAppComplete = {

            }
            onAppError = { msg, errorCode ->
                LogUtil.d(TAG, "devices error=" + msg)
                if (errorCode == REPOSITORY_RESULT_TOKEN_INVALID) {
                    quiteLogin()
                    if (activity is MainActivity) {
                        (activity as MainActivity).getFrgManager()?.showMainFragment()
                    }
                    UToast.showShortToast("???????????????")
                } else {
                    UToast.showShortToast(msg)
                }

            }
        }
    }

    private fun quiteLogin() {
        SettingInfoManager.instance.cleanUserInfo()
        SettingInfoManager.instance.cleanBleModel()
        //??????????????????
        BleServiceManager.instance.disconnectAndClean()
    }

    //     private val scanList : Set<ScanResult> = HashSet<ScanResult>()
    private fun backGroundScanListen() {

        deviceViewModel.liveData.observe(this, androidx.lifecycle.Observer {


            when (it.what) {
                ScanBleViewModel.scanBlePermission -> {
                    activity?.requestScanPermission(it.obj as RxBleClient)
                }

                ScanBleViewModel.scanBleDevicesSuccess -> {

                    if (it.obj is ScanResult) {
                        BleServiceManager.instance.setBleDevice((it.obj as ScanResult).bleDevice.macAddress)
//                        hasBleDevice =true
                        updateConnectStatus("?????????", false)
//                        reloadingMessage("???????????????")
                    }

                    deviceViewModel.onPause()

                }
                scanBleDevicesError -> {
                    if (it.obj is BleScanException) activity?.showError(it.obj as BleScanException)
//                    dismissLoadingDialog()
                }
                ScanBleViewModel.startScanBleDevice -> {
//                    hasBleDevice =false
//                    showLoadingDialog("??????????????????")
                    updateConnectStatus("?????????", false)
                    LogUtil.d(TAG, "updateScanState startScan")
                }

                ScanBleViewModel.disposeScanBleDevices -> {
//                    if(!hasBleDevice){
//                        dismissLoadingDialog()
//                    }

                }
            }
        })
    }

    //??????mac????????????
    private fun backgroundScan(mac: String) {
        deviceViewModel.onBackGroundToggleClick(mac)
    }


    private fun updateConnectStatus(status: String, clickEnable: Boolean) {
//        binding.reconnectLayout.isVisible =true
//        binding.addDevice.isGone=true
//        binding.reconnect.text = status
//        binding.reconnect.isEnabled =clickEnable
    }

    private fun isOpenBluetooth(): Boolean {
        val adapter = bleManager.adapter ?: return false
        return adapter.isEnabled
    }

    //??????????????????
    val enableBluetooth =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == Activity.RESULT_OK) {
                showMsg(if (isOpenBluetooth()) "???????????????" else "???????????????")
            }
        }

    //??????BLUETOOTH_CONNECT????????????
    val requestBluetoothConnect =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        {
            if (it) {
                //????????????
                enableBluetooth.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            } else {
                showMsg("Android12????????????????????????????????????????????????")
            }
        }

    private fun isAndroid12() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    private fun hasPermission(permission: String) =
        checkSelfPermission(requireActivity(), permission) == PackageManager.PERMISSION_GRANTED

    private fun showMsg(msg: String) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
    }

    fun checkBle() {

        if (isOpenBluetooth()) {
//            showMsg( "???????????????")
            return
        }

        if (isAndroid12()) {
            if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                enableBluetooth.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))

            } else {
                requestBluetoothConnect.launch(Manifest.permission.BLUETOOTH_CONNECT)
            }
        } else {
            enableBluetooth.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }

    }

    //????????????
    val register =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                LogUtil.d(TAG, "del devices")
                if (data != null) {
                    val status = data.getIntExtra(DEL_DEVICES_STATUS, 1)
                    if (status == DEL_SUCCESS) {
                        if (BleServiceManager.instance.isBleConnect()) {
                            BleServiceManager.instance.disconnectAndClean()
                        }

                        updateUi()
                    }
                }

            }
        }


    override fun onResume() {
        super.onResume()

        isConnStatus()
        //reconnect_layout
    }

    //??????????????????????????????
    private fun isConnStatus() {
        val bleMac = MmkvUtils.getConnDeviceMac()
        val isEmpty = BikeUtils.isEmpty(bleMac)

        reconnect_layout?.visibility = if (isEmpty) View.GONE else View.VISIBLE
        add_device?.visibility = if (isEmpty) View.VISIBLE else View.GONE
        deviceSetLayout?.visibility = if (isEmpty) View.GONE else View.VISIBLE

        if (!isEmpty) {
            val name = MmkvUtils.getConnDeviceName()
            device_name?.text = name
            device_imei?.text = bleMac

//            if(name.toLowerCase(Locale.ROOT).contains("a15")){
//                binding.deviceSetDialView.visibility = View.GONE
//            }else{
//                binding.deviceSetDialView.visibility = View.VISIBLE
//            }

        }

        //???????????????
        val isConn = TimeBoatApplication.timeBoatApplication.connStatus == ConnStatus.CONNECTED

        LogUtil.e(TAG, "-------?????????mac=" + bleMac + " " + isConn)
        reconnect?.visibility = if (isConn) View.GONE else View.VISIBLE



        //A159????????????

    }


    fun onClickEvent(v: View) {
        when (v.id) {
            R.id.add_device -> {
//                val intent = Intent(activity, ScanActivity::class.java)

                val intent = Intent(activity, NewScanDeviceActivity::class.java)
                requireActivity().startActivity(intent)
            }

            R.id.reconnect -> {
                reconnect?.text = "?????????.."
                (activity as MainActivity).verifyScanFun()
                //checkBle()

//                if(isOpenBluetooth()){
//                    if((SettingInfoManager.instance.macAddress!=null&&SettingInfoManager.instance.macAddress!!.isNotEmpty())){
//                        backgroundScan(SettingInfoManager.instance.macAddress!!);
//                    }
//                }else{
//                    checkBle()
//                }
//                val intent = Intent(activity, ScanActivity::class.java)
//                intent.putExtra(Constants.MAC_Address,SettingInfoManager.instance.macAddress)
//                requireActivity().startActivity(intent)
            }

            R.id.reconnect_layout -> {
                register.launch(Intent(activity, DelDeviceActivity::class.java))
            }

            //????????????
            R.id.deviceSetDialView -> {
                startActivity(Intent(activity, DialActivity::class.java))
            }
        }
    }

    override fun widgetClick(v: View) {
        super.widgetClick(v)
        onClickEvent(v)
//        startActivity(Intent(activity,NewScanDeviceActivity::class.java))
//        if (SettingInfoManager.instance.isAccessTokenNotEmpty()) {
//            if (SettingInfoManager.instance.isPhoneEmpty()) {
//                (activity as MainActivity).showBinding()
//            } else {
//                onClickEvent(v)
//            }
//        } else {
//            (activity as MainActivity).showLogin()
//        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (isScanPermissionGranted(requestCode, grantResults)) {
            if (SettingInfoManager.instance.macAddress != null && SettingInfoManager.instance.macAddress!!.isNotEmpty()) {
                backgroundScan(SettingInfoManager.instance.macAddress!!)
            }
        } else if (isScanPermissionNotGranted(requestCode, grantResults)) {
            LogUtil.d(TAG, "onRequestPermissionsResult not allow")
            checkLocation()
        }

        if (isConnectionPermissionGranted(requestCode, grantResults)) {
            // BleServiceManager.instance.connectMac()
        }
    }


    fun checkLocation() {
        if (!isLocationEnabled()) {
            enableLocation.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    //????????????
    val enableLocation =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == Activity.RESULT_OK) {
                UToast.showShortToast(if (isLocationEnabled()) "???????????????" else "???????????????")
            }
        }

    fun isLocationEnabled(): Boolean {
        var enabled = false
        try {
            val locationManager =
                activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            enabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                locationManager.isLocationEnabled
            } else {
                Settings.Secure.getInt(
                    activity?.contentResolver,
                    Settings.Secure.LOCATION_MODE
                ) != Settings.Secure.LOCATION_MODE_OFF
            }
        } catch (e: Settings.SettingNotFoundException) {
            Log.e(TAG, "Cannot check GPS status: " + e.message, e)
        }
        return enabled


    }


    override fun onDestroyView() {
        super.onDestroyView()
        try {
            activity?.unregisterReceiver(broadcastReceiver)
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    private val broadcastReceiver : BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
           val action = p1?.action
            if(action == BleConstant.BLE_CONNECTED_ACTION){
                isConnStatus()
            }
            if(action == BleConstant.BLE_DIS_CONNECT_ACTION){
                isConnStatus()
            }
        }

    }
}
