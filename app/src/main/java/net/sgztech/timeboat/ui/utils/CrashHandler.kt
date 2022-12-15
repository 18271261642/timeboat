package net.sgztech.timeboat.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import kotlin.Throws
import com.imlaidian.utilslibrary.utils.SystemTool
import android.content.pm.PackageManager
import android.os.Build
import net.sgztech.timeboat.provide.dataModel.PhoneCrashInfo
import com.alibaba.fastjson.JSON
import com.imlaidian.utilslibrary.utils.FileUtils
import net.sgztech.timeboat.config.Constants
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.PrintWriter
import java.lang.Exception

class CrashHandler private constructor(cxt: Context) : Thread.UncaughtExceptionHandler {
    private val mContext: Context

    /**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
     */
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        // 导出异常信息到SD卡中
        try {
            ex.printStackTrace()
            saveToSDCard(ex)
        } catch (e: Exception) {
            //ViewInject.toast(e.getMessage());
        } finally {
            // ex.printStackTrace();// 调试时打印日志信息
            System.exit(0)
        }
    }

    @Throws(Exception::class)
    private fun saveToSDCard(ex: Throwable) {
        val file = FileUtils.getSaveCacheFile(Constants.SAVE_FOLDER, FILE_NAME_SUFFIX)
        var append = false
        if (System.currentTimeMillis() - file.lastModified() > 5000) {
            append = true
        }
        val pw = PrintWriter(BufferedWriter(FileWriter(file, append)))
        // 导出发生异常的时间
        pw.println(SystemTool.getDataTime("yyyy-MM-dd-HH-mm-ss"))
        // 导出手机信息
        dumpPhoneInfo(pw, ex)
        pw.println()
        // 导出异常的调用栈信息
        ex.printStackTrace(pw)
        pw.println()
        pw.close()
    }

    @Throws(PackageManager.NameNotFoundException::class)
    private fun dumpPhoneInfo(pw: PrintWriter, e: Throwable) {
        // 应用的版本名称和版本号
        val pm = mContext.packageManager
        val pi = pm.getPackageInfo(mContext.packageName, PackageManager.GET_ACTIVITIES)
        pw.print("App Version: ")
        pw.print(pi.versionName)
        pw.print('_')
        pw.println(pi.versionCode)
        pw.println()

        // android版本号
        pw.print("OS Version: ")
        pw.print(Build.VERSION.RELEASE)
        pw.print("_")
        pw.println(Build.VERSION.SDK_INT)
        pw.println()

        // 手机制造商
        pw.print("Vendor: ")
        pw.println(Build.MANUFACTURER)
        pw.println()

        // 手机型号
        pw.print("Model: ")
        pw.println(Build.MODEL)
        pw.println()

        // cpu架构
        pw.print("CPU ABI: ")
        pw.println(Build.CPU_ABI)
        pw.println()
        val mExceptionInfo = PhoneCrashInfo()
        mExceptionInfo.cpuAbi = "" + Build.CPU_ABI
        mExceptionInfo.model = "" + Build.MODEL
        mExceptionInfo.manufacture = Build.MANUFACTURER
        mExceptionInfo.osVersion = Build.VERSION.RELEASE
        mExceptionInfo.sdkVersion = "" + Build.VERSION.SDK_INT
        mExceptionInfo.versionCode = "" + pi.versionCode
        mExceptionInfo.versionName = pi.versionName
        mExceptionInfo.exception = e.toString()
        val mInfo = JSON.toJSONString(mExceptionInfo)
    }

    companion object {
        // log文件的后缀名
        const val FILE_NAME_SUFFIX = "sgz.log"
        @SuppressLint("StaticFieldLeak")
        private var sInstance: CrashHandler? = null
        @Synchronized
        fun create(cxt: Context): CrashHandler? {
            if (sInstance == null) {
                sInstance = CrashHandler(cxt)
            }
            return sInstance
        }
    }

    init {
        // 将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
        // 获取Context，方便内部使用
        mContext = cxt
    }
}