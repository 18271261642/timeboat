package net.sgztech.timeboat.bleCommand

class SleepDataModel {
    inner class SleepItem(h: Int, m: Int, s: Int) {
        var hour: Int = h
        var minute: Int = m
        var state: Int = s

        override fun toString(): String {
            return "hour = $hour, minute = $minute, state = $state"
        }
    }

    lateinit var sleepDataList: List<SleepItem>

    constructor()

    constructor(buf: ByteArray, dataLength: Int) : this() {
        val data: ArrayList<SleepItem> = ArrayList()

        if (buf.size >= dataLength) {
            val unit = 3
            val arrayCount = dataLength / unit


            var h = 0
            var m = 0
            var s = 0
            for (index in 0 until arrayCount) {
                h = buf[index * unit].toInt() and 0xff
                m = buf[index * unit + 1].toInt() and 0xff
                s = buf[index * unit + 2].toInt() and 0xff
                data.add(SleepItem(h, m, s))
            }
        }

        sleepDataList = data
    }

    override fun toString(): String {
        return String.format("sleepDataList = %s", sleepDataList.toString())
    }

    companion object {
        @JvmStatic
        fun build(buf: ByteArray?): SleepDataModel? {
            if (null == buf) {
                return null
            }

            return SleepDataModel(buf, buf.size)
        }
    }
}