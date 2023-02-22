package net.sgztech.timeboat


import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.blala.blalable.BleApplication
import com.blala.blalable.BleOperateManager
import com.device.rxble.LogConstants
import com.device.rxble.LogOptions
import com.device.rxble.RxBleClient
import com.imlaidian.utilslibrary.UtilsApplication
import com.tencent.mmkv.MMKV
import net.sgztech.timeboat.ui.newui.ConnStatus
import net.sgztech.timeboat.ui.newui.ConnStatusService
import net.sgztech.timeboat.ui.utils.CrashHandler
import net.sgztech.timeboat.ui.utils.MmkvUtils
import org.litepal.LitePal
import org.litepal.LitePalApplication

open class TimeBoatApplication : UtilsApplication() {



    /**连接状态枚举 */
     var connStatus: ConnStatus = ConnStatus.NOT_CONNECTED


    companion object {
        lateinit var rxBleClient: RxBleClient
            private set

        lateinit var timeBoatApplication : TimeBoatApplication private set

        lateinit var connStatusService : ConnStatusService private set
    }

    override fun onCreate() {
        super.onCreate()
        timeBoatApplication = this
        LitePal.initialize(this)
        MMKV.initialize(this)
        MmkvUtils.initMkv()
        BleApplication.initBleApplication(this)
        rxBleClient = RxBleClient.create(this)
        RxBleClient.updateLogOptions(LogOptions.Builder()
                .setLogLevel(LogConstants.INFO)
                .setMacAddressLogSetting(LogConstants.MAC_ADDRESS_FULL)
                .setUuidsLogSetting(LogConstants.UUIDS_FULL)
                .setShouldLogAttributeValues(true)
                .build()
        )
       CrashHandler.create(this)
        bindService()

    }


    private fun bindService(){
        val intent = Intent(this, ConnStatusService::class.java)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)

    }

    open fun getInstance(): TimeBoatApplication? {
        return timeBoatApplication
    }


    fun getBleOperate(): BleOperateManager? {
        return BleOperateManager.getInstance()
    }

    open fun getConnStatusService(): ConnStatusService? {
        return connStatusService
    }


    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            try {
                connStatusService =
                    (iBinder as ConnStatusService.ConnBinder).service
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {

        }


        //获取连接状态
        fun getConnStatus(): ConnStatus? {
            return connStatus
        }

        //设置连接状态
        fun setConnStatus(connStatus: ConnStatus) {
           this@TimeBoatApplication.connStatus = connStatus
        }
    }
}
