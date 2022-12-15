package net.sgztech.timeboat.bleCommand

import org.apache.commons.lang3.Conversion
import kotlin.experimental.and

/**
 * Created by Jun on 2022/7/27 10:21 上午.
 */
class HeartRateDataModel {
    // 心率, 4 byte
    var heartRate: Int = 0

    constructor()

    constructor(buf: ByteArray) : this() {
        if (buf.size == DATA_LEN_MIN) {
            heartRate = (buf[0] and 0xff.toByte()).toInt()
        } else if (buf.size == DATA_LEN_MAX) {
            heartRate = Conversion.byteArrayToInt(buf, 0, 0, 0, 4)
        }
    }

    override fun toString(): String {
        return "heartRate = $heartRate"
    }

    companion object {
        private const val DATA_LEN_MAX = 4
        private const val DATA_LEN_MIN = 1

        @JvmStatic
        fun build(buf: ByteArray?): HeartRateDataModel? {
            if (null == buf) {
                return null
            }

            if (buf.size == DATA_LEN_MAX || buf.size == DATA_LEN_MIN) {
                return HeartRateDataModel(buf)
            }

            return null
        }
    }
}