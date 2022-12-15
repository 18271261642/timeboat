package net.sgztech.timeboat.bleCommand

import org.apache.commons.lang3.Conversion

class TotalStepsDataModel {
    // 总步数, 4 byte
    var totalStep: Int = 0

    // 总距离, 4 byte
    var totalDistance: Int = 0

    // 总卡路里, 4 byte
    var totalCalorie: Int = 0

    constructor()

    constructor(buf: ByteArray) : this() {
        if (buf.size >= 12) {
            var index = 0

            totalStep = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4

            totalDistance = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4

            totalCalorie = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
        }
    }

    override fun toString(): String {
        return "totalStep = $totalStep, totalDistance = $totalDistance, totalCalorie = $totalCalorie"
    }

    companion object {
        @JvmStatic
        fun build(buf: ByteArray?) : TotalStepsDataModel? {
            if (null == buf) {
                return null
            }

            return TotalStepsDataModel(buf)
        }
    }
}