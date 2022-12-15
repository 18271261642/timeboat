package net.sgztech.timeboat.netty

import com.imlaidian.utilslibrary.utils.LogUtil
import io.netty.channel.ChannelHandlerContext
import net.sgztech.timeboat.ui.utils.UIUtils.isNetworkAvailable

/**
 * Created by zbo on 16/7/14.
 */
class MachineNettyClient : NettyClient() {
    private val TAG = "MachineNettyClient"
    override val host: String
        get() = NettyConstant.getTcpHost()
    // exception port 置顶为 10100;
    override val port: Int
        get() = try {
            NettyConstant.getTcpPort().toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.s(TAG, "TcpPort error", e)

            // exception port 置顶为 10100 ;
            NettyConstant.getTcpPort().toInt()
        }

    override fun onMsgReceived(ctx: ChannelHandlerContext, msg: Any?) {

    }

    @Synchronized
    fun sendAndReceiveCommand(
        obj: Any,
        receivedListen: ReceiveCommandListen
    ) {
        if (getState() === ChannelState.CONNECTING || getState() === ChannelState.FETCHING) {
            NettyChannelMessageManager.getInstance()
                .setReceivedCommandFromServerListen(receivedListen)
            sendBleCommand(obj)
        } else if (getState() === ChannelState.CLOSED) {
            restartNettyConnectTask()
            LogUtil.d(TAG, "netty is closed ， canot send command ,restart netty ")
        } else if (getState() === ChannelState.CLOSING) {
            LogUtil.d(TAG, "netty is closing can not send command")
        } else if (getState() === ChannelState.DISCONNECT) {
            LogUtil.d(TAG, "net is unlink , netty is disconnect  can not send command ")
        }
    }

    //暂时不用 disconnect 需要状态实时更新
    fun getNetStatus(){
        if(!isNetworkAvailable()){
            setState(ChannelState.DISCONNECT)
        }
    }

    // 设备 -> 服务器,
    fun sendBleCommand(obj: Any?) {
        if (getState() === ChannelState.CONNECTING || getState() === ChannelState.FETCHING) {
            writeCommand(obj)
        } else if (getState() === ChannelState.CLOSED) {
            restartNettyConnectTask()
            LogUtil.d(TAG, "netty is closed ， canot send command ,restart netty ")
        } else if (getState() === ChannelState.CLOSING) {
            LogUtil.d(TAG, "netty is closing can not send command")
        } else if (getState() === ChannelState.DISCONNECT) {
            LogUtil.d(TAG, "net is unlink , netty is disconnect  can not send command ")
        }
    }

    fun writeCommand(obj: Any?){
        if (null != obj && obj is String) {
            write(obj, ChannelHandlerType.bleCmdChannel)
        } else {
            LogUtil.d(TAG, "Send borrow command, the class of OBJ isn't MachineTaskModel")
        }
    }

    companion object {
        val instance: MachineNettyClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            MachineNettyClient()
        }
    }
}