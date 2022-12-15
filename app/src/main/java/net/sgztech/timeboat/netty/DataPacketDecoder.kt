package net.sgztech.timeboat.netty

import android.util.Log
import io.netty.handler.codec.ByteToMessageDecoder
import net.sgztech.timeboat.netty.DataPacketDecoder
import kotlin.Throws
import io.netty.channel.ChannelHandlerContext
import io.netty.buffer.ByteBuf
import net.sgztech.timeboat.netty.Protocal
import net.sgztech.timeboat.netty.DataPacket
import java.lang.Exception

class DataPacketDecoder : ByteToMessageDecoder() {
    var TAG = DataPacketDecoder::class.java.simpleName
    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if (buf.readableBytes() < Protocal.HEADER_LENGTH) {
            Log.d(TAG, "READABLE_BYTES_ERROR $buf")
            return
        }
        val pkg = DataPacket()
        fillPacket(pkg, buf)
        out.add(pkg)
    }

    private fun fillPacket(pkg: DataPacket, buf: ByteBuf) {

        // 头部开始
        // 数据总长度
        pkg.size = buf.readInt()
        // 版本
        val version = ByteArray(Protocal.FIELD_VERSION_LENGTH)
        buf.readBytes(version)
        pkg.version = version

        // 消息id
        pkg.id = buf.readInt()

        // 消息类型
        val type = ByteArray(Protocal.FIELD_TYPE_LENGTH)
        buf.readBytes(type)
        pkg.type = type

        // 通用字段
        val common = ByteArray(Protocal.FIELD_COMMON_LENGTH)
        buf.readBytes(common)
        pkg.common = common
        // 头部结束

        // body内容
        val content = ByteArray(pkg.size - Protocal.HEADER_LENGTH)
        buf.readBytes(content)
        pkg.content = content
    }
}