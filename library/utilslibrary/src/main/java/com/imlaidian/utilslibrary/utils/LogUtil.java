package com.imlaidian.utilslibrary.utils;


import android.os.Environment;
import android.util.Log;

import com.imlaidian.ldclog.LdLog;
import com.imlaidian.ldclog.LdLogConfig;
import com.imlaidian.ldclog.LdUploadItem;
import com.imlaidian.ldclog.OnLdLogProtocolStatus;
import com.imlaidian.ldclog.SendLogRunnable;
import com.imlaidian.utilslibrary.BuildConfig;
import com.imlaidian.utilslibrary.UtilsApplication;
import com.imlaidian.utilslibrary.config.PublicConstant;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * 日志
 */
public class LogUtil {
    public static boolean debug = BuildConfig.DEBUG;;
    private static LogUtil mInstance = null;
    private static boolean isOpen =false ;
    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private final String TAG = LogUtil.class.getSimpleName();
    private final String[] whiteList = {
            PublicConstant.SAVE_SERIAL_FILE_NAME,
            PublicConstant.SAVE_START_APP_LOG_FILE_NAME
    };
    /// 单例
    public static LogUtil getInstance() {
        if (null == mInstance) {
            synchronized (LogUtil.class) {
                if (null == mInstance) {
                    mInstance = new LogUtil();
                }
            }
        }

        return mInstance;
    }

    public LogUtil() {
        if(isOpen){
            initLog();
        }

    }


    private void initLog(){

        LdLogConfig config = new LdLogConfig.Builder()
                .setCachePath(UtilsApplication.getInstance().getApplicationContext().getFilesDir().getAbsolutePath())
                .setPath(getLogPath())
                .setWhitePath(whiteList)
                .build();
        LdLog.init(config);
        LdLog.setDebug(debug);
        LdLog.setOnLdLogProtocolStatus(new OnLdLogProtocolStatus() {
            @Override
            public void logProtocolStatus(String cmd, int code) {
                if(debug){
                    Log.d(TAG, "clog > cmd : " + cmd + " | " + "code : " + code);
                }
            }
        });
    }

    private String getRootPath() {
        if (MainBoardVersionTools.isLaidianPlatform()) {
            List<String> list = SystemTool.getExtSDCardPath();
            if (list.size() > 0) {
                return list.get(0);
            }

            return null;
        }

        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    private String getLogPath() {
        String rootPath = getRootPath();
        if (null != rootPath) {
            return rootPath + PublicConstant.SAVE_SD_LOG_FILE_PATH;
        }
        return null;
    }

    public static void i(String tag, String msg) {
        getInstance().info(tag, msg);
    }

    public static void v(String tag, String msg) {
        getInstance().verbose(tag, msg);
    }

    public static void d(String tag, String msg) {
        getInstance().debug(tag, msg);
    }

    public static void e(String tag, String msg) {
        getInstance().error(tag, msg);
    }

    public static void w(String tag, String msg) {
        getInstance().warning(tag, msg);
    }

    public static void s(String tag, String describe,Exception e){
        getInstance().getStackTraceString( tag, describe , e);
    }

    public static void t(String tag, String describe ,Throwable tr){
        getInstance().getStackTraceThrowString( tag,  describe ,tr);
    }

    public static  void upload(List<LdUploadItem> fileList , SendLogRunnable runnable){
        getInstance().uploadFile(fileList, runnable);
    }

    /**
     * 停止日志输入到文件
     */
    public void stopLogToFile() {
        getInstance().stopLog();
    }

    public void getStackTraceThrowString(String tag , String describle ,Throwable tr) {
        String message = getExceptionThrowableString(tr);
        if (debug) {
            Log.e(tag, "Throwable Exception: " + describle + message);
        }
        if(isOpen){
            LdLog.e(tag , message);
        }

    }

    private  String getExceptionThrowableString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    private String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();

        if (sts == null) {
            return null;
        }

        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }

            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }

            if (st.getClassName().equals(this.getClass().getName())) {
                continue;
            }

            return "[" + mDateFormat.format(new Date()) + " "
                    + Thread.currentThread().getName() + "("
                    + Thread.currentThread().getId() + "): " + st.getFileName()
                    + ":" + st.getLineNumber() + "]";
        }

        return null;
    }

    private String getDateAndThreadTag() {
        return "[" + mDateFormat.format(new Date()) + " "
                + Thread.currentThread().getName() + "]";
    }

    private String createMessage(String tag, String msg) {
        String dateTagInfo = getDateAndThreadTag();//getFunctionName();
        return tag + " " + dateTagInfo + " - " + msg + "\n";
    }


    private void report(String tag, String msg,String  eventType){

        if (debug) {
            String message = createMessage(tag, msg);
            Log.i(tag, eventType +"\n"+ msg );
        }
        if(isOpen) {
            LdLog.r(tag, msg, eventType);
        }
    }

    private void uploadFile(List<LdUploadItem> fileList, SendLogRunnable runnable){
        if(isOpen) {
            LdLog.upload(fileList, runnable);
        }

    }

    private void stopLog(){
        if(isOpen) {
            LdLog.stop();
        }

    }

    private void info(String tag, String msg) {

        if (debug) {
            String message = createMessage(tag, msg);
            Log.i(tag, message);
        }
        if(isOpen) {
            LdLog.d(tag, msg);
        }

    }

    private void verbose(String tag, String msg) {


        if (debug) {
            String message = createMessage(tag, msg);
            Log.v(tag, message);
        }
        if(isOpen) {
            LdLog.v(tag, msg);
        }
    }

    private void debug(String tag, String msg) {;
        if (debug) {
            String message = createMessage(tag, msg);
            Log.d(tag, message);
        }
        if(isOpen){
            LdLog.d(tag, msg);
        }

    }

    private void error(String tag, String msg) {


        if (debug) {
            String message = createMessage(tag, msg);
            Log.e(tag, message);
        }
        if(isOpen){
            LdLog.e(tag, msg);
        }

    }

    private void warning(String tag, String msg) {


        if (debug) {
            String message = createMessage(tag, msg);
            Log.w(tag, message);
        }
        if(isOpen) {
            LdLog.w(tag, msg);
        }
    }

    private void getStackTraceString(String tag , String describe ,Exception e){
        String message = Log.getStackTraceString(e);

        if (debug) {
            Log.e(tag, "Exception: " + describe + message);
        }
        if(isOpen){
            LdLog.e(tag, message);
        }

    }


    public static void setDebug(boolean isDebug) {
        debug = isDebug;
        if(isOpen){
            LdLog.setDebug(debug);
        }

    }

    public static boolean isDebug() {
        return debug;
    }

}
