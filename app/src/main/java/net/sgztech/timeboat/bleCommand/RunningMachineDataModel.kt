package net.sgztech.timeboat.bleCommand

import com.imlaidian.utilslibrary.utils.LogUtil
import org.apache.commons.lang3.Conversion

class RunningMachineDataModel {
    // 所用时间 4Byte (单位:秒)
    var useTime: Int = 0

    // 总计步数 4Byte
    var stepCount: Int = 0

    // 卡路里 4Byte （cal）
    var calorie: Int = 0

    // 步频数组 uint16[]（步数每分钟，运动过程中，每一分钟记录一个数据）
    var stepFrequencyList: List<Short>? = null

    // 心率数组 uint8 [ ] (单位:次/分钟，每 5 分钟记录一次数据)
    var heartRateList: ByteArray? = null

    constructor()

    constructor(buf: ByteArray) : this() {
        var index = 0

        if (index + 4 <= buf.size) {
            // 所用时间 4Byte (单位:秒)
            useTime = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            // 总计步数 4Byte
            stepCount = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            // 卡路里 4Byte （cal）
            calorie = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 2 <= buf.size) {
            // 步频数组 uint16[]（步数每分钟，运动过程中，每一分钟记录一个数据）
            val walkRateLength = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
            index += 2;

            if (walkRateLength > 0) {
                if (index + walkRateLength * 2 <= buf.size) {
                    stepFrequencyList = ArrayList<Short>()
                    for (i in 0 until walkRateLength) {
                        val data = Conversion.byteArrayToShort(buf, index, 0, 0, 2)
                        index += 2
                        (stepFrequencyList as ArrayList<Short>).add(data)
                    }
                } else {
                    LogUtil.d(TAG, "index + walkRateLength * 2 is overflow, $index, $walkRateLength")
                }
            }
        }

        if (index + 2 <= buf.size) {
            // 心率数组 uint8[] (单位:次/分钟，每 5 分钟记录一次数据)
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
        return String.format("useTime = %d, stepCount = %d, calorie = %d, " +
                "stepFrequencyList = %s, heartRateList = %s",
            useTime, stepCount, calorie,
            stepFrequencyList?.toString(), heartRateList?.toString())
    }

    companion object {
        private const val TAG = "RunTrainingDataModel"

        @JvmStatic
        fun build(buf: ByteArray?) : RunningMachineDataModel? {
            if (null == buf) {
                return null
            }

            return RunningMachineDataModel(buf)
        }
    }
}