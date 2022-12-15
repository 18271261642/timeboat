package net.sgztech.timeboat.netty

import io.netty.util.AttributeKey
import net.sgztech.timeboat.config.Constants.Companion.isDebugEnv
import net.sgztech.timeboat.config.Constants.Companion.isReleaseEnv
import net.sgztech.timeboat.managerUtlis.SettingInfoManager

object NettyConstant {
    const val READ_TIMEOUT_HANDLER = "readTimeoutHandler"
    const val LOGIN_AUTH_HANDLER = "LoginAuthHandler"
    const val HEART_BEAT_HANDLER = "HeartBeatHandler"

    //Tcp
    const val REMOTE_TCP_RELEASE = "app-tcp-pro.sgztech.net"
    const val REMOTE_TCP_PRE_RELEASE = "app-tcp-pro.sgztech.net"
    const val REMOTE_TCP_DEBUG = "app-tcp-pro.sgztech.net"

    //PORT
    const val REMOTE_TCP_PORT_RELEASE = "10100"
    const val REMOTE_TCP_PORT_TEST = "10100"

    object Key {
        val login = AttributeKey.valueOf<Session>("login")
        val heartBeat = AttributeKey.valueOf<Session>("heartBeat")

        @JvmField
        val bleCmd = AttributeKey.valueOf<Session>("cmd")
    }

    object Client {
        const val AUTO_RECONNECT_INTERVAL_THRESSHOLD = 6000
        const val DEFAULT_HEARTBEAT_TIMEOUT_MINISECONDS = 9
        const val READ_DATA_TIMEOUT_MILLISECOND = 30_000L
    }

    fun getTcpHost(): String {
        return if (isReleaseEnv()) {
            SettingInfoManager.instance.tcpHost.ifEmpty {
                REMOTE_TCP_RELEASE;
            }
        } else if (isDebugEnv()) {
            REMOTE_TCP_DEBUG
        } else {
            REMOTE_TCP_RELEASE;
        }
    }


    fun getTcpPort(): String {
        return if (isReleaseEnv()) {
            SettingInfoManager.instance.tcpPort.ifEmpty {
                REMOTE_TCP_PORT_RELEASE;
            }
        } else if (isDebugEnv()) {
            REMOTE_TCP_PORT_TEST
        } else {
            REMOTE_TCP_PORT_RELEASE;
        }
    }
}