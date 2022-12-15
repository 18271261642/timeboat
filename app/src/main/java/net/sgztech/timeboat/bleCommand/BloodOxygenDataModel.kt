package net.sgztech.timeboat.bleCommand

import org.apache.commons.lang3.Conversion
import kotlin.experimental.and

/**
 * Created by Jun on 2022/7/27 10:21 上午.
 */
class BloodOxygenDataModel {
    // 血氧, 4 byte
    var bloodOxygen: Int = 0

    constructor(buf: ByteArray) : this() {
        if (buf.size == DATA_LEN_MIN) {
            bloodOxygen = (buf[0] and 0xff.toByte()).toInt()
        }
    }

    constructor()

    override fun toString(): String {
        return String.format("bloodOxygen = %d", bloodOxygen)
    }

    companion object {
        private const val DATA_LEN_MIN = 1

        @JvmStatic
        fun build(buf: ByteArray?): BloodOxygenDataModel? {
            if (null == buf) {
                return null
            }

            if (buf.size == DATA_LEN_MIN) {
                return BloodOxygenDataModel(buf)
            }

            return null
        }
    }
}