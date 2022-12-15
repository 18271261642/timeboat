package net.sgztech.timeboat.bleCommand

import net.sgztech.timeboat.util.toHex

/**
 * Created by Jun on 2022/7/27 1:40 上午.
 */
class BLECMDItem {
    var header: Byte = 0
    var cmdType: Byte = 0
    var dataLen: Int = 0
    var timestampSecond: Long = 0
    var data: ByteArray? = null

    constructor()

    constructor(header: Byte, cmdType: Byte, dataLen: Int, timestampSecond: Long, data: ByteArray?) : this() {
        this.header = header
        this.cmdType = cmdType
        this.dataLen = dataLen
        this.timestampSecond = timestampSecond
        this.data = data
    }

    override fun toString(): String {
        return String.format("timestampSecond = %d, header = %02X, " +
                "cmdType = %02X, dataLen = %02X, data = %s",
            timestampSecond, header,
            cmdType, dataLen, data?.toHex())
    }
}