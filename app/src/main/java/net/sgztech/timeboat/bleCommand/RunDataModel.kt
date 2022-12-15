package net.sgztech.timeboat.bleCommand

import com.imlaidian.utilslibrary.utils.LogUtil
import net.sgztech.timeboat.util.toHex
import org.apache.commons.lang3.Conversion.byteArrayToInt
import org.apache.commons.lang3.Conversion.byteArrayToShort

class RunDataModel {
    // 模式
    var model: Byte = 0

    // 完成指标
    var target: Byte = 0

    // 计划里程
    var planeDistance: Int = 0

    // 计划用时
    var planeUseTime: Int = 0

    // 实际里程
    var actualDistance: Int = 0

    // 实际用时
    var actualUseTime: Int = 0

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

    // 总计步数
    var stepCount: Int = 0

    constructor()

    constructor(buf: ByteArray) : this() {
        var index = 0

        if (index + 1 <= buf.size) {
            //模式  1byte
            model = buf[index]
            index++
        }

        if (index + 1 <= buf.size) {
            //完成指标 1byte
            target = buf[index]
            index++
        }

        if (index + 2 <= buf.size) {
            //计划里程 2 bytes, 公里数
            planeDistance = byteArrayToInt(buf, index, 0, 0, 2)
            index += 2
        }

        if (index + 4 <= buf.size) {
            //计划用时 4 bytes, 秒
            planeUseTime = byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            //实际里程 4 bytes, 米
            actualDistance = byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            //实际用时 4 bytes, 秒
            actualUseTime = byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            //卡路里
            calorie = byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            // 总计上坡高度
            totalUphillHeight = byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            //起始点经度
            startLng = byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            //起始点纬度
            startLat = byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 2 <= buf.size) {
            //经度数据数组 经度增量，精度 0.00001 度,每 5 秒钟记录一次数据
            val lngArrayLength = byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            if (lngArrayLength > 0) {
                if ((index + lngArrayLength) <= buf.size) {
                    lngArray = buf.copyOfRange(index, index + lngArrayLength)
                    index += lngArrayLength
                } else {
                    LogUtil.d(TAG, "index + lngArrayLength is overflow, $index, $lngArrayLength")
                }
            }
        }

        if (index + 2 <= buf.size) {
            //纬度数据数组 纬度增量，精度 0.00001 度,每 5 秒钟记录一次数据
            val latArrayLength = byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            if (latArrayLength > 0) {
                if ((index + latArrayLength) <= buf.size) {
                    latArray = buf.copyOfRange(index, index + latArrayLength)
                    index += latArrayLength
                } else {
                    LogUtil.d(TAG, "index + latArrayLength is overflow, $index, $latArrayLength")
                }
            }
        }

        if (index + 2 <= buf.size) {
            //速度数组
            val speedLength = byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            if (speedLength > 0) {
                if ((index + speedLength) <= buf.size) {
                    speedArray = buf.copyOfRange(index, index + speedLength)
                    index += speedLength
                } else {
                    LogUtil.d(TAG, "index + speedLength is overflow, $index, $speedLength")
                }
            }
        }

        if (index + 2 <= buf.size) {
            //海拔高度数组
            val altitudeLength = byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            if (altitudeLength > 0) {
                if (index + altitudeLength * 2 <= buf.size) {
                    altitudeList = ArrayList<Short>()
                    for (i in 0 until altitudeLength) {
                        val data = byteArrayToShort(buf, index, 0, 0, 2)
                        index += 2
                        (altitudeList as ArrayList<Short>).add(data)
                    }
                } else {
                    LogUtil.d(TAG, "index + altitudeLength * 2 is overflow, $index, $altitudeLength")
                }
            }
        }

        if (index + 2 <= buf.size) {
            // 配速数组
            val paceLength = byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            if (paceLength > 0) {
                if (index + paceLength * 2 <= buf.size) {
                    paceList = ArrayList<Short>()
                    for (i in 0 until paceLength) {
                        val data = byteArrayToShort(buf, index, 0, 0, 2)
                        index += 2
                        (paceList as ArrayList<Short>).add(data)
                    }
                } else {
                    LogUtil.d(TAG, "index + paceLength * 2 is overflow, $index, $paceLength")
                }
            }
        }

        if (index + 2 <= buf.size) {
            //心率测量数组
            val heartRateLength = byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            if (heartRateLength > 0) {
                if ((index + heartRateLength) <= buf.size) {
                    heartRateList = buf.copyOfRange(index, index + heartRateLength)
                    index += heartRateLength
                } else {
                    LogUtil.d(TAG, "index + heartRateLength is overflow, $index, $heartRateLength")
                }
            }
        }

        if (index + 2 <= buf.size) {
            //每分钟步数
            val everyMiniStepLength = byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            if (everyMiniStepLength > 0) {
                if (index + everyMiniStepLength * 2 <= buf.size) {
                    stepFrequencyList = ArrayList<Short>()
                    for (i in 0 until everyMiniStepLength) {
                        val data = byteArrayToShort(buf, index, 0, 0, 2)
                        index += 2
                        (stepFrequencyList as ArrayList<Short>).add(data)
                    }
                } else {
                    LogUtil.d(TAG, "index + everyMiniStepLength * 2 is overflow, $index, $everyMiniStepLength")
                }
            }
        }

        if (index + 4 <= buf.size) {
            // 总计步数
            stepCount = byteArrayToInt(buf, index, 0, 0, 4)
        }
    }

    override fun toString(): String {
        return String.format("model = %d, target = %d, " +
                "planeMileage = %d, planeTime = %d, " +
                "mileage = %d, useTime = %d, calorie = %d, " +
                "totalUphillHeight = %d, startLng = %d, startLat = %d, " +
                "lngArray = %s, latArray = %s, " +
                "speedArray = %s, altitudeList = %s, " +
                "paceList = %s, heartRateList = %s, everyMiniStepList = %s",
            model, target,
            planeDistance, planeUseTime,
            actualDistance, actualUseTime, calorie,
            totalUphillHeight, startLng, startLat,
            lngArray?.toHex(), latArray?.toHex(),
            speedArray?.toHex(), altitudeList?.toString(),
            paceList?.toString(), heartRateList?.toHex(), stepFrequencyList?.toString()
        )
    }

    companion object {
        private const val TAG = "RunningDataModel"

        @JvmStatic
        fun build(buf: ByteArray?) : RunDataModel? {
            if (null == buf) {
                return null
            }

            return RunDataModel(buf)
        }
    }
}