package net.sgztech.timeboat.bleCommand

import com.imlaidian.utilslibrary.utils.LogUtil
import net.sgztech.timeboat.util.toHex
import org.apache.commons.lang3.Conversion

//球类
class BallDataModel {
    // 卡路里
    var calorie: Int = 0

    //  所用时间 U32 (单位:秒)
    var useTime: Int = 0

    // 起始点经度 4Byte 单位：0.00001
    var startLng: Int = 0

    // 起始点纬度 4Byte 单位：0.00001
    var startLat: Int = 0

    // 经度数据数组 经度增量，精度 0.00001 度,每 5 秒钟记录一次数据
    var lngArray: ByteArray? = null

    // 纬度数据数组 纬度增量，精度 0.00001 度,每 5 秒钟记录一次数据
    var latArray: ByteArray? = null

    // 速度数组
    var speedArray: ByteArray? = null

    // 心率测量数组
    var heartRateList: ByteArray? = null

    constructor()

    constructor(buf: ByteArray) : this() {
        var index = 0

        if (index + 4 <= buf.size) {
            //卡路里
            calorie = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            // 所用时间 U32 (单位:秒)
            useTime = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            // 起始点经度 4Byte 单位：0.00001
            startLng = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            // 起始点纬度 4Byte 单位：0.00001
            index += 4
        }

        if (index + 4 <= buf.size) {
            startLat = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 2 <= buf.size) {
            //经度数据数组 经度增量，精度 0.00001 度,每 5 秒钟记录一次数据
            val lngArrayLength = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            if (lngArrayLength > 0) {
                if ((index + lngArrayLength) <= buf.size) {
                    lngArray = buf.copyOfRange(index, index + lngArrayLength)
                    index += lngArrayLength
                } else {
                    LogUtil.d(TAG, "index + lngArray is overflow, $index, $lngArray")
                }
            }
        }

        if (index + 2 <= buf.size) {
            //纬度数据数组 纬度增量，精度 0.00001 度,每 5 秒钟记录一次数据
            val latArrayLength = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            if (latArrayLength > 0) {
                if (index + latArrayLength <= buf.size) {
                    latArray = buf.copyOfRange(index, index + latArrayLength)
                    index += latArrayLength
                } else {
                    LogUtil.d(TAG, "index + latArrayLength is overflow, $index, $latArrayLength")
                }
            }
        }

        if (index + 2 <= buf.size) {
            //速度数组Ω
            val speedLength = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            if (speedLength > 0) {
                if (index + speedLength <= buf.size) {
                    speedArray = buf.copyOfRange(index, index + speedLength)
                    index += speedLength
                } else {
                    LogUtil.d(TAG, "index + speedLength is overflow, $index, $speedLength")
                }
            }
        }

        if (index + 2 <= buf.size) {
            //心率测量数组
            val heartRateLength = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            if (heartRateLength > 0) {
                if (index + heartRateLength <= buf.size) {
                    heartRateList = buf.copyOfRange(index, index + heartRateLength)
                } else {
                    LogUtil.d(TAG, "index + heartRateLength is overflow, $index, $heartRateLength")
                }
            }
        }
    }

    override fun toString(): String {
        return String.format("calorie = %d, totalTime = %d, " +
                "startLng = %d, startLat = %d, " +
                "lngArray = %s, latArray = %s, " +
                "speedArray = %s, heartRateList = %s",
            calorie, useTime,
            startLng, startLat,
            lngArray?.toHex(),
            latArray?.toHex(),
            speedArray?.toHex(),
            heartRateList?.toHex()
        )
    }

    companion object {
        private const val TAG = "BallDataModel"

        @JvmStatic
        fun build(buf: ByteArray?): BallDataModel? {
            if (null == buf) {
                return null
            }

            return BallDataModel(buf)
        }
    }
}