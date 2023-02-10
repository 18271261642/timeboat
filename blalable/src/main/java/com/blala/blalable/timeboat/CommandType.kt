package com.blala.blalable.timeboat

import java.util.*

class CommandType private constructor() {
    companion object {
        // 命令头
        const val BEGIN_CMD_HEADER: Byte = (0xAA).toByte()

        // 数据包类型表：类型编码说明
        // 计步数据
        const val STEP_COUNT: Byte = 0x01

        // 心率数据
        const val HEART_RATE: Byte = 0x02

        // 睡眠监测数据
        const val SLEEP: Byte = 0x03

        // 跑步运动数据
        const val RUNNING: Byte = 0x04

        // 徒步运动数据
        const val HIKING: Byte = 0x05

        // 马拉松训练数据
        const val MARATHON: Byte = 0x06

        // 跳绳运动数据
        const val SKIPPING_ROPE: Byte = 0x07

        // 游泳运动数据
        const val SWIM: Byte = 0x08

        // 攀岩运动数据
        const val ROCK_CLIMBING: Byte = 0x09

        // 滑雪运动数据
        const val SKIING: Byte = 0x0A

        // 骑行运动数据
        const val RIDE: Byte = 0x0B

        // 划船运动数据
        const val BOAT: Byte = 0x0C

        // 蹦极运动数据
        const val BUNGEE_JUMPING: Byte = 0x0D

        // 登山运动数据
        const val MOUNTAINEERING: Byte = 0x0E

        // 跳伞运动数据
        const val PARACHUTE_JUMP: Byte = 0x0F

        // 高尔夫运动数据
        const val GOLF: Byte = 0x10

        // 冲浪运动数据
        const val SURFING: Byte = 0x11

        // 气压检测数据
        const val BAROMETRIC_PRESSURE: Byte = 0x12

        // SOS呼救
        const val SOS: Byte = 0x13

        // 跑步机训练数据
        const val RUNNING_MACHINE: Byte = 0x14

        // 拍照
        const val PICTURE: Byte = 0x15

        // 寻找手机
        const val FIND_PHONE: Byte = 0x16

        // 当前主动测量心率值
        const val MEASURE_HEART_RATE: Byte = 0x17

        // 当前主动测量体温值
        const val MEASURE_BODY_TEMPERATURE: Byte = 0x18

        // 计步总步数
        const val TOTAL_STEP: Byte = 0x19

        // 手表改变配置信息
        const val CHANGE_CONFIG: Byte = 0x20

        // 羽毛球
        const val BADMINTON: Byte = 0x21

        // 篮球
        const val BASKETBALL: Byte = 0x22

        // 足球
        const val FOOTBALL: Byte = 0x23

        // 主动测量的血氧值
        const val BLOOD_OXYGEN: Byte = 0x24

        // ======手机应用 -> 手表==========
        // 时间同步
        const val SYNC_TIME: Byte = 0x30

        // 用户信息
        const val USER_INFO: Byte = 0x31

        // 设备信息
        const val DEVICE_INFO: Byte = 0x32

        // 天气
        const val WEATHER: Byte = 0x33

        // 来电提醒
        const val CALL_REMINDER: Byte = 0x34

        // 断开连接命令
        const val DISCONNECT: Byte = 0x35

        // 设置巡航坐标
        const val CRUISE_COORDINATE: Byte = 0x36

        // 寻找手表
        const val FIND_WATCH: Byte = 0x38

        // 同步语言
        const val SYNC_LANGUAGE: Byte = 0x39

        // 信息（短信、微信、QQ等）
        const val MESSAGE: Byte = 0x40

        // 设置单位制式
        const val SET_UNIT: Byte = 0x41

        // IOS系统通知拦截配置
        const val IOS_NOTIFICATION_CONFIG: Byte = 0x42

        // 海拔校验数据
        const val ALTITUDE_CALIBRATION: Byte = 0x43

        // 时间制式
        const val TIME_UNIT: Byte = 0x44

        // 设置主动测量心率
        const val SET_HEART_RATE: Byte = 0x45

        // 传输图片
        const val TRANSFER_IMAGE: Byte = 0x46

        @JvmStatic
        fun isHeader(c: Byte?): Boolean {
            if (null != c) {
                return c == BEGIN_CMD_HEADER
            }

            return false
        }

        @JvmStatic
        fun isCMDTypeValid(type: Byte?): Boolean {
            if (null != type) {
                return type in 1..0x7f
            }

            return false
        }

        @JvmStatic
        fun buildCommandHeader(cmdType: Byte, length: Int): ByteArray {
            val timeStamp: Long = System.currentTimeMillis() / 1000
            val item = ByteArray(8)

            item[0] = BEGIN_CMD_HEADER
            item[1] = cmdType
            item[2] = (length and 0xff).toByte()
            item[3] = (length shr 8 and 0xff).toByte()
            item[4] = (timeStamp and 0xff).toByte()
            item[5] = (timeStamp shr 8 and 0xff).toByte()
            item[6] = (timeStamp shr 16 and 0xff).toByte()
            item[7] = (timeStamp shr 24 and 0xff).toByte()

            return item
        }

        @JvmStatic
        fun buildStepCountCMDHeader(): ByteArray {
            return buildCommandHeader(STEP_COUNT, 0)
        }

        @JvmStatic
        fun buildHeartRateCMDHeader(): ByteArray {
            return buildCommandHeader(HEART_RATE, 0)
        }

        @JvmStatic
        fun buildSleepCMDHeader(): ByteArray {
            return buildCommandHeader(SLEEP, 0)
        }

        @JvmStatic
        fun buildRunningCMDHeader(): ByteArray {
            return buildCommandHeader(RUNNING, 0)
        }

        @JvmStatic
        fun buildHikingCMDHeader(): ByteArray {
            return buildCommandHeader(HIKING, 0)
        }

        @JvmStatic
        fun buildMarathonCMDHeader(): ByteArray {
            return buildCommandHeader(MARATHON, 0)
        }

        @JvmStatic
        fun buildSkippingRopeCMDHeader(): ByteArray {
            return buildCommandHeader(SKIPPING_ROPE, 0)
        }

        @JvmStatic
        fun buildSwimmingCMDHeader(): ByteArray {
            return buildCommandHeader(SWIM, 0)
        }

        @JvmStatic
        fun buildRockClimbingCMDHeader(): ByteArray {
            return buildCommandHeader(ROCK_CLIMBING, 0)
        }

        @JvmStatic
        fun buildSkiingCMDHeader(): ByteArray {
            return buildCommandHeader(SKIING, 0)
        }

        @JvmStatic
        fun buildRideCMDHeader(): ByteArray {
            return buildCommandHeader(RIDE, 0)
        }

        @JvmStatic
        fun buildBoatCMDHeader(): ByteArray {
            return buildCommandHeader(BOAT, 0)
        }

        @JvmStatic
        fun buildBungeeJumpingCMDHeader(): ByteArray {
            return buildCommandHeader(BUNGEE_JUMPING, 0)
        }

        @JvmStatic
        fun buildMountaineeringCMDHeader(): ByteArray {
            return buildCommandHeader(MOUNTAINEERING, 0)
        }

        @JvmStatic
        fun buildParachuteJumpCMDHeader(): ByteArray {
            return buildCommandHeader(PARACHUTE_JUMP, 0)
        }

        @JvmStatic
        fun buildGolfCMDHeader(): ByteArray {
            return buildCommandHeader(GOLF, 0)
        }

        @JvmStatic
        fun buildSurfingCMDHeader(): ByteArray {
            return buildCommandHeader(SURFING, 0)
        }

        @JvmStatic
        fun buildPressureCMDHeader(): ByteArray {
            return buildCommandHeader(BAROMETRIC_PRESSURE, 0)
        }

        @JvmStatic
        fun buildSOSCMDHeader(): ByteArray {
            return buildCommandHeader(SOS, 0)
        }

        @JvmStatic
        fun buildRunTrainingCMDHeader(): ByteArray {
            return buildCommandHeader(RUNNING_MACHINE, 0)
        }

        @JvmStatic
        fun buildPictureCMDHeader(): ByteArray {
            return buildCommandHeader(PICTURE, 0)
        }

        @JvmStatic
        fun buildFindPhoneCMDHeader(): ByteArray {
            return buildCommandHeader(FIND_PHONE, 0)
        }

        @JvmStatic
        fun buildMeasureHeartRateCMDHeader(): ByteArray {
            return buildCommandHeader(MEASURE_HEART_RATE, 0)
        }

        @JvmStatic
        fun buildMeasureBodyTemperatureCMDHeader(): ByteArray {
            return buildCommandHeader(MEASURE_BODY_TEMPERATURE, 0)
        }

        @JvmStatic
        fun buildTotalStepCMDHeader(): ByteArray {
            return buildCommandHeader(TOTAL_STEP, 0)
        }

        @JvmStatic
        fun buildChangeConfigCMDHeader(): ByteArray {
            return buildCommandHeader(CHANGE_CONFIG, 0)
        }

        @JvmStatic
        fun buildBadmintonCMDHeader(): ByteArray {
            return buildCommandHeader(BADMINTON, 0)
        }

        @JvmStatic
        fun buildBasketballCMDHeader(): ByteArray {
            return buildCommandHeader(BASKETBALL, 0)
        }

        @JvmStatic
        fun buildFootballCMDHeader(): ByteArray {
            return buildCommandHeader(FOOTBALL, 0)
        }

        @JvmStatic
        fun buildBloodOxygenCMDHeader(): ByteArray {
            return buildCommandHeader(BLOOD_OXYGEN, 0)
        }

        // 年 2Byte
        // 月 1Byte
        // 日 1Byte
        // 时 1Byte
        // 分 1Byte
        // 秒 1Byte
        // 时区符号 1Byte //1:正时区 东部地区，0：负时区
        // 时区 1Byte //高 2 位代表时区，个位代表半点
        // 安卓手机标记 4byte = 0x78563412， 请参考下面注解
        @JvmStatic
        fun buildSyncTimeCommand(timezoneType: Byte = 1): ByteArray {
            val date = Date(System.currentTimeMillis())
            val timeLength = 13;
            val syncTimeHeaderArray = buildCommandHeader(SYNC_TIME, timeLength)

            val calendar = Calendar.getInstance()
            calendar.time = date
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH] + 1
            val day = calendar[Calendar.DAY_OF_MONTH]
            val hour = calendar[Calendar.HOUR_OF_DAY]
            val minute = calendar[Calendar.MINUTE]
            val second = calendar[Calendar.SECOND]
            val timezone = 0x50
            val androidType = 0x78563412

            var index = syncTimeHeaderArray.size
            val timeCommand = ByteArray(timeLength + index)
            syncTimeHeaderArray.copyInto(timeCommand, 0, 0, index)
            timeCommand[index] = (year and 0xff).toByte()
            index += 1
            timeCommand[index] = (year shr 8 and 0xff).toByte()
            index += 1
            timeCommand[index] = month.toByte()
            index += 1
            timeCommand[index] = day.toByte()
            index += 1
            timeCommand[index] = hour.toByte()
            index += 1
            timeCommand[index] = minute.toByte()
            index += 1
            timeCommand[index] = second.toByte()
            index += 1
            timeCommand[index] = timezoneType
            index += 1
            timeCommand[index] = timezone.toByte()
            index += 1
            timeCommand[index] = (androidType and 0xff).toByte()
            index += 1
            timeCommand[index] = (androidType shr 8 and 0xff).toByte()
            index += 1
            timeCommand[index] = (androidType shr 16 and 0xff).toByte()
            index += 1
            timeCommand[index] = (androidType shr 24 and 0xff).toByte()

            return timeCommand
        }
    }
}