package net.sgztech.timeboat.managerUtlis

import android.annotation.SuppressLint
import android.os.ConditionVariable
import com.imlaidian.utilslibrary.utils.LogUtil
import net.sgztech.timeboat.util.toHex
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.sgztech.timeboat.bleCommand.CommandType
import net.sgztech.timeboat.bleCommand.DeviceInfoDataModel
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * Created by Jun on 2022/7/27 12:04 上午.
 */
class BLESendCommandManager() {
    companion object {
        private const val TAG: String = "BLESendCommandManager"

        val instance: BLESendCommandManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            BLESendCommandManager()
        }
    }

    private val COMMAND_LIST: ArrayList<ByteArray> = ArrayList()
    private var mThread: Thread? = null
    private var mSyncTimeTimer: Timer? = null
    private var mLocationTimer: Timer? = null
    private var mTotalStepTimer: Timer? = null
    private val mLock: ConditionVariable = ConditionVariable()
    private var mLastSyncTimestamp: Long = 0L

    init {
        createThread()
        createSyncTimer()
    }

    private fun sendCommand(cmd: ByteArray) {
        synchronized(COMMAND_LIST) {
            COMMAND_LIST.add(cmd)
            LogUtil.d(TAG, "command list size = " + COMMAND_LIST.size)
            releaseLock()
        }
    }

    private fun popCommandByteArray(): ByteArray? {
        var cmd: ByteArray? = null

        synchronized(COMMAND_LIST) {
            if (COMMAND_LIST.isNotEmpty()) {
                cmd = COMMAND_LIST[0];
                COMMAND_LIST.removeAt(0);
            }
        }

        return cmd
    }

    private fun waitLock() {
        mLock.block()
    }

    private fun releaseLock() {
        mLock.open()
    }

    inner class SyncTimerTask : TimerTask() {
        override fun run() {
            sendSyncTimeCMD()
        }
    }

    inner class LocationTask : TimerTask() {
        override fun run() {
            sendLocationCMD()
        }
    }

    inner class TotalStepTask : TimerTask() {
        override fun run() {
            sendTotalStepCMD()
        }
    }

    private fun createSyncTimer() {
        if (null == mSyncTimeTimer) {
            LogUtil.d(TAG, "create sync timer")

            mSyncTimeTimer = Timer()
            mSyncTimeTimer!!.schedule(SyncTimerTask(), 60*60*1000, 60*60*1000)
        }

        if (null == mLocationTimer) {
            LogUtil.d(TAG, "create location timer")

            mLocationTimer = Timer()
            mLocationTimer!!.schedule(LocationTask(), 60*1000, 30*60*1000)
        }

        if (null == mTotalStepTimer) {
            LogUtil.d(TAG, "create total step timer")
            mTotalStepTimer = Timer()
            mTotalStepTimer!!.schedule(TotalStepTask(), 1000, 60*1000)
        }
    }

    private fun createThread() {
        if (null == mThread) {
            LogUtil.d(TAG, "create send command thread")

            mThread = Thread {
                var cmd: ByteArray? = null

                while (true) {
                    cmd = popCommandByteArray();
                    if (null != cmd) {
                        BleServiceManager.instance.writeData(cmd)
                    } else {
                        waitLock()
                    }
                }
            }

            mThread!!.start()
        }
    }

    fun sendSyncTimeCMD() {
        val curTimestamp = System.currentTimeMillis()
        if (curTimestamp - mLastSyncTimestamp > (30 * 1000)) {
            mLastSyncTimestamp = curTimestamp

            LogUtil.d(TAG, "send sync time cmd")
            val cmd = CommandType.buildSyncTimeCommand()
            sendCommand(cmd)
        } else {
            LogUtil.d(TAG, "send sync time, less 30 second")
        }
    }

    fun sendLocationCMD() {
        val lng = LocationAddressManager.instance.longitude
        val lat = LocationAddressManager.instance.latitude

        LogUtil.d(TAG, "send coordinate cmd, lng = $lng, lat = $lat")

        if (lng.isNotEmpty() && lat.isNotEmpty()) {
            TCPReportDataManager.getInstance().sendCoordinateCommand(
                System.currentTimeMillis() / 1000, lng, lat
            )
        }
    }

    fun sendTotalStepCMD() {
        LogUtil.d(TAG, "send total step cmd")

        val cmd = CommandType.buildTotalStepCMDHeader()
        sendCommand(cmd)
    }

    @SuppressLint("CheckResult")
    fun sendSyncTimeCMDWithDelay() {
        LogUtil.d(TAG, "send sync time cmd with delay")
        Observable.timer(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                sendSyncTimeCMD()
            }
    }

    fun parseDeviceInfoCMD(deviceInfo: DeviceInfoDataModel?) {
        if (null != deviceInfo) {
            val data: DeviceInfoDataModel = deviceInfo

            if (data.stepCount > 0) {
                LogUtil.d(TAG, "need read step count data")
                sendCommand(CommandType.buildStepCountCMDHeader())
            }

            if (data.heartRate > 0) {
                LogUtil.d(TAG, "need read heart rate data ")
                sendCommand(CommandType.buildHeartRateCMDHeader())
            }

            if (data.sleep > 0) {
                LogUtil.d(TAG, "need read sleep data ")
                sendCommand(CommandType.buildSleepCMDHeader())
            }

            if (data.run > 0) {
                LogUtil.d(TAG, "need read running data ")
                sendCommand(CommandType.buildRunningCMDHeader())
            }

            if (data.hiking > 0) {
                LogUtil.d(TAG, "need read walk data ")
                sendCommand(CommandType.buildHikingCMDHeader())
            }

            if (data.marathon > 0) {
                LogUtil.d(TAG, "need read marathon data ")
                sendCommand(CommandType.buildMarathonCMDHeader())
            }

            if (data.skippingRope > 0) {
                LogUtil.d(TAG, "need read skippingRope data ")
                sendCommand(CommandType.buildSkippingRopeCMDHeader())
            }

            if (data.swim > 0) {
                LogUtil.d(TAG, "need read swimming data ")
                sendCommand(CommandType.buildSwimmingCMDHeader())
            }

            if (data.rockClimbing > 0) {
                LogUtil.d(TAG, "need read rockClimbing data ")
                sendCommand(CommandType.buildRockClimbingCMDHeader())
            }

            if (data.skiing > 0) {
                LogUtil.d(TAG, "need read skiing data ")
                sendCommand(CommandType.buildSkiingCMDHeader())
            }

            if (data.ride > 0) {
                LogUtil.d(TAG, "need ride data ")
                sendCommand(CommandType.buildRideCMDHeader())
            }

            if (data.boat > 0) {
                LogUtil.d(TAG, "need boat data ")
                sendCommand(CommandType.buildBoatCMDHeader())
            }

            if (data.bungeeJumping > 0) {
                LogUtil.d(TAG, "need bungeeJumping data ")
                sendCommand(CommandType.buildBungeeJumpingCMDHeader())
            }

            if (data.mountaineering > 0) {
                LogUtil.d(TAG, "need mountaineering data ")
                sendCommand(CommandType.buildMountaineeringCMDHeader())
            }

            if (data.parachuteJump > 0) {
                LogUtil.d(TAG, "need parachuteJump data ")
                sendCommand(CommandType.buildParachuteJumpCMDHeader())
            }

            if (data.golf > 0) {
                LogUtil.d(TAG, "need golf data ")
                sendCommand(CommandType.buildGolfCMDHeader())
            }

            if (data.surfing > 0) {
                LogUtil.d(TAG, "need surfing data ")
                sendCommand(CommandType.buildSurfingCMDHeader())
            }

            if (data.runningMachine > 0) {
                LogUtil.d(TAG, "need runTraining data ")
                sendCommand(CommandType.buildRunTrainingCMDHeader())
            }

            if (data.measureHeartRate > 0) {
                LogUtil.d(TAG, "need measureHeartRate data ")
                sendCommand(CommandType.buildMeasureHeartRateCMDHeader())
            }

            if (data.bleAddress?.isNotEmpty() == true) {
                LogUtil.d(TAG, "Bluetooth Address = " + (data.bleAddress?.toHex() ?: "null"))
            }

            if (data.badminton > 0) {
                LogUtil.d(TAG, "need badminton data ")
                sendCommand(CommandType.buildBadmintonCMDHeader())
            }

            if (data.basketball > 0) {
                LogUtil.d(TAG, "need basketball data ")
                sendCommand(CommandType.buildBasketballCMDHeader())
            }

            if (data.footBall > 0) {
                LogUtil.d(TAG, "need footBall data ")
                sendCommand(CommandType.buildFootballCMDHeader())
            }

            if (data.bloodOxygen > 0) {
                LogUtil.d(TAG, "need bloodOxygen data ")
                sendCommand(CommandType.buildBloodOxygenCMDHeader())
            }
        }
    }
}