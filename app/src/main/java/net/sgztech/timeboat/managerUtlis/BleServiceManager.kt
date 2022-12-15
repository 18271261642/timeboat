package net.sgztech.timeboat.managerUtlis

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.os.ParcelUuid
import com.device.rxble.RxBleClient
import com.device.rxble.RxBleConnection
import com.device.rxble.RxBleDevice
import com.device.rxble.RxBleDeviceServices
import com.imlaidian.utilslibrary.utils.LogUtil
import com.jakewharton.rx.ReplayingShare
import net.sgztech.timeboat.util.isConnected
import net.sgztech.timeboat.util.toHex
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.PublishSubject
import net.sgztech.timeboat.TimeBoatApplication
import net.sgztech.timeboat.bleCommand.BLECMDParseUtil
import net.sgztech.timeboat.bleCommand.CommandType
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.Constants.Companion.Notification_Char
import net.sgztech.timeboat.config.Constants.Companion.Notification_Char_UUID
import net.sgztech.timeboat.config.Constants.Companion.Read_Char
import net.sgztech.timeboat.config.Constants.Companion.Read_Char_UUID
import net.sgztech.timeboat.config.Constants.Companion.Write_Char
import net.sgztech.timeboat.config.Constants.Companion.Write_Char_UUID
import net.sgztech.timeboat.netty.MachineNettyClient
import net.sgztech.timeboat.provide.dataModel.BleConnectEvent
import net.sgztech.timeboat.provide.viewModel.ScanBleViewModel
import org.greenrobot.eventbus.EventBus
import java.util.*
import java.util.concurrent.TimeUnit


class BleServiceManager() {
    private lateinit var mBLEDevice: RxBleDevice
    private var mMacAddress: String = ""
    private lateinit var mConnectionObservable: Observable<RxBleConnection>
    private val mDisconnectCharTriggerSubject = PublishSubject.create<Unit>()
    private val mConnectionCharDisposable = CompositeDisposable()
    private var mWriteDisposable: Disposable? = null
    private var mUIListen: UpdateUIListen? = null
    private var mNotifyListen: NotifyInfoListen? = null
    private var mCharacterMap = HashMap<String, BluetoothGattCharacteristic>()
    var mServiceMap = HashMap<UUID, List<UUID>>()
    private  var reconnectTimes = 0
    companion object {
        private const val TAG: String = "BleServiceManager"

        val instance: BleServiceManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            BleServiceManager()
        }
    }



    fun getMacAddress(): String {
        return mMacAddress
    }

    fun isBleConnect():Boolean{
        return if(canConnect()){
            mBLEDevice.isConnected
        }else{
            false
        }
    }

    fun getBleConnectStatus() :RxBleConnection.RxBleConnectionState?{
        return if(canConnect()){
           return mBLEDevice.connectionState
        }else{
            return null
        }

    }

    fun reconnect() {
        if(canConnect()){
            connectAndCommunication(mMacAddress)
        }
    }

    fun canConnect():Boolean{
        return mMacAddress.isNotEmpty()
    }

    fun setBleDevice(mac: String) {


        if (TimeBoatApplication.rxBleClient.isConnectRuntimePermissionGranted) {
            mBLEDevice = TimeBoatApplication.rxBleClient.getBleDevice(mac)
            connectAndCommunication(mac)
            MachineNettyClient.instance.connect()
            TCPReportDataManager.getInstance().init()
        } else {
            //此处不能使用mBLEDevice
            mMacAddress = mac
            listenerHandle(ScanBleViewModel.connectDiscovery, ScanBleViewModel.blePermission,mac)
        }
    }

    fun connectMac(){
        if(canConnect()){
            LogUtil.d(TAG, "connectMac mac="+ mMacAddress)
            setBleDevice(mMacAddress)
        }else{
            LogUtil.d(TAG, "connectMac is empty")
        }

    }


    private fun prepareConnectionObservable(): Observable<RxBleConnection> =
        mBLEDevice.establishConnection(false).takeUntil(mDisconnectCharTriggerSubject).compose(ReplayingShare.instance())

    private fun connectAndCommunication(mac: String) {
        mConnectionObservable = prepareConnectionObservable()
        LogUtil.d(TAG, "connectAndCommunication ：" + mBLEDevice.connectionState)
        if (mBLEDevice.isConnected) {
            triggerCharDisconnect()
        } else {
            LogUtil.d(TAG, "connectAndCommunication reconnect")
            mMacAddress = mac
            connectService()
        }
    }

    fun triggerCharDisconnect() = mDisconnectCharTriggerSubject.onNext(Unit)

    @SuppressLint("CheckResult")
    fun connectService() {
        LogUtil.d(TAG, "connectService")
        updateUI(ScanBleViewModel.connectDiscovery, ScanBleViewModel.isConnecting)
        mConnectionObservable
            .flatMapSingle { it ->

                it.discoverServices().map {
                    getGattServices(it)
                }.doOnSuccess {
                    LogUtil.d(TAG, "on discovery success, state = " + mBLEDevice.connectionState)
                }.doOnError {
                    LogUtil.d(TAG, "on discovery error, state = " + mBLEDevice.connectionState)
                    connectError("connection error: $it")
                }.doFinally {
                    connectNoticeCharacteristicUuid()
                    connectWriteCharacteristicUuid()
                    LogUtil.d(TAG, "on discovery finish")
                }
            }.doFinally {
                LogUtil.d(TAG, "connect finally reconnectTimes =" +reconnectTimes)
                reconnectTimes ++
                updateUI(ScanBleViewModel.connectDiscovery, ScanBleViewModel.autoDisconnect ,reconnectTimes)
                //断开重连
                //connectAndCommunication(macAddress);
            }
            .subscribe({
                LogUtil.d(TAG, "on connect success, state = " + mBLEDevice.connectionState)
                onConnectReceived()
            }, { connectError("connection error: $it") })
    }

    private fun onConnectReceived() {
        LogUtil.d(TAG, "onConnectReceived =" + mBLEDevice.connectionState)
        if (mBLEDevice.isConnected) {
            reconnectTimes =0
            updateUI(ScanBleViewModel.connectDiscovery, ScanBleViewModel.isConnectSuccess, mBLEDevice)
            BLESendCommandManager.instance.sendSyncTimeCMDWithDelay()
        } else {
            updateUI(ScanBleViewModel.connectDiscovery, ScanBleViewModel.connectFailed)
        }
    }

    private fun getGattServices(services: RxBleDeviceServices) {
        services.bluetoothGattServices.map { service ->
            val serviceUUID = service.uuid
            val gattServiceUUid = ParcelUuid.fromString(Constants.GATT_Service_UUID).uuid

            if (serviceUUID.equals(gattServiceUUid)) {
                service.characteristics.map { characteristic ->
                    if (characteristic.uuid.toString().contains(Write_Char_UUID)) {
                        LogUtil.d(TAG, "write char uuid = " + characteristic.uuid)

                        mCharacterMap[Write_Char] = characteristic
                    } else if (characteristic.uuid.toString().contains(Read_Char_UUID)) {
                        LogUtil.d(TAG, "read char uuid = " + characteristic.uuid)

                        mCharacterMap[Read_Char] = characteristic
                    } else if (characteristic.uuid.toString().contains(Notification_Char_UUID)) {
                        LogUtil.d(TAG, "notification char uuid= " + characteristic.uuid)

                        mCharacterMap[Notification_Char] = characteristic
                    }

                    characteristic.uuid
                }.let { mServiceMap.put(serviceUUID, it) }
            }

            mServiceMap
        }.let {

        }
    }



    fun updateUI(what: Int, type: Int) {
        listenerHandle(what, type, "")
    }



    fun updateUI(what: Int, type: Int ,describe: Any?) {
        listenerHandle(what,type ,describe)
        EventBus.getDefault().post(BleConnectEvent(mBLEDevice.connectionState))
    }

    private fun connectWriteCharacteristicUuid() {
        mConnectionObservable
            .flatMapSingle { it.discoverServices() }
            .flatMapSingle { it.getCharacteristic(ParcelUuid.fromString(Constants.Write_Char_UUID).uuid) }
            .observeOn(io())
            .subscribe({
                    characteristic -> {}
//                  updateUI(characteristic)
//                    val commandItem = CommandType.buildSyncTimeCommand()
//                    writeData(characteristic, commandItem)
//                    intervalWrite() // 无数据
//                    intervalSend(characteristic) //定时发送正常

                },
                { onReadFailure(it) }
            )
            .let { mConnectionCharDisposable.add(it) }
    }


    private fun writeData(characteristic: BluetoothGattCharacteristic, inputBytes: ByteArray) {
        if (mBLEDevice.isConnected) {
            mConnectionObservable
                .firstOrError()
                .flatMap {
                    it.writeCharacteristic(characteristic.uuid, inputBytes)
                }
                .observeOn(io())
                .doFinally {
                    mWriteDisposable = null
                }
                .subscribe({ onWriteSuccess() }, { onWriteFailure(it) })
                .let { mWriteDisposable = it }
        }
    }

    fun writeData(inputBytes: ByteArray) {
        LogUtil.d(TAG, "write data byteArray connect = " + mBLEDevice.isConnected)
        if (mBLEDevice.isConnected) {
            LogUtil.d(TAG, "write data byteArray = " + inputBytes.toHex())
            mConnectionObservable
                .firstOrError()
                .flatMap {
                    LogUtil.d(TAG, "write data mtu=" + it.mtu)
                    it.mtu
                    it.writeCharacteristic(
                        ParcelUuid.fromString(Constants.Write_Char_UUID).uuid,
                        inputBytes
                    )
                }
                .observeOn(io())
                .doFinally {
                    mWriteDisposable = null
                    LogUtil.d(TAG, "writeData doFinally =")
                }
                .subscribe({ onWriteSuccess() }, { onWriteFailure(it) })
                .let { mWriteDisposable = it }
        }
    }

    private fun connectNoticeCharacteristicUuid() {
        mConnectionObservable
            .flatMapSingle {
                it.discoverServices()
            }
            .flatMapSingle { it.getCharacteristic(ParcelUuid.fromString(Constants.Notification_Char_UUID).uuid) }
            .observeOn(io())
            .subscribe(
                { characteristic -> notifyEvent(characteristic) },
                { onReadFailure(it) }
            )
            .let { mConnectionCharDisposable.add(it) }
    }

    private fun notifyEvent(characteristic: BluetoothGattCharacteristic) {
        LogUtil.d(TAG, "notify event data byteArray connect = " + mBLEDevice.isConnected)

        if (mBLEDevice.isConnected) {
            mConnectionObservable
                .flatMap { it.setupNotification(characteristic.uuid) }
                .doOnNext { notificationHasBeenSetUp() }
                .flatMap { it }
                .observeOn(io())
                .subscribe(
                    { onNotificationReceived(it) },
                    { onNotificationSetupFailure(it) }
                )
                .let {
                    mConnectionCharDisposable.add(it)
                }
        }
    }

    private fun connectError(error: String) {
        if (mUIListen != null) {
            mUIListen!!.refreshUi(
                ScanBleViewModel.connectDiscovery,
                ScanBleViewModel.connectFailed,
                error
            )
        }
    }

    private fun onReadFailure(throwable: Throwable) {
        if (mUIListen != null) {
            mUIListen!!.refreshUi(ScanBleViewModel.readError, 0, throwable.toString())
        }
    }

    private fun onWriteSuccess() {
        LogUtil.d(TAG, "write Success")
    }

    private fun onWriteFailure(throwable: Throwable) {
        if (mUIListen != null) {
            mUIListen!!.refreshUi(ScanBleViewModel.writeError, 0, throwable.toString())
        }
    }

    private fun onNotificationReceived(bytes: ByteArray) {
        LogUtil.d(TAG, "on notification received cmd = " + bytes.toHex())

        try {
            BLECMDParseUtil.parseCmd(bytes)
            if (mNotifyListen != null) {
                mNotifyListen?.notify(bytes, "")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        synchronized(mParseResponseCommand) {
//            try {
//                mParseResponseCommand.parseRespond(bytes)
//                if (mNotifyListen != null) {
//                    mNotifyListen?.notify(bytes, "")
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
    }

    private fun onNotificationSetupFailure(throwable: Throwable) {
        if (mUIListen != null) {
            mUIListen!!.refreshUi(ScanBleViewModel.notificationError, 0, throwable.toString())
        }
    }

    private fun notificationHasBeenSetUp() {
        LogUtil.d(TAG, "notification set up")
    }

    private fun connectReadCharacteristicUuid() {
        mConnectionObservable
            .flatMapSingle { it.discoverServices() }
            .flatMapSingle { it.getCharacteristic(ParcelUuid.fromString(Constants.Read_Char_UUID).uuid) }
            .observeOn(io())
            .subscribe(
                { characteristic -> readData(characteristic.uuid) },
                { onReadFailure(it) }
            )
            .let {
                mConnectionCharDisposable.add(it)
            }
    }

    fun readData() {
        val readUUid = ParcelUuid.fromString(Constants.Read_Char_UUID).uuid
        LogUtil.d(TAG, "read data byteArray connect = " + mBLEDevice.isConnected)
        readData(readUUid)
    }

    private fun readData(uuid: UUID) {
        LogUtil.d(TAG, "read data byteArray connect= " + mBLEDevice.isConnected)

        if (mBLEDevice.isConnected) {
            mConnectionObservable
                .firstOrError()
                .flatMap {
                    it.readCharacteristic(uuid)
                }
                .subscribeOn(io())
                .subscribe({ bytes -> String(bytes) }, { onReadFailure(it) })
                .let { mConnectionCharDisposable.add(it) }
        } else {
            LogUtil.d(TAG, "readDat disconnect")
        }
    }

    private class ListenerItem(id: Any, listener: UpdateUIListen) {
        var listener: UpdateUIListen
        var id: Any
        init {
            this.listener = listener
            this.id = id
        }
    }

    private val mListenerList = ArrayList<ListenerItem>()

    // 添加监听
    fun addListener(id: Any, listener: UpdateUIListen): Boolean {
        synchronized(mListenerList) {
            mListenerList.add(
                ListenerItem(
                    id,
                    listener
                )
            )

            return true
        }
    }
    // 断开连接且不再连接
    fun disconnectAndClean(){
        if(canConnect()){
            if (mBLEDevice.isConnected) {
                mMacAddress =""
                triggerCharDisconnect()

            }
        }
    }

    /// 发送监听
    fun listenerHandle(what: Int, msg: Any?, describe: Any?) {
        LogUtil.d(TAG , "listenerHandle what" +what)
        synchronized(mListenerList) {
            if (mListenerList.size > 0) {
                for (i in mListenerList.indices) {
                    val item = mListenerList[i]
                    LogUtil.d(TAG , "listen item" +item)
                    item.listener.refreshUi(what, msg, describe)
                }
            }else{
                 LogUtil.d(TAG , "listen size=0")
            }
        }
    }

    /// 移除监听
    fun removeListener(id: Any) {
        removeListener(id, null)
    }

    /// 移除监听
    fun removeListener(id: Any, listener: UpdateUIListen?) {
        synchronized(mListenerList) {
            for (i in mListenerList.indices) {
                val item = mListenerList[i]
                if (item.id == id) {
                    if (null == listener) {
                        mListenerList.removeAt(i)
                    } else if (listener === item.listener) {
                        mListenerList.removeAt(i)
                    }
                }else{
                    LogUtil.d(TAG , "id not same")
                }
            }
        }
    }

    interface UpdateUIListen {
        fun refreshUi(what: Int, msg: Any?, describe: Any?)
    }

    interface NotifyInfoListen {
        fun notify(notify: ByteArray, describe: String)
    }

    fun registerNotify(listen: NotifyInfoListen) {
        mNotifyListen = listen
    }

    fun unRegisterNotify() {
        mNotifyListen = null;
    }

    fun clear() {
        mConnectionCharDisposable.clear()
    }

    private fun setMtu() {
        mConnectionObservable
            .flatMapSingle { rxBleConnection -> rxBleConnection.requestMtu(200) }
            .take(1) // Disconnect automatically after discovery
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                LogUtil.d(TAG, "setMtu finish")
            }
            .subscribe({ LogUtil.d(TAG, "setMtu =" + it) }, { connectError("Connection error: $it") })
            .let { mConnectionCharDisposable.add(it) }
    }

    //定时发送正常
    var index = 0;
    @SuppressLint("CheckResult")
    fun intervalSend(characteristic: BluetoothGattCharacteristic) {
        Observable.interval(3, 10, TimeUnit.SECONDS, Schedulers.trampoline())
            .flatMap { aLong ->
//                val commandItem = SynTimeCommand.synTimeCommandItem()
                val commandItem = CommandType.buildSyncTimeCommand()
                Observable.just(writeData(characteristic, commandItem))
//            if (index % 30 == 0) {
//                index++
//                val commandItem = SynTimeCommand.synTimeCommandItem()
//                Observable.just(writeData(characteristic, commandItem))
//            } else {
//                index++
//                val commandItem2 = CommandItem.readCommandHead((index % 30).toByte(), 0)
//                Observable.just(writeData(characteristic, commandItem2))
//            }
            }.observeOn(io())
            .subscribe({
                onWriteSuccess()
                LogUtil.d(TAG, "intervalSend  disposable size=" + mConnectionCharDisposable.size())
            }, { onWriteFailure(it) }
            )
    }


}