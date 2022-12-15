package net.sgztech.timeboat.bleCommand

import com.imlaidian.utilslibrary.utils.LogUtil
import net.sgztech.timeboat.util.toHex
import org.apache.commons.lang3.Conversion

class HikingDataModel {
    // 徒步里程
    var distance: Int = 0

    // 所用时间
    var useTime: Int = 0

    // 总计步数
    var stepCount: Int = 0

    // 卡路里
    var calorie: Int = 0

    // 总计上坡高度
    var totalUphillHeight: Int = 0

    // 起始点经度
    var startLng: Int = 0

    // 起始点纬度
    var startLat: Int = 0

    // 经度数据数组 经度增量，精度 0.00001 度,每 5 秒钟记录一次数据
    var lngArray: ByteArray? = null

    // 纬度数据数组 纬度增量，精度 0.00001 度,每 5 秒钟记录一次数据
    var latArray: ByteArray? = null

    // 速度数组
    var speedArray: ByteArray? = null

    // 海拔高度数组 单位:秒, 每公里所用时间，每公里记录一次数据
    var altitudeList: List<Short>? = null

    // 配速数组
    var paceList: List<Short>? = null

    // 心率测量数组
    var heartRateList: ByteArray? = null

    // 每分钟步数
    var stepFrequencyList: List<Short>? = null

    constructor()

    constructor(buf: ByteArray) : this() {
        var index = 0

        if (index + 4 <= buf.size) {
            //徒步里程
            distance = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            useTime = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            stepCount = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            //卡路里
            calorie = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            // 总计上坡高度
            totalUphillHeight = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            //起始点经度
            startLng = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
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
            //速度数组
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
            //配速数组
            val paceLength = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            if (paceLength > 0) {
                if (index + paceLength <= buf.size) {
                    paceList = ArrayList<Short>()
                    for (i in 0 until paceLength) {
                        val data = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
                        index += 2
                        (paceList as ArrayList<Short>).add(data)
                    }
                } else {
                    LogUtil.d(TAG, "index + paceLength is overflow, $index, $paceLength")
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
                    index += heartRateLength
                } else {
                    LogUtil.d(TAG, "index + heartRateLength is overflow, $index, $heartRateLength")
                }
            }
        }

        if (index + 2 <= buf.size) {
            //每分钟步数
            val everyMiniStepLength = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            stepFrequencyList = ArrayList()
            for (i in 0 until everyMiniStepLength) {
                val data = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
                index += 2
                (stepFrequencyList as ArrayList<Short>).add(data)
            }
        }
    }

    override fun toString(): String {
        return String.format("distance = %d, useTime = %d, stepCount = %d, calorie = %d," +
                "totalUphillHeight = %d, startLng = %d, startLat = %d, " +
                "lngArray = %s, latArray = %s, speedArray = %s, " +
                "altitudeList = %s, paceList = %s, heartRateList = %s," +
                "stepFrequencyList = %s",
            distance, useTime, stepCount, calorie,
            totalUphillHeight, startLng, startLat,
            lngArray?.toHex(), latArray?.toHex(), speedArray?.toHex(),
            altitudeList?.toString(), paceList?.toString(), heartRateList?.toHex(),
            stepFrequencyList?.toString())
    }

    companion object {
        private const val TAG = "WalkDataModel"

        @JvmStatic
        fun build(buf: ByteArray?) : HikingDataModel? {
            if (null == buf) {
                return null
            }

            return HikingDataModel(buf)
        }
    }
}