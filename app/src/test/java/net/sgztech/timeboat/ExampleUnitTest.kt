package com.glx.watch

import com.imlaidian.utilslibrary.utils.LogUtil
import net.sgztech.timeboat.bleCommand.BLECMDParseUtil
import net.sgztech.timeboat.bleCommand.RunDataModel
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun runDataTest() {
        val array: ByteArray = ByteArray(200)
        var offset = 0
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0xDD).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x5F).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x86).toByte()
        array[offset++] = (0x24).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x04).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x5A).toByte()
        array[offset++] = (0x59).toByte()
        array[offset++] = (0x52).toByte()
        array[offset++] = (0x53).toByte()
        array[offset++] = (0x04).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x02).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0xAD).toByte()
        array[offset++] = (0x01).toByte()
        array[offset++] = (0xAD).toByte()
        array[offset++] = (0x01).toByte()
        array[offset++] = (0x02).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x77).toByte()
        array[offset++] = (0x77).toByte()
        array[offset++] = (0x03).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0xA1).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0xA5).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0xA4).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x04).toByte()
        array[offset++] = (0x01).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()

        var data: ByteArray = array.copyOfRange(0, offset)
        val model = RunDataModel.build(data)
        LogUtil.d("test", "model = $model")
    }

    @Test
    fun parseBLECommand() {
        var offset = 0
        val array: ByteArray = ByteArray(100)
        array[offset++] = (0xAA).toByte()
        array[offset++] = (0x14).toByte()
        array[offset++] = (0x16).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x35).toByte()
        array[offset++] = (0xCC).toByte()
        array[offset++] = (0xDE).toByte()
        array[offset++] = (0x62).toByte()
        array[offset++] = (0x1E).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x37).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x4E).toByte()
        array[offset++] = (0x07).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()

        var cmd: ByteArray = array.copyOfRange(0, offset)
        var list = BLECMDParseUtil.parse(cmd)
        if (null != list) {
            offset = 0;
        }

        offset = 0
        array[offset++] = (0x02).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x6E).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x6E).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x02).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x62).toByte()
        array[offset++] = (0x62).toByte()
        array[offset++] = (0xAA).toByte()
        array[offset++] = (0x19).toByte()
        array[offset++] = (0x0C).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x10).toByte()
        array[offset++] = (0xCC).toByte()
        array[offset++] = (0xDE).toByte()
        array[offset++] = (0x62).toByte()

        cmd = array.copyOfRange(0, offset)
        list = BLECMDParseUtil.parse(cmd)
        if (null != list) {
            offset = 0;
        }

        offset = 0
        array[offset++] = (0x74).toByte()
        array[offset++] = (0x01).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x1C).toByte()
        array[offset++] = (0x01).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x76).toByte()
        array[offset++] = (0x34).toByte()
        array[offset++] = (0x00).toByte()
        array[offset++] = (0x00).toByte()

        cmd = array.copyOfRange(0, offset)
        list = BLECMDParseUtil.parse(cmd)
        if (null != list) {
            offset = 0;
        }
    }
}