package net.sgztech.timeboat.bleCommand

import com.imlaidian.utilslibrary.utils.LogUtil
import net.sgztech.timeboat.util.toHex
import org.apache.commons.lang3.Conversion

class RockClimbingDataModel {
    // 高度 2Byte (单位:米)
    var height: Int = 0

    // 所用时间 4Byte (单位:秒)
    var useTime: Int = 0

    // 心率数组 uint8[] (单位:次/分钟，每分钟记录一次数据)
    var heartRateList: ByteArray? = null

    // 卡路里 4Byte (单位：卡)
    var calorie: Int = 0

    //  海拔高度数组 int16 [
    var altitudeList: List<Short>? = null

    constructor()

    constructor(buf: ByteArray) : this() {
        var index = 0

        if (index + 2 <= buf.size) {
            //高度 2Byte (单位:米)
            height = Conversion.byteArrayToInt(buf, index, 0, 0, 2)
            index += 2
        }

        if (index + 4 <= buf.size) {
            // 所用时间 4Byte (单位:秒)
            useTime = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 2 <= buf.size) {
            // 心率数组 uint8[] (单位:次/分钟，每分钟记录一次数据)
            val heartRateArrayLength = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            if (heartRateArrayLength > 0) {
                if (index + heartRateArrayLength <= buf.size) {
                    heartRateList = buf.copyOfRange(index, index + heartRateArrayLength)
                    index += heartRateArrayLength
                } else {
                    LogUtil.d(TAG, "index + heartRateArrayLength * 2 is overflow, $index, $heartRateArrayLength")
                }
            }
        }

        if (index + 4 <= buf.size) {
            //卡路里 4Byte (单位：卡)
            calorie = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 2 <= buf.size) {
            // 海拔高度数组 int16 []
            // 海拔高度数组 单位:秒, 每公里所用时间，每公里记录一次数据
            val altitudeLength = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
            index += 2

            if (altitudeLength > 0) {
                if (index + altitudeLength * 2 <= buf.size) {
                    altitudeList = ArrayList<Short>()
                    for (i in 0 until altitudeLength) {
                        val data = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
                        index += 2

                        (altitudeList as ArrayList<Short>).add(data)
                    }
                } else {
                    LogUtil.d(TAG, "index + altitudeLength * 2 is overflow, $index, $altitudeLength")
                }
            }
        }
    }

    override fun toString(): String {
        return String.format("height = %d, useTime = %d, calorie = %d, " +
                "heartRateList = %s, altitudeList = %s",
            height, useTime, calorie, heartRateList?.toHex(), altitudeList?.toString())
    }

    companion object {
        private const val TAG = "RockClimbingDataModel"

        @JvmStatic
        fun build(buf: ByteArray?) : RockClimbingDataModel? {
            if (null == buf) {
                return null
            }

            return RockClimbingDataModel(buf)
        }
    }
}