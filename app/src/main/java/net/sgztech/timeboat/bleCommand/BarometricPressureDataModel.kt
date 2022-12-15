package net.sgztech.timeboat.bleCommand

import org.apache.commons.lang3.Conversion

// 气压检测
class BarometricPressureDataModel {
    // 气压测量
    var barometricMeasurement: Int = 0

    // 高度
    var height = 0

    constructor()

    constructor(packageData: ByteArray) : this() {
        if (packageData.size >= 8) {
            barometricMeasurement = Conversion.byteArrayToInt(packageData, 0, 0, 0, 4)
            height = Conversion.byteArrayToInt(packageData, 4, 0, 0, 4)
        }
    }

    override fun toString(): String {
        return String.format("barometricMeasurement = %d, height = %d", barometricMeasurement, height)
    }

    companion object {
        @JvmStatic
        fun build(buf: ByteArray?) : BarometricPressureDataModel? {
            if (null == buf) {
                return null
            }

            return BarometricPressureDataModel(buf)
        }
    }
}