package net.sgztech.timeboat.bleCommand

import com.imlaidian.utilslibrary.utils.LogUtil
import net.sgztech.timeboat.util.toHex
import org.apache.commons.lang3.Conversion

//冲浪
class SurfingDataModel {
    // 里程 2Byte (单位:米)
    var distance: Int = 0

    // 所用时间 4Byte (单位:秒)
    var useTime: Int = 0

    // 起始经度 S32 (精度 0.00001 度)
    var startLng: Int = 0

    // 起始纬度 S32 (精度 0.00001 度)
    var startLat: Int = 0

    // 经度数组 S8[] (经度增量，精度 0.00001 度,每 5 秒钟记录一次数据)
    var lngArray: ByteArray? = null

    // 纬度数组 S8[] (纬度增量，精度 0.00001 度,每 5 秒钟记录一次数据)
    var latArray: ByteArray? = null

    // 速度数组 U8[] (单位:0.1km/h，每 5 秒钟记录一次数据)
    var speedArray: ByteArray? = null

    // 心率数组 U8[] (单位:次/分钟，每分钟记录一次数据)//目前固定是 0
    var heartRateList: ByteArray? = null

    // 卡路里
    var calorie: Int = 0

    constructor()

    constructor(buf: ByteArray) : this() {
        var index = 0

        if (index + 2 <= buf.size) {
            // 里程 4Byte (单位:米)
            distance = Conversion.byteArrayToInt(buf, index, 0, 0, 2)
            index += 2
        }

        if (index + 4 <= buf.size) {
            // 所用时间 4Byte (单位:秒)
            useTime = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            // 起始点经度
            startLng = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            // 起始点纬度
            startLat = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 2 <= buf.size) {
            // 经度数据数组 经度增量，精度 0.00001 度,每 5 秒钟记录一次数据
            val lngArrayLength = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            if (lngArrayLength > 0) {
                if (index + lngArrayLength <= buf.size) {
                    lngArray = buf.copyOfRange(index, index + lngArrayLength)
                    index += lngArrayLength
                } else {
                    LogUtil.d(TAG, "index + lngArrayLength is overflow, $index, $lngArrayLength")
                }
            }
        }

        if (index + 2 <= buf.size) {
            // 纬度数据数组 纬度增量，精度 0.00001 度,每 5 秒钟记录一次数据
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
            // 速度数组
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
            // 心率测量数组
            val heartRateLength = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            if (heartRateLength > 0) {
                if (index + heartRateLength <= buf.size) {
                    heartRateList = buf.copyOfRange(index, index + heartRateLength)
                    index += heartRateLength
                } else {
                    LogUtil.d(TAG, "index + heartRateLength is overflow, $index, $heartRateLength")
                }
            }
        }

        if (index + 4 <= buf.size) {
            // 卡路里
            calorie = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
        }
    }

    override fun toString(): String {
        return String.format("distance = %d, useTime = %d, startLng = %d, startLat = %d, calorie = %d, " +
                "lngArray = %s, latArray = %s, speedArray = %s, heartRateList = %s",
            distance, useTime, startLng, startLat, calorie,
            lngArray?.toHex(), latArray?.toHex(), speedArray?.toHex(), heartRateList?.toHex())
    }

    companion object {
        private const val TAG = "SurfingDataModel"

        @JvmStatic
        fun build(buf: ByteArray?) : SurfingDataModel? {
            if (null == buf) {
                return null
            }

            return SurfingDataModel(buf)
        }
    }
}