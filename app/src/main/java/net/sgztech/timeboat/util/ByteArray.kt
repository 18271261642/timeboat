package net.sgztech.timeboat.util

fun ByteArray.toHex() = joinToString(" ") {
    String.format("%02X", (it.toInt() and 0xff))
}
