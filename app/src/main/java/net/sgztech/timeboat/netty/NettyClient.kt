package net.sgztech.timeboat.netty

import com.imlaidian.utilslibrary.utils.LogUtil
import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder
import io.netty.handler.timeout.IdleStateHandler
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.util.CharsetUtil
import net.sgztech.timeboat.managerUtlis.BleServiceManager
import net.sgztech.timeboat.managerUtlis.SettingInfoManager
import net.sgztech.timeboat.netty.NettyConstant.Client.READ_DATA_TIMEOUT_MILLISECOND
import java.lang.ref.WeakReference
import java.net.UnknownHostException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


abstract class NettyClient protected constructor() : MsgListener {
    private val TAG = "NettyClient"
    private var channel: Channel? = null
    private var bootstrap: Bootstrap? = null
    private var workGroup: EventLoopGroup? = null
    private var state: ChannelState
    private var macAddress: String? = null

    //是否中断了终端连接  如destroy 时
    var isTerminate = false
        private set
    private var isRestarting = false
    private val executor = Executors.newScheduledThreadPool(1)
    private var lastNettyStatus = ChannelState.CLOSED // 第一次默认为close状态
    private var lastNetDisconnectTime: Long = 0
    private var restartCount = 0
    var isAllowRestartNetty = true
        set(allowRestartNetty) {
            isRestarting = false
            field = allowRestartNetty
        }
    private var mListenWeakReference: WeakReference<onChannelStateChangeListen>? = null
    protected abstract val port: Int

    @Synchronized
    @Throws(Exception::class)
    private fun initClient() {
        LogUtil.e(TAG, "init start")
        macAddress = BleServiceManager.instance.getMacAddress()
        isTerminate = false
        ShutdowNettyClient.sharedInstance().addListener(shutdownListener)
        workGroup = NioEventLoopGroup()
        bootstrap = Bootstrap()
        bootstrap!!.group(workGroup)
        // 指定NIO方式
        // 指定是NioSocketChannel
        bootstrap!!.channel(NioSocketChannel::class.java)
        setBootstrapOption(bootstrap!!)
        setBootstrapHandler(bootstrap!!)
        LogUtil.e(TAG, "init over")
    }

    private fun start() {
        LogUtil.e(TAG, "NETTY_CLIENT_START $state")
        try {
            if (port <= 0) {
                throw Exception("LISTEN_PORT_ILLEGAL")
            }

            val future = bootstrap!!.connect(host, port).sync()
            future.addListener(channelStartListener)
            LogUtil.e(TAG, "NETTY_CLIENT_START listen state=$state")
            val closeFuture = future.channel().closeFuture()
            closeFuture.addListener(channelExceptionStopListener)
            closeFuture.sync()
        } catch (e: UnknownHostException) {
            restartCount = 0
            isRestarting = false
            e.printStackTrace()
            LogUtil.s(TAG, "NETTY_CLIENT 连接服务器异常  e =", e)
        } catch (e: Exception) {
            restartCount = 0
            isRestarting = false
            e.printStackTrace()
            LogUtil.e(TAG, "NETTY_CLIENT 连接服务器异常 网络不通 e =$e")
        } finally {
            if (lastNettyStatus != ChannelState.CLOSED
                && lastNettyStatus != ChannelState.DISCONNECT
            ) {
                LogUtil.e(TAG, "NETTY_CLIENT 连接服务器异常 网络不通")
                // 所有资源释放完成之后，清空资源，再次发起重连操作
                //不是机器断网的时候后 就要发起重连
                if (state != ChannelState.DISCONNECT) {
                    LogUtil.e(TAG, "NETTY_CLIENT state =$state")
                    if (isAllowRestartNetty) {
                        restartNettyConnectTask()
                    }
                }
                /// when the netty disconnect, ping tcp host.
            }
        }
    }

    @Synchronized
    fun restartNettyConnectTask() {
        LogUtil.d(TAG, "restartNettyConnectTask RESTARTING restartCount=$restartCount")
        if (restartCount >= 5) {
            LogUtil.d(TAG, "restartNettyConnectTask RESTARTING restartCount=$restartCount")
            isRestarting = false
        }
        LogUtil.d(TAG, "restartNettyConnectTask RESTARTING  start")
        if (isRestarting) {
            restartCount++
            LogUtil.d(TAG, "restartNettyConnectTask RESTARTING return")
            return
        }
        isRestarting = true
        LogUtil.d(TAG, "restartNettyConnectTask RESTARTING running")
        if (workGroup != null) {
            workGroup!!.shutdownGracefully()
        }
        channel = null
        bootstrap = null
        LogUtil.d(TAG, "restartNettyConnectTask init execute")
        executor.execute {
            try {
                TimeUnit.SECONDS.sleep(5)
            } catch (e: Exception) {
                setState(ChannelState.CLOSED)
                LogUtil.s(TAG, "restartNettyConnectTask sleep first:", e)
                e.printStackTrace()
            }
            try {
                LogUtil.d(TAG, "restartNettyConnectTask init start")
                if (isAllowRestartNetty) {
                    initClient()
                    start()
                }
                LogUtil.d(TAG, "restartNettyConnectTask Restart start  over")
            } catch (e: Exception) {
                setState(ChannelState.CLOSED)
                LogUtil.s(TAG, "restartNettyConnectTask error:", e)
                e.printStackTrace()
            }
        }
    }

    fun shutdownAndAwaitTermination(pool: ExecutorService) {
        // Disable new tasks from being submitted
        pool.shutdown()
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(20, TimeUnit.SECONDS)) {
                pool.shutdownNow() // Cancel currently executing tasks

                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(20, TimeUnit.SECONDS)) LogUtil.d(
                    TAG,
                    "Pool did not terminate"
                )
            }
        } catch (ie: InterruptedException) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow()
            // Preserve interrupt status
            Thread.currentThread().interrupt()
        }
    }

    fun connect() {
        LogUtil.d(TAG, "connect")
        Thread {
            LogUtil.d(TAG, "connect  start")
            start()
        }.start()
    }

    fun shutdown() {
        if (workGroup != null) {
            workGroup!!.shutdownGracefully()
        }
    }

    fun disconnect() {
        LogUtil.i(TAG, "NETTY_CLIENT disconnect  state =$state")
        if (state == ChannelState.CLOSED) {
            return
        }
        if (channel != null) {
            val future = channel!!.close()
            setState(ChannelState.CLOSING)
            future.addListener(channelStopListener)
        }
    }

    fun destroy() {
        isTerminate = true
        if (workGroup != null) {
            workGroup!!.shutdownGracefully()
        }
        LogUtil.e(TAG, "NETTY_CLIENT destroy WORKGROUP_SHUTDOWN_GRACEFULLY")
    }

    protected fun enableFrameDecoder(): Boolean {
        return false
    }

    protected fun setFrameDecoderConfig(config: GlobalConfig.FrameDecoder?) {}
    private fun setBootstrapHandler(bootstrap: Bootstrap) {
        bootstrap.handler(object : ChannelInitializer<Channel>() {
            @Throws(Exception::class)
            override fun initChannel(ch: Channel) {
//                if (enableFrameDecoder()) {
//                    val config = GlobalConfig.FrameDecoder()
//                    setFrameDecoderConfig(config)
//                    ch.pipeline().addLast(
//                        LengthFieldBasedFrameDecoder(
//                            Int.MAX_VALUE,
//                            config.offset, config.length,
//                            config.adjustment, 0
//                        )
//                    )
//                }

//                    //byte decode
//                    ch.pipeline().addLast(new DataPacketDecoder());
//                    ch.pipeline().addLast(new DataPacketEncoder());
                val decoder = StringDecoder(CharsetUtil.UTF_8)
                val encoder = StringEncoder(CharsetUtil.UTF_8)

                val idleStateHandler = IdleStateHandler(
                    SettingInfoManager.instance.heartBeatInterval,
                    0,
                    0,
                    TimeUnit.MILLISECONDS
                )

                val channelHandlerAdapter = NettyChannelHandlerAdapter()

                // string decode
                ch.pipeline().addLast("decoder", decoder);
                ch.pipeline().addLast("encoder", encoder);
                ch.pipeline().addLast("idleStateHandler", idleStateHandler);
                ch.pipeline().addLast("channelHandlerAdapter", channelHandlerAdapter);


//                //9秒后没有读取到任何消息就自动断开连接 ,抛出 exception
//                ch.pipeline().addLast(
//                    NettyConstant.READ_TIMEOUT_HANDLER,
//                    ReadTimeoutHandler(defaultHeartbeatTimeout())
//                )
            }


        })
    }

    private fun setBootstrapOption(bootstrap: Bootstrap) {
        bootstrap.option(ChannelOption.TCP_NODELAY, true)
        bootstrap.option(ChannelOption.SO_KEEPALIVE, optionSocketKeepAlive())
    }

    protected fun optionSocketKeepAlive(): Boolean {
        return true
    }

    private fun defaultReconnectIntervalTime(): Int {
        return NettyConstant.Client.AUTO_RECONNECT_INTERVAL_THRESSHOLD
    }

    protected fun defaultHeartbeatTimeout(): Int {
        return NettyConstant.Client.DEFAULT_HEARTBEAT_TIMEOUT_MINISECONDS
    }

    protected abstract val host: String?

    interface onChannelStateChangeListen {
        fun onChannelStateChanged(state: ChannelState?)
        fun onChannelStateClose()
    }

    private var channelStatusListen: onChannelStateChangeListen? = null
    fun setChannelStatusListen(mListen: onChannelStateChangeListen) {
        mListenWeakReference = WeakReference(mListen)
        channelStatusListen = mListenWeakReference!!.get()
    }

    fun unChannelStatusListen() {
        if (mListenWeakReference != null) {
            mListenWeakReference!!.clear()
            channelStatusListen = null
            mListenWeakReference = null
        }
    }

    fun setState(state: ChannelState) {
        LogUtil.d(TAG, "setState state =$state")
        this.state = state
        if (channelStatusListen != null) {
            channelStatusListen!!.onChannelStateChanged(state)
        }
        restartMaChineByNettyStatus(state)
    }

    fun getState(): ChannelState {
        return state
    }

    val isNettyClose: Boolean
        get() = state != ChannelState.CONNECTING

    //监听channel通道是否应打开，初始化状态
    private val channelStartListener = ChannelFutureListener { future ->
        if (future.isSuccess) {
            channel = future.channel()
            LogUtil.d(TAG, "SOCKET连接开启  running")
            setState(ChannelState.CONNECTING)
        } else {
            LogUtil.d(TAG, "SOCKET连接错误")
            setState(ChannelState.CLOSED)
            channel = null
        }
        restartCount = 0
        isRestarting = false
    }
    private val channelStopListener = ChannelFutureListener {
        setState(ChannelState.CLOSED)
        LogUtil.d(TAG, " Disconnect channelStopListener CHANNEL_CLOSED")
    }
    private val channelExceptionStopListener = ChannelFutureListener {
        LogUtil.d(TAG, "channelExceptionStopListener CHANNEL_CLOSED")
        setState(ChannelState.CLOSED)
    }
    private val shutdownListener = ShutdownListener {
        LogUtil.i(TAG, "NETTY_CLIENT  shutdownListener disconnect")
        disconnect()
        destroy()
    }

    private fun getSessionByType(type: ChannelHandlerType): Session? {
        if (channel != null) {
            var o: Any? = null
            o = when (type) {
                ChannelHandlerType.loginChannel ->                         // 获取登录的channelHandler 的 session属性
                    channel!!.attr(NettyConstant.Key.login).get()
                ChannelHandlerType.heartBeatChannel ->                         //获取心跳包的channelHandler的session属性 属性;
                    channel!!.attr(NettyConstant.Key.heartBeat).get()
                ChannelHandlerType.bleCmdChannel ->                         //获取心跳包的channelHandler的session属性 属性;
                    channel!!.attr(NettyConstant.Key.bleCmd).get()
                else -> {
                    LogUtil.i(
                        TAG,
                        "getSessionByType ChannelHandlerType=$type, go default"
                    )
                    null
                }
            }
            if (o == null) {
                LogUtil.i(
                    TAG, "getSessionByType ChannelHandlerType=$type, get Login channel content"
                )
                o = channel!!.attr(NettyConstant.Key.bleCmd).get()
            }
            if (o != null && o is Session) {
                return o
            }
        }
        LogUtil.i(TAG, "getSessionByType ChannelHandlerType=$type, channel =$channel")
        return null
    }

    fun write(pkg: Any, type: ChannelHandlerType) {
        val session = getSessionByType(type)
        if (session == null) {
            setState(ChannelState.CLOSED)
            return
        }
        LogUtil.i(TAG, "write session pkg=" + pkg + "ChannelHandlerType=" + type)
        session.write(pkg)
        setState(ChannelState.CONNECTING)
    }

    private fun restartMaChineByNettyStatus(status: ChannelState) {
        if (status == ChannelState.CLOSED || status == ChannelState.DISCONNECT) {
            if (lastNettyStatus != ChannelState.CLOSED
                && lastNettyStatus != ChannelState.DISCONNECT
            ) {
                lastNettyStatus = status
                lastNetDisconnectTime = System.currentTimeMillis()
            }
        } else {
            lastNettyStatus = status
        }
    }

    /*
     * 判断是否重启链接服务器，
     * 如果重启多次了，应该将以前重启打开的线程在 线程池中关掉，
     * 否则多次重启会导致，线程过多，导致系统资源不足
     */
    init {
        LogUtil.e(TAG, "NettyClient 11")
        LogUtil.e(TAG, "NettyClient 22")
        lastNetDisconnectTime = System.currentTimeMillis()
        state = ChannelState.CLOSED
        try {
            LogUtil.e(TAG, "NettyClient init")
            initClient()
        } catch (e: Exception) {
            LogUtil.e(TAG, "Netty初始化失败,重新创建Netty")
            try {
                disconnect()
                destroy()
                Thread.sleep(3000)
                initClient()
            } catch (o: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}