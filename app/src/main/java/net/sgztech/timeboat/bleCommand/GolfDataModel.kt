package net.sgztech.timeboat.bleCommand

import com.imlaidian.utilslibrary.utils.LogUtil
import net.sgztech.timeboat.util.toHex
import org.apache.commons.lang3.Conversion

//高尔夫
class GolfDataModel {
    // 总杆数 4Byte
    var rodsNumber: Int = 0

    // 所用时间 4Byte
    var totalTime: Int = 0

    // 总计步数 4Byte
    var totalStep: Int = 0

    // 起始点经度 4Byte 单位：0.00001
    var startLng: Int = 0

    // 起始点纬度 4Byte 单位：0.00001
    var startLat: Int = 0

    // 经度数据数组 int8[]
    var lngArray: ByteArray? = null

    // 纬度数据数组 int8[]
    var latArray: ByteArray? = null

    // 速度数组 uint8[] 0.1km/h
    var speedArray: ByteArray? = null

    // 海拔高度数组 int16[]
    var altitudeList: List<Short>? = null

    // 心率测量数组 uint8[] (单位:次/分钟，每分钟记录一次数据)
    var heartRateList: ByteArray? = null

    // 每洞最长击球距离 uint16[]
    var ballDistanceList: List<Short>? = null

    // 每洞杆数 uint8[]
    var holeCount: ByteArray? = null

    constructor()

    constructor(buf: ByteArray) : this() {
        var index = 0

        if (index + 4 <= buf.size) {
            // 总杆数 4Byte
            rodsNumber = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            // 所用时间 4Byte
            totalTime = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            // 总计步数 4Byte
            totalStep = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            // 起始点经度 4Byte 单位：0.00001
            startLng = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            // 起始点纬度
            startLat = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 2 <= buf.size) {
            // 经度数据数组,经度增量，精度 0.00001 度,每 5 秒钟记录一次数据
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
            // 海拔高度数组 单位:秒, 每公里所用时间，每公里记录一次数据
            val altitudeLength = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            if (altitudeLength > 0) {
                if (index + altitudeLength <= buf.size) {
                    altitudeList = ArrayList<Short>()
                    for (i in 0 until altitudeLength) {
                        val data = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
                        index += 2
                        (altitudeList as ArrayList<Short>).add(data)
                    }
                } else {
                    LogUtil.d(TAG, "index + altitudeLength is overflow, $index, $altitudeLength")
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

        if (index + 2 <= buf.size) {
            // 每洞最长击球距离 uint16[]
            val ballDistanceLength = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            if (ballDistanceLength > 0) {
                if (index + ballDistanceLength * 2 <= buf.size) {
                    ballDistanceList = ArrayList<Short>()
                    for (i in 0 until ballDistanceLength) {
                        val data = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
                        index += 2
                        (ballDistanceList as ArrayList<Short>).add(data)
                    }
                } else {
                    LogUtil.d(TAG, "index + ballDistanceLength is overflow, $index, $ballDistanceLength")
                }
            }
        }

        if (index + 2 <= buf.size) {
            // 每洞杆数 uint8[]
            val holeLength = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            if (holeLength > 0) {
                if (index + holeLength <= buf.size) {
                    holeCount = buf.copyOfRange(index, index + holeLength)
                } else {
                    LogUtil.d(TAG, "index + holeLength is overflow, $index, $holeLength")
                }
            }
        }
    }

    override fun toString(): String {
        return String.format("rodsNumber = %d, totalTime = %d, totalStep = %d, " +
                "startLng = %d, startLat = %d, lngArray = %s, latArray = %s, " +
                "speedArray = %s, altitudeList = %s, heartRateList = %s, " +
                "ballDistanceList = %s, holeCount = %s",
            rodsNumber, totalTime, totalStep,
            startLng, startLat, lngArray?.toHex(), latArray?.toHex(),
            speedArray?.toHex(), altitudeList?.toString(), heartRateList?.toHex(),
            ballDistanceList?.toString(), holeCount?.toHex())
    }

    companion object {
        private const val TAG = "GolfDataModel"

        @JvmStatic
        fun build(buf: ByteArray?) : GolfDataModel? {
            if (null == buf) {
                return null
            }

            return GolfDataModel(buf)
        }
    }
}
