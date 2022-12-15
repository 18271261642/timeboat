package net.sgztech.timeboat.bleCommand

import org.apache.commons.lang3.Conversion

class SOSDataModel {
    // 是否携带位置 1Byte 0 - 不携带gps 位置；
    var carryingPosition: Byte = 0

    // gps 经度 4Byte
    var lng: Int = 0

    // gps 纬度 4Byte
    var lat: Int = 0

    constructor()

    constructor(buf: ByteArray) : this() {
        var index = 0

        if (index + 1 <= buf.size) {
            // 是否携带位置 1Byte 0 - 不携带gps 位置；
            carryingPosition = buf[index]
            index += 1
        }

        if (index + 4 <= buf.size) {
            //Gps 经度 4Byte
            lng = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
            index += 4
        }

        if (index + 4 <= buf.size) {
            // Gps 纬度 4Byte
            lat = Conversion.byteArrayToInt(buf, index, 0, 0, 4)
        }
    }

    override fun toString(): String {
        return "carryingPosition = $carryingPosition, lng = $lng, lat = $lat"
    }

    companion object {
        @JvmStatic
        fun build(buf: ByteArray?) : SOSDataModel? {
            if (null == buf) {
                return null
            }

            return SOSDataModel(buf)
        }
    }
}