package com.device.ui.baseUi.interfaces

import android.view.View

/**
 * Created by zbo on 16/6/7.
 * KJFrameActivity接口协议，实现此接口可使用KJActivityManager堆栈<br></br>
 */
interface I_BaseActivity {
    /**
     * livedata observe
     */

    fun startObserve()
    /**
     * 初始化数据
     */
    fun initData()

    /**
     * 在线程中初始化数据
     */
    fun initDataFromThread()

    /**
     * 初始化控件
     */
    fun initWidget()

    /**
     * 点击事件回调方法
     */
    fun widgetClick(v: View)

    companion object {
        const val DESTROY = 0
        const val STOP = 2
        const val PAUSE = 1
        const val RESUME = 3
        const val CREATE = 4
    }
}