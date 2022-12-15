package net.sgztech.timeboat.netty

import io.netty.handler.codec.MessageToByteEncoder
import net.sgztech.timeboat.netty.DataPacket
import kotlin.Throws
import io.netty.channel.ChannelHandlerContext
import io.netty.buffer.ByteBuf
import net.sgztech.timeboat.netty.Protocal
import java.lang.Exception

class DataPacketEncoder : MessageToByteEncoder<DataPacket>() {
    @Throws(Exception::class)
    override fun encode(ctx: ChannelHandlerContext, pkg: DataPacket, buf: ByteBuf) {
        fillBuf(pkg, buf)
        ctx.flush()
    }

    private fun fillBuf(pkg: DataPacket, buf: ByteBuf) {

        // 头部开始
        // 数据总长度
        if (pkg.size == 0) {
            val content = pkg.content
            if (content != null && content.size > 0) {
                pkg.size = pkg.content.size + Protocal.HEADER_LENGTH
            } else {
                pkg.size = Protocal.HEADER_LENGTH
            }
        }
        buf.writeInt(pkg.size)

        // 版本
        //byte[] version = ByteUtil.toByteArray(pkg.getVersion(), Protocal.FIELD_VERSION_LENGTH);
        buf.writeBytes(pkg.version)

        // 消息id
        buf.writeInt(pkg.id)

        // 消息类型
        //byte[] type = ByteUtil.toByteArray(pkg.getType(), Protocal.FIELD_TYPE_LENGTH);
        buf.writeBytes(pkg.type)

        // 通用字段
        val common = pkg.common
        if (common != null && common.size == Protocal.FIELD_COMMON_LENGTH) {
            buf.writeBytes(common)
        } else {
            buf.writeBytes(ByteArray(Protocal.FIELD_COMMON_LENGTH))
        }
        // 头部结束

        // body内容
        val content = pkg.content
        if (content != null && content.size > 0) {
            buf.writeBytes(content)
        }
    }
}