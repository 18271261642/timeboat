package net.sgztech.timeboat.bleCommand

import com.imlaidian.utilslibrary.utils.LogUtil
import net.sgztech.timeboat.util.toHex
import org.apache.commons.lang3.Conversion

class RideDataModel {
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

    // 卡路里
    var calorie: Int = 0

    // 总计上坡高度
    var totalUphillHeight: Int = 0

    constructor()

    constructor(buf: ByteArray) : this() {
        var index = 0

        if (index + 1 <= buf.size) {
            // 模式 1byte
            model = buf[index]
            index++
        }

        if (index + 1 <= buf.size) {
            // 完成指标 1byte
            target = buf[index]
            index++
        }

        if (index + 4 <= buf.size) {
            // 计划里程 4byte
            planeDistance = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            // 计划用时  4byte
            planeUseTime = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            //实际里程
            actualDistance = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            //实际用时
            actualUseTime = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            //起始点经度
            startLng = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            //起始点纬度
            startLat = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 2 <= buf.size) {
            //经度数据数组 经度增量，精度 0.00001 度,每 5 秒钟记录一次数据
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
            //海拔高度数组 单位:秒, 每公里所用时间，每公里记录一次数据
            val altitudeLength = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            if (altitudeLength > 0) {
                if (index + altitudeLength * 2 <= buf.size) {
                    altitudeList = ArrayList()
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
                if (index + paceLength * 2 <= buf.size) {
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
    }

    override fun toString(): String {
        return String.format("model = %d, target = %d, calorie = %d" +
                "totalUphillHeight = %d, planeDistance = %d, planeUseTime = %d," +
                "actualDistance = %d, actualUseTime = %d, startLng = %d, startLat = %d," +
                "lngArray = %s, latArray = %s, speedArray = %s," +
                "altitudeList = %s, paceList = %s, heartRateList = %s",
            model, target, calorie,
            totalUphillHeight, planeDistance, planeUseTime,
            actualDistance, actualUseTime, startLng, startLat,
            lngArray?.toHex(), latArray?.toHex(), speedArray?.toHex(),
            altitudeList?.toString(), paceList?.toString(), heartRateList?.toHex()
        )
    }

    companion object {
        private const val TAG = "RideDataModel"

        @JvmStatic
        fun build(buf: ByteArray?) : RideDataModel? {
            if (null == buf) {
                return null
            }

            return RideDataModel(buf)
        }
    }
}