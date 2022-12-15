package net.sgztech.timeboat.bleCommand

import com.imlaidian.utilslibrary.utils.LogUtil
import net.sgztech.timeboat.managerUtlis.BLESendCommandManager
import net.sgztech.timeboat.managerUtlis.TCPReportDataManager
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Jun on 2022/7/26 6:51 下午.
 */
class BLECMDParseUtil private constructor() {
    companion object {
        private const val TAG = "BLECMDParseUtil"
        private var mBufferLen = 0
        private var mDataLen = 0
        private var mBuffer: ByteArray = ByteArray(8*1024*1024)
        private var mHasHeader: Boolean = false
        private var mCmdTypeValid: Boolean = false
        private val PARSE_LOCK: UUID = UUID.randomUUID()

        /**
         * 存在以下几种数据情况：
         * 1. 每次buf是完整的命令: 头 + 类型 + 数据长度 + 时间戳 + 数据内容
         * 2. buf前面有完整的命令，后面无效数据
         * 3. buf前面无效数据，后面有完整命令
         * 4. buf前面有完整命令，后面有下一个命令部分数据
         * 5. buf有多条完整命令
         * 6. buf前面有一条完整命令，中间有无效数据，后面又有多条完整命令
         * 7. 完整命令分成多次buf传入，最终解析成一条命令
         */
        @JvmStatic
        fun parse(buf: ByteArray?): List<BLECMDItem>? {
            if (null == buf) {
                return null
            }

            /**
             * 使用了内部全局变量，避免出现错乱，因此加上同步锁
             */
            synchronized(PARSE_LOCK) {
                return parseBuffer(buf)
            }
        }

        @JvmStatic
        fun parseCmd(buf: ByteArray?) {
            val cmdList = parse(buf)
            if (null != cmdList && cmdList.isNotEmpty()) {
                cmdHandle(cmdList)
            }
        }

        @JvmStatic
        private fun parseBuffer(buf: ByteArray?): List<BLECMDItem>? {
            if (null == buf) {
                return null
            }

            val cmdList: ArrayList<BLECMDItem> = ArrayList()
            var c: Byte = 0
            for (element in buf) {
                c = element
                if (mHasHeader) {
                    // cmd type
                    if (1 == mBufferLen) {
                        // cmd type 1
                        if (CommandType.isCMDTypeValid(c)) {
                            mCmdTypeValid = true
                            mBuffer[mBufferLen] = c
                            mBufferLen++
                        } else {
                            mHasHeader = false
                            mCmdTypeValid = false
                            mBufferLen = 0
                            mDataLen = 0
                        }
                    } else if (2 == mBufferLen || 3 == mBufferLen) {
                        // data len 2/3
                        mBuffer[mBufferLen] = c
                        mBufferLen++
                    } else if (4 == mBufferLen) {
                        // timestamp 4/5/6/7
                        mBuffer[mBufferLen] = c
                        mBufferLen++

                        mDataLen = (mBuffer[2].toInt() and 0xff) or (mBuffer[3].toInt() and 0xff shl 8)
                    } else if (5 == mBufferLen || 6 == mBufferLen || 7 == mBufferLen) {
                        // timestamp 4/5/6/7
                        mBuffer[mBufferLen] = c
                        mBufferLen++
                    } else {
                        if (mDataLen > 0) {
                            mBuffer[mBufferLen] = c
                            mBufferLen++

                            mDataLen--
                        }

                        if (0 == mDataLen) {
                            val header: Byte = mBuffer[0]
                            val type: Byte = mBuffer[1]
                            val len: Int = (mBuffer[2].toInt() and 0xff) or (mBuffer[3].toInt() and 0xff shl 8)
                            val timestampSecond: Long = ((mBuffer[4].toInt() and 0xff) or (mBuffer[5].toInt() and 0xff shl 8) or (mBuffer[6].toInt() and 0xff shl 16) or (mBuffer[7].toInt() and 0xff shl 24)).toLong()
                            var data: ByteArray? = null
                            if (len > 0) {
                                data = mBuffer.copyOfRange(8, mBufferLen)
                            }

                            val cmdItem = BLECMDItem(header, type, len, timestampSecond, data)
                            cmdList.add(cmdItem)

                            mHasHeader = false
                            mBufferLen = 0
                            mCmdTypeValid = false
                            mDataLen = 0
                        }
                    }
                } else {
                    if (CommandType.isHeader(c)) {
                        // header 0
                        mHasHeader = true
                        mBufferLen = 0
                        mBuffer[mBufferLen] = c
                        mBufferLen++
                    } else {
                        mHasHeader = false
                        mBufferLen = 0
                        mCmdTypeValid = false
                        mDataLen = 0
                    }
                }
            }

            return cmdList
        }

        @JvmStatic
        private fun cmdHandle(cmdList: List<BLECMDItem>?) {
            if (null == cmdList) {
                return
            }

            for (cmdItem in cmdList) {
                LogUtil.d(TAG, "cmd item = $cmdItem")

                when (cmdItem.cmdType) {
                    // 计步数据
                    CommandType.STEP_COUNT -> {
                        LogUtil.d(TAG, "step count")
                        val model = StepCountDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle step count model = $model")
                            TCPReportDataManager.getInstance().sendStepCommand(
                                cmdItem.timestampSecond,
                                model.step,
                                model.distance,
                                model.calorie
                            )
                        }
                    }

                    // 心率数据
                    CommandType.HEART_RATE -> {
                        LogUtil.d(TAG, "heart rate")
                        val model = HeartRateDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle heart rate model = $model")
                            TCPReportDataManager.getInstance().sendHeartRateCommand(
                                cmdItem.timestampSecond,
                                model.heartRate
                            )
                        }
                    }

                    // 睡眠监测数据
                    CommandType.SLEEP -> {
                        LogUtil.d(TAG, "sleep")
                        val model = SleepDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle sleep model = $model")
                            for (sleepItem in model.sleepDataList) {
                                TCPReportDataManager.getInstance().sendSleepCommand(
                                    cmdItem.timestampSecond,
                                    sleepItem.hour,
                                    sleepItem.minute,
                                    sleepItem.state
                                )
                            }
                        }
                    }

                    // 跑步运动数据
                    CommandType.RUNNING -> {
                        LogUtil.d(TAG, "running")
                        val model = RunDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle running model = $model")

                            TCPReportDataManager.getInstance().sendRunCommand(
                                cmdItem.timestampSecond,
                                model
                            )
                        }
                    }

                    // 马拉松训练数据
                    CommandType.MARATHON -> {
                        LogUtil.d(TAG, "marathon")
                        val model = RunDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle marathon model = $model")
                        }
                    }

                    // 徒步运动数据
                    CommandType.HIKING -> {
                        LogUtil.d(TAG, "hiking")
                        val model = HikingDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle hiking model = $model")

                            TCPReportDataManager.getInstance().sendHikingCommand(
                                cmdItem.timestampSecond,
                                model
                            )
                        }
                    }

                    // 跳绳运动数据
                    CommandType.SKIPPING_ROPE -> {
                        //略
                    }

                    // 游泳运动数据
                    CommandType.SWIM -> {
                        LogUtil.d(TAG, "swim")
                        val model = SwimDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle swim model = $model")
                        }
                    }

                    // 攀岩运动数据
                    CommandType.ROCK_CLIMBING -> {
                        LogUtil.d(TAG, "rock climbing")
                        val model = RockClimbingDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle climbing model = $model")
                        }
                    }

                    // 滑雪运动数据
                    CommandType.SKIING -> {
                        LogUtil.d(TAG, "skiing")
                        val model = SkiingDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle skiing model = $model")
                        }
                    }

                    // 骑行运动数据
                    CommandType.RIDE -> {
                        LogUtil.d(TAG, "ride")
                        val model = RideDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle ride model = $model")

                            TCPReportDataManager.getInstance().sendRideCommand(
                                cmdItem.timestampSecond,
                                model
                            )
                        }
                    }

                    // 划船运动数据
                    CommandType.BOAT -> {
                        LogUtil.d(TAG, "boat")
                        val model = BoatDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle boat model = $model")
                        }
                    }

                    // 蹦极运动数据
                    CommandType.BUNGEE_JUMPING -> {
                        //略
                    }

                    // 登山运动数据
                    CommandType.MOUNTAINEERING -> {
                        LogUtil.d(TAG, "mountaineering")
                        val model = MountaineeringDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle mountaineering model = $model")

                            TCPReportDataManager.getInstance().sendMountaineeringCommand(
                                cmdItem.timestampSecond,
                                model
                            )
                        }
                    }

                    // 跳伞运动数据
                    CommandType.PARACHUTE_JUMP -> {
                        //略
                    }

                    // 高尔夫运动数据
                    CommandType.GOLF -> {
                        LogUtil.d(TAG, "golf")
                        val model = GolfDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle golf model = $model")
                        }
                    }

                    // 冲浪运动数据
                    CommandType.SURFING -> {
                        LogUtil.d(TAG, "surfing")
                        val model = SurfingDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle surfing model = $model")
                        }
                    }

                    // 气压检测数据
                    CommandType.BAROMETRIC_PRESSURE -> {
                        LogUtil.d(TAG, "barometric pressure")
                        val model = BarometricPressureDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle barometric pressure model = $model")
                        }
                    }

                    // SOS呼救
                    CommandType.SOS -> {
                        LogUtil.d(TAG, "SOS")
                        val model = SOSDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle SOS model = $model")
                        }
                    }

                    // 跑步机训练数据
                    CommandType.RUNNING_MACHINE -> {
                        LogUtil.d(TAG, "running machine")
                        val model = RunningMachineDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle running machine model = $model")

                            TCPReportDataManager.getInstance().sendRunningMachineCommand(
                                cmdItem.timestampSecond,
                                model
                            )
                        }
                    }

                    // 拍照
                    CommandType.PICTURE -> {

                    }

                    // 寻找手机
                    CommandType.FIND_PHONE -> {

                    }

                    // 当前主动测量心率值
                    CommandType.MEASURE_HEART_RATE -> {
                        LogUtil.d(TAG, "measure heart rate")
                        val model = HeartRateDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle measure heart rate model = $model")

                            TCPReportDataManager.getInstance().sendHeartRateCommand(
                                cmdItem.timestampSecond,
                                model.heartRate
                            )
                        }
                    }

                    // 当前主动测量体温值
                    CommandType.MEASURE_BODY_TEMPERATURE -> {
                        LogUtil.d(TAG, "measure body temperature")
                        val model = BodyTemperatureDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle body temperature model = $model")

                            TCPReportDataManager.getInstance().sendTempCommand(
                                cmdItem.timestampSecond,
                                model.bodyTempStr
                            )
                        }
                    }

                    // 总步数
                    CommandType.TOTAL_STEP -> {
                        LogUtil.d(TAG, "total step")
                        val model = TotalStepsDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle total step model = $model")

                            TCPReportDataManager.getInstance().sendTotalStepCommand(
                                cmdItem.timestampSecond,
                                model.totalStep,
                                model.totalDistance,
                                model.totalCalorie
                            )
                        }
                    }

                    // 手表改变配置信息
                    CommandType.CHANGE_CONFIG -> {

                    }

                    // 羽毛球
                    CommandType.BADMINTON -> {
                        LogUtil.d(TAG, "badminton")
                        val model = BallDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle badminton model = $model")

                            TCPReportDataManager.getInstance().sendBadmintonCommand(
                                cmdItem.timestampSecond,
                                model
                            )
                        }
                    }

                    // 篮球
                    CommandType.BASKETBALL -> {
                        LogUtil.d(TAG, "basketball")
                        val model = BallDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle football model = $model")

                            TCPReportDataManager.getInstance().sendBasketballCommand(
                                cmdItem.timestampSecond,
                                model
                            )
                        }
                    }

                    // 足球
                    CommandType.FOOTBALL -> {
                        LogUtil.d(TAG, "football")
                        val model = BallDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle football model = $model")

                            TCPReportDataManager.getInstance().sendFootballCommand(
                                cmdItem.timestampSecond,
                                model
                            )
                        }
                    }

                    // 主动测量的血氧值
                    CommandType.BLOOD_OXYGEN -> {
                        LogUtil.d(TAG, "blood oxygen")
                        val model = BloodOxygenDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle blood oxygen model = $model")

                            TCPReportDataManager.getInstance().sendOxygenCommand(
                                cmdItem.timestampSecond,
                                model.bloodOxygen
                            )
                        }
                    }

                    // 时间同步
                    CommandType.SYNC_TIME -> {

                    }

                    // 用户信息
                    CommandType.USER_INFO -> {

                    }

                    // 设备信息
                    CommandType.DEVICE_INFO -> {
                        LogUtil.d(TAG, "device info")
                        val model = DeviceInfoDataModel.build(cmdItem.data)
                        if (null != model) {
                            LogUtil.d(TAG, "handle device info model = $model")

                            BLESendCommandManager.instance.parseDeviceInfoCMD(model)
                        }
                    }

                    // 天气
                    CommandType.WEATHER -> {

                    }

                    // 来电提醒
                    CommandType.CALL_REMINDER -> {

                    }

                    // 断开连接命令
                    CommandType.DISCONNECT -> {

                    }

                    // 设置巡航坐标
                    CommandType.CRUISE_COORDINATE -> {

                    }

                    // 寻找手表
                    CommandType.FIND_WATCH -> {

                    }

                    // 同步语言
                    CommandType.SYNC_LANGUAGE -> {

                    }

                    // 信息（短信、微信、QQ等）
                    CommandType.MESSAGE -> {

                    }

                    // 设置单位制式
                    CommandType.SET_UNIT -> {

                    }

                    // iOS系统通知拦截配置
                    CommandType.IOS_NOTIFICATION_CONFIG -> {

                    }

                    // 海拔校验数据
                    CommandType.ALTITUDE_CALIBRATION -> {

                    }

                    // 时间制式
                    CommandType.TIME_UNIT -> {

                    }

                    // 设置主动测量心率
                    CommandType.SET_HEART_RATE -> {

                    }

                    // 传输图片
                    CommandType.TRANSFER_IMAGE -> {

                    }
                }
            }
        }
    }
}