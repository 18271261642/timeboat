package net.sgztech.timeboat.bleCommand

import org.apache.commons.lang3.Conversion
import java.text.DecimalFormat

/**
 * Created by Jun on 2022/7/27 11:32 上午.
 */
class BodyTemperatureDataModel {
    // 体温, 2 byte
    var bodyTempInt: Short = 0
    var bodyTempStr: String = ""

    constructor(buf: ByteArray) : this() {
        if (buf.size >= DATA_LEN) {
            val temp = Conversion.byteArrayToShort(buf, 0, 0, 0, 2)
            val t: Double = temp / 10.0;
            val df = DecimalFormat("#.0")
            val str = df.format(t)

            this.bodyTempInt = temp
            this.bodyTempStr = str
        }
    }

    constructor()

    override fun toString(): String {
        return "bodyTempInt = $bodyTempInt, bodyTempStr = $bodyTempStr"
    }

    companion object {
        private const val DATA_LEN = 2

        @JvmStatic
        fun build(buf: ByteArray?): BodyTemperatureDataModel? {
            if (null == buf) {
                return null
            }

            if (buf.size >= DATA_LEN) {
                return BodyTemperatureDataModel(buf)
            }

            return null
        }
    }
}