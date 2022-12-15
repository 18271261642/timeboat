package net.sgztech.timeboat


import com.device.rxble.LogConstants
import com.device.rxble.LogOptions
import com.device.rxble.RxBleClient
import com.imlaidian.utilslibrary.UtilsApplication
import com.imlaidian.utilslibrary.utils.LogUtil
import net.sgztech.timeboat.ui.utils.CrashHandler

class TimeBoatApplication : UtilsApplication() {

    companion object {
        lateinit var rxBleClient: RxBleClient
            private set
    }

    override fun onCreate() {
        super.onCreate()
        rxBleClient = RxBleClient.create(this)
        RxBleClient.updateLogOptions(LogOptions.Builder()
                .setLogLevel(LogConstants.INFO)
                .setMacAddressLogSetting(LogConstants.MAC_ADDRESS_FULL)
                .setUuidsLogSetting(LogConstants.UUIDS_FULL)
                .setShouldLogAttributeValues(true)
                .build()
        )
       CrashHandler.create(this)
    }
}
