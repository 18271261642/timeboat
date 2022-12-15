package net.sgztech.timeboat.netty

import io.netty.channel.ChannelHandlerContext

interface MsgListener {
    fun onMsgReceived(ctx : ChannelHandlerContext, msg: Any?)
}