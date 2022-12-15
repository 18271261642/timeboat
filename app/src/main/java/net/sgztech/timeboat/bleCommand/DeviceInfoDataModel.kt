package net.sgztech.timeboat.bleCommand

import org.apache.commons.lang3.Conversion

class DeviceInfoDataModel {
    // 标示符 2Byte
    var indication: Short = 0

    // 计步 1Byte
    var stepCount: Byte = 0

    // 心率 1Byte
    var heartRate: Byte = 0

    // 睡眠 1Byte
    var sleep: Byte = 0;

    // 跑步 1Byte
    var run: Byte = 0

    //  徒步 1Byte
    var hiking: Byte = 0

    // 马拉松 1Byte
    var marathon: Byte = 0

    // 跳绳 1Byte
    var skippingRope: Byte = 0

    // 户外游泳 1Byte
    var swim: Byte = 0

    // 攀岩 1Byte
    var rockClimbing: Byte = 0

    // 滑雪 1Byte
    var skiing: Byte = 0

    //  骑行 1Byte
    var ride: Byte = 0

    // 划船 1Byte
    var boat: Byte = 0

    //  蹦极 1byte
    var bungeeJumping: Byte = 0

    //  登山 1Byte
    var mountaineering: Byte = 0

    //  跳伞 1Byte
    var parachuteJump: Byte = 0

    //  20 高尔夫 1Byte
    var golf: Byte = 0

    //  冲浪 1Byte
    var surfing: Byte = 0

    //  跑步机 1Byte
    var runningMachine: Byte = 0

    //  主动心率测量开关 1Byte 1：测量打开，0：测量关闭 （默认）
    var measureHeartRate: Byte = 0

    //  蓝牙地址 6Byte
    var bleAddress: ByteArray? = null

    // 羽毛球 1Byte
    var badminton: Byte = 0

    // 篮球 1Byte
    var basketball: Byte = 0

    //  足球 1Byte
    var footBall: Byte = 0

    //  血氧 1Byte
    var bloodOxygen: Byte = 0

    constructor()

    constructor(buf: ByteArray) : this() {
        var index = 0

        if (index + 2 <= buf.size) {
            //  标示符 2Byte
            indication = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
            index += 2
        }

        if (index + 1 <= buf.size) {
            // 计步 1Byte
            stepCount = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            // 心率 1Byte
            heartRate = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            // 睡眠 1Byte
            sleep = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            // 跑步 1Byte
            run = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            // 徒步 1Byte
            hiking = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            // 马拉松 1Byte
            marathon = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            // 跳绳 1Byte
            skippingRope = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            // 户外游泳 1Byte
            swim = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            // 攀岩 1Byte
            rockClimbing = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            //    滑雪 1Byte
            skiing = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            // 骑行 1Byte
            ride = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            // 划船 1Byte
            boat = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            // 蹦极 1byte
            bungeeJumping = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            // 登山 1Byte
            mountaineering = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            // 跳伞 1Byte
            parachuteJump = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            // 高尔夫 1Byte
            golf = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            // 冲浪 1Byte
            surfing = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            // 跑步机 1Byte
            runningMachine = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            // 主动心率测量开关 1Byte 1：测量打开，0：测量关闭 （默认）
            measureHeartRate = buf[index]
            index += 1
        }

        if (index + 6 <= buf.size) {
            // 蓝牙地址 6Byte
            bleAddress = buf.copyOfRange(index, index + 6)
            index += 6
        }

        if (index + 1 <= buf.size) {
            // 羽毛球 1Byte
            badminton = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            // 篮球 1Byte
            basketball = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            // 足球 1Byte
            footBall = buf[index]
            index += 1
        }

        if (index + 1 <= buf.size) {
            // 血氧 1Byte
            bloodOxygen = buf[index]
            index += 1
        }
    }

    companion object {
        private const val TAG = "DeviceInfoDataModel"

        @JvmStatic
        fun build(buf: ByteArray?) : DeviceInfoDataModel? {
            if (null == buf) {
                return null
            }

            return DeviceInfoDataModel(buf)
        }
    }
}