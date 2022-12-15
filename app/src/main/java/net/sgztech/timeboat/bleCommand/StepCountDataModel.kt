package net.sgztech.timeboat.bleCommand

import org.apache.commons.lang3.Conversion

/**
 * Created by Jun on 2022/7/27 10:07 上午.
 */
class StepCountDataModel {
    // 步数, 4 byte
    var step: Int = 0

    // 距离, 4 byte
    var distance: Int = 0

    // 卡路里, 4 byte
    var calorie: Int = 0

    constructor()

    constructor(buf: ByteArray) : this() {
        if (buf.size >= DATA_LEN) {
            var index = 0

            step = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4

            distance = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4

            calorie = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
        }
    }

    override fun toString(): String {
        return "step = $step, distance = $distance, calorie = $calorie"
    }

    companion object {
        private const val DATA_LEN: Int = 12

        @JvmStatic
        fun build(buf: ByteArray?): StepCountDataModel? {
            if (null == buf) {
                return null
            }

            if (buf.size >= DATA_LEN) {
                return StepCountDataModel(buf)
            }

            return null
        }
    }
}