package com.device.ui.baseUi.baseActivity


import android.app.Activity
import android.content.Context
import com.device.ui.baseUi.interfaces.I_BaseActivity
import java.util.*

/**
 * Created by zbo on 16/6/7.
 */
/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出<br></br>
 */
class ActivityStack private constructor() {
    /**
     * 获取当前Activity栈中元素个数
     */
    val count: Int
        get() = activityStack!!.size

    /**
     * 添加Activity到栈
     */
    fun addActivity(activity: I_BaseActivity?) {
        if (activityStack == null) {
            activityStack = Stack()
        }
        activityStack!!.add(activity)
    }

    /**
     * 获取当前Activity（栈顶Activity）
     */
    fun topActivity(): Activity? {
        if (activityStack == null) {
            throw NullPointerException(
                    "Activity stack is Null,your Activity must extend BaseActivity")
        }
        if (activityStack!!.isEmpty()) {
            return null
        }
        val activity = activityStack!!.lastElement()
        return activity as Activity?
    }

    /**
     * 获取当前Activity（栈顶Activity） 没有找到则返回null
     */
    fun findActivity(cls: Class<*>): Activity? {
        var activity: I_BaseActivity? = null
        for (aty in activityStack!!) {
            if (aty!!.javaClass == cls) {
                activity = aty
                break
            }
        }
        return activity as Activity?
    }

    /**
     * 结束当前Activity（栈顶Activity）
     */
    fun finishActivity() {
        val activity = activityStack!!.lastElement()
        finishActivity(activity as Activity?)
    }

    /**
     * 结束指定的Activity(重载)
     */
    fun finishActivity(activity: Activity?) {
        var activity = activity
        if (activity != null && activityStack !=null) {
            activityStack!!.removeElement(activity)
            // activity.finish();//此处不用finish
            activity = null
        }
    }

    /**
     * 结束指定的Activity(重载)
     */
    fun finishActivity(cls: Class<*>) {
        if (activityStack != null) {
            for (activity in activityStack!!) {
                if (activity!!.javaClass == cls) {
                    finishActivity(activity as Activity?)
                }
            }
        }
    }

    /**
     * 关闭除了指定activity以外的全部activity 如果cls不存在于栈中，则栈全部清空
     *
     * @param cls
     */
    fun finishOthersActivity(cls: Class<*>) {
        if(activityStack !=null) {
            for (activity in activityStack!!) {
                if (activity!!.javaClass != cls) {
                    finishActivity(activity as Activity?)
                }
            }
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        var i = 0
        if(activityStack !=null){
            val size = activityStack!!.size
            while (i < size) {
                if (null != activityStack!![i]) {
                    (activityStack!![i] as Activity?)!!.finish()
                }
                i++
            }
            activityStack!!.clear()
        }
    }

    @Deprecated("")
    fun AppExit(cxt: Context?) {
        appExit(cxt)
    }

    /**
     * 应用程序退出
     */
    fun appExit(context: Context?) {
        try {
            finishAllActivity()
            Runtime.getRuntime().exit(0)
        } catch (e: Exception) {
            Runtime.getRuntime().exit(-1)
        }
    }

    companion object {
        private var activityStack: Stack<I_BaseActivity?>? = null
        private val instance = ActivityStack()
        @JvmStatic
        fun create(): ActivityStack {
            return instance
        }
    }
}