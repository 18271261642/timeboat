package com.imlaidian.utilslibrary.utils;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.imlaidian.utilslibrary.UtilsApplication;
import com.imlaidian.utilslibrary.config.IntentConstant;
import com.imlaidian.utilslibrary.config.PublicConstant;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.imlaidian.utilslibrary.config.IntentConstant.MAP_NAVIGATION_ACTION_QUITE;
import static com.imlaidian.utilslibrary.config.IntentConstant.MAP_NAVIGATION_ACTION_TERMINAL_ID;
import static com.imlaidian.utilslibrary.utils.MainBoardVersionTools.isAlagroupPlatform;
import static com.imlaidian.utilslibrary.utils.MainBoardVersionTools.isChuangPinPlatform;
import static com.imlaidian.utilslibrary.utils.MainBoardVersionTools.isCpFacePayRom;
import static com.imlaidian.utilslibrary.utils.MainBoardVersionTools.isCpYsWxPayRom;
import static com.imlaidian.utilslibrary.utils.MainBoardVersionTools.isFacePayRom;
import static com.imlaidian.utilslibrary.utils.MainBoardVersionTools.isQzFacePayRom;
import static com.imlaidian.utilslibrary.utils.MainBoardVersionTools.isXiaoWeiFootballPrinterPlatform;


public class SystemTool {
    public static Context mContext = UtilsApplication.getInstance().getApplicationContext();
    private static final String TAG = "SystemTool";


    /// ????????????
    public static final String BROADCAST_ACTION_NAME = "com.mobilepower.terminal.message";
    /// ??????
    public static final String BROADCAST_PARAM_TYPE_NAME = "type";

    /// ????????????
    public static final int BROADCAST_TYPE_MONITOR = 1;
    /// ????????????
    public static final int BROADCAST_TYPE_SYSTEM_UPGRADE = 2;
    /// ????????????
    public static final int BROADCAST_TYPE_REBOOT = 3;
    /// ????????????
    public static final int BROADCAST_TYPE_POWEROFF = 4;
    /// ????????????
    public static final int BROADCAST_TYPE_SET_VOLUME = 5;
    /// ??????????????????
    public static final int BROADCAST_TYPE_SET_DATETIME = 6;
    /// ???????????????
    public static final int BROADCAST_TYPE_SERIAL_CAN_INPUT = 7;
    /// ???????????????????????????????????????
    public static final int BROADCAST_TYPE_SERIAL_ALWAYS_RECV_DATA_ERR = 8;
    /// ??????????????????????????????
    public static final String BROADCAST_SERIAL_DATA_ERR_CONTENT = "content";
    /// ???????????????????????????????????????????????????
    public static final int BROADCAST_TYPE_SERIAL_DATA_ERR_POWER_REBOOT = 9;


    private static Intent getBroadCastIntent(int type) {
        String action = BROADCAST_ACTION_NAME;
        Intent intent = new Intent(action);
        intent.putExtra(BROADCAST_PARAM_TYPE_NAME, "" + type);

        return intent;
    }

    /// ??????????????????
    public static void sendMonitorBroadcast(boolean on) {
        Context context = UtilsApplication.getInstance().getApplicationContext();

        if (null != context) {
            Intent intent = getBroadCastIntent(BROADCAST_TYPE_MONITOR);
            intent.putExtra("on", on ? 1 : 0);
            context.sendBroadcast(intent);
        }
    }

    /// ????????????????????????
    public static void sendSystemUpgradeBroadcast(String path) {
        Context context = UtilsApplication.getInstance().getApplicationContext();

        if (null != context && null != path) {
            Intent intent = getBroadCastIntent(BROADCAST_TYPE_SYSTEM_UPGRADE);
            intent.putExtra("path", path);
            context.sendBroadcast(intent);
        }
    }

    /// ????????????
    public static void sendRebootBroadcast() {
        Context context = UtilsApplication.getInstance().getApplicationContext();

        if (null != context) {
            Intent intent = getBroadCastIntent(BROADCAST_TYPE_REBOOT);
            context.sendBroadcast(intent);
        }
    }

    /// ????????????
    public static void sendPoweroffBroadcast() {
        Context context = UtilsApplication.getInstance().getApplicationContext();

        if (null != context) {
            Intent intent = getBroadCastIntent(BROADCAST_TYPE_POWEROFF);
            context.sendBroadcast(intent);
        }
    }

    /// ????????????
    public static void sendSetVolumeBroadcast(int volume) {
        Context context = UtilsApplication.getInstance().getApplicationContext();

        if (null != context && volume >= 0) {
            Intent intent = getBroadCastIntent(BROADCAST_TYPE_SET_VOLUME);
            intent.putExtra("volume", volume);
            context.sendBroadcast(intent);
        }
    }


    /// ??????????????????
    /// dateTime: 20170426.121230
    public static void sendSetDateTimeBroadcast(String dateTime) {
        Context context = UtilsApplication.getInstance().getApplicationContext();

        if (null != context && null != dateTime) {
            Intent intent = getBroadCastIntent(BROADCAST_TYPE_SET_DATETIME);
            intent.putExtra("dateTime", dateTime);
            context.sendBroadcast(intent);
        }
    }

    /// ????????????????????????
    public static void sendSerialCanInput(boolean on) {
        Context context = UtilsApplication.getInstance().getApplicationContext();

        if (null != context) {
            Intent intent = getBroadCastIntent(BROADCAST_TYPE_SERIAL_CAN_INPUT);
            intent.putExtra("on", on ? 1 : 0);
            context.sendBroadcast(intent);
        }
    }

    /// ???????????????????????????????????????
    public static void sendSerialAlwaysRecvDataBroadcast(String content) {
        Context context = UtilsApplication.getInstance().getApplicationContext();

        if (null != context) {
            Intent intent = getBroadCastIntent(BROADCAST_TYPE_SERIAL_ALWAYS_RECV_DATA_ERR);
            intent.putExtra(BROADCAST_SERIAL_DATA_ERR_CONTENT, content);
            context.sendBroadcast(intent);
        }
    }

    /// ???????????????????????????????????????????????????
    public static void sendSerialDataErrPowerRebootBroadcast() {
        Context context = UtilsApplication.getInstance().getApplicationContext();

        if (null != context) {
            Intent intent = getBroadCastIntent(BROADCAST_TYPE_SERIAL_DATA_ERR_POWER_REBOOT);
            context.sendBroadcast(intent);
        }
    }




    /**
     * ????????????????????????????????????
     */
    public static String getDataTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }

    /**
     * ????????????????????????(?????????HH:mm??????)
     */
    public static String getDataTime() {
        return getDataTime("HH:mm");
    }


    /**
     * ??????????????????SDK??????
     *
     * @return ???API 17 ????????? 17
     */
    public static int getSDKVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * ??????????????????
     *
     * @return ??????4.4.2
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static String getAndroidId() {
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getSystemModeVersion() {
        String version = "BOARD=" + android.os.Build.BOARD
                + "BRAND=" + android.os.Build.BRAND
                + "MANUFACTURER=" + android.os.Build.MANUFACTURER
                + "hardWare=" + android.os.Build.HARDWARE;

        return version;
    }

    public static String getManufacturer() {
        return android.os.Build.MANUFACTURER;
    }

    public static String getModel() {
        return Build.MODEL;
    }

    public static String getCPUABI() {
        return Build.CPU_ABI;
    }

    /**
     * ????????????IMEI???
     */
    public static String getPhoneImei() {
        try {
            String imei = "";
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getSimSerialNumber();
            if (imei == null) {
                imei = "";
            }
            LogUtil.d(TAG, "getPhoneImei = " + imei);
            return imei;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * ????????????
     * return String
     */
    public static String getBaseBandVersion() {
        String Version = "";
        try {
            Class cl = Class.forName("android.os.SystemProperties");
            Object invoker = cl.newInstance();
            Method m = cl.getMethod("get", new Class[]{String.class, String.class});
            Object result = m.invoke(invoker, new Object[]{"gsm.version.baseband", "no message"});
            Version = (String) result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Version;
    }

    /**
     * ????????????
     * return String
     */

    public static String getLinuxCoreVersion() {
        Process process = null;
        String kernelVersion = "";
        try {
            process = Runtime.getRuntime().exec("cat /proc/version");
        } catch (IOException e) {
            e.printStackTrace();
        }


        // get the output line
        InputStream outs = process.getInputStream();
        InputStreamReader isrout = new InputStreamReader(outs);
        BufferedReader brout = new BufferedReader(isrout, 8 * 1024);


        String result = "";
        String line;
        // get the whole standard output string
        try {
            while ((line = brout.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            if (result != "") {
                String Keyword = "version ";
                int index = result.indexOf(Keyword);
                line = result.substring(index + Keyword.length());
                index = line.indexOf(" ");
                kernelVersion = line.substring(0, index);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return kernelVersion;
    }


    /**
     * ??????????????????
     */
    public static String getInnerVersion() {
        String ver = "";
//        ver = "V1.0.0(20190319)_LD_L_43_THIRTY_SHIANYUN" ;
//        return ver;
        if (!android.os.Build.DISPLAY.equals("")) {
            ver = android.os.Build.DISPLAY;
        } else {
            ver = android.os.Build.VERSION.INCREMENTAL;
        }

        return ver;
    }


    /**
     * ????????????????????????
     *
     * @param cxt     context
     * @param smsBody ????????????
     */
    public static void sendSMS(Context cxt, String smsBody) {
        Uri smsToUri = Uri.parse("smsto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", smsBody);
        cxt.startActivity(intent);
    }


    /**
     * ????????????????????????
     */
    public static boolean isNetworkAvailable() {
        return isNetworkAvailable(mContext);
    }

    public static boolean isNetworkAvailable(Context ctx) {
        boolean result = true;

        if (null != ctx) {
            ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager != null) {
                NetworkInfo networkinfo = manager.getActiveNetworkInfo();

                if (networkinfo == null || !networkinfo.isAvailable()) {
                    result = false;
                }
            }
        } else {
            result = false;
        }

        return result;
    }


    /**
     * check if WIFI is available
     *
     * @return boolean true available false unavailable
     */
    public static boolean isWifiAvailable(Context ctx) {
        if (null != ctx) {
            ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null != manager) {
                NetworkInfo activeNetInfo = manager.getActiveNetworkInfo();
                if (activeNetInfo != null
                        && null != activeNetInfo.getTypeName()
                        && activeNetInfo.getTypeName().equalsIgnoreCase("WIFI")) {
                    return true;
                }
            }
        }

        return false;
    }

    //??????????????????????????????
    public static boolean isMobile(Context ctx) {
        if (null != ctx) {
            ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null != manager) {
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @return String
     */
    public static String getPhoneCode(Context context) {
        String phonecode = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (phonecode == null || phonecode.equals("null")) {
            phonecode = "";
        }
        return phonecode;
    }

    /**
     * get default country
     *
     * @return String such as tw cn hk and sg
     */
    public static String getCountryCode() {
        return Locale.getDefault().getCountry().toLowerCase();
    }

    /**
     * get default language
     *
     * @return String such as zh
     */
    public static String getLanguage() {
        return Locale.getDefault().getLanguage().toLowerCase();
    }

    /**
     * get android os version
     *
     * @return String such as 4.1.1
     */
    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * get application version
     *
     * @return String
     */
    public static String getAppVersion(Context context) {
        String version = "0";
        String packageName = null;
        PackageInfo pf = null;
        try {
            packageName = context.getPackageName();
            pf = context.getPackageManager().getPackageInfo(packageName, 0);
            version = String.valueOf(pf.versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return version;
    }


    /**
     * ????????????????????????
     *
     * @param context     ?????????
     * @param packageName ??????
     * @return ??????
     */
    private static Intent getIntentByPackageName(Context context, String packageName) {
        return context.getPackageManager().getLaunchIntentForPackage(packageName);
    }

    /**
     * ?????????????????????App
     *
     * @param context     ?????????
     * @param packageName ??????
     * @return {@code true}: ????????????<br>{@code false}: ????????????
     */
    public static boolean openAppByPackageName(Context context, String packageName) {
        Intent intent = getIntentByPackageName(context, packageName);
        if (intent != null) {
            context.startActivity(intent);
            return true;
        }
        return false;
    }



    /**
     * ?????????????????????
     */
    public static void onFocusChange(final TextView tv, final boolean isShow) {
        tv.requestFocus();
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) tv.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (isShow) {
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    imm.hideSoftInputFromWindow(tv.getWindowToken(), 0);
                }
            }
        }, 100);
    }

    /**
     * ??????????????????????????????????????????
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    // ????????????
                    return true;
                } else {
                    // ????????????
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * ??????application????????????meta-data
     *
     * @return ????????????????????????(??????????????? ??? ????????????)?????????????????????
     */
    public String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key) + "";
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }


    /**
     * get application name
     *
     * @return String
     */
    public static String getAppName() {
        return PublicConstant.APP_NAME;
    }




    /**
     * ??????????????????????????????
     */
    public static boolean isSleeping(Context context) {
        KeyguardManager kgMgr = (KeyguardManager) context
                .getSystemService(Context.KEYGUARD_SERVICE);
        boolean isSleeping = kgMgr.inKeyguardRestrictedInputMode();
        return isSleeping;
    }

    public static String getSystemProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method get = clazz.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(clazz, key, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    public static boolean isCPUInfo64() {
        return getSystemProperty("ro.product.cpu.abi", "arm").contains("64");
    }

    public static boolean uninstallAPK(String packageName) {
        PrintWriter writer = null;
        Process process = null;
        int value = -1;

        try {
            process = Runtime.getRuntime().exec(getRootCommand());
            writer = new PrintWriter(process.getOutputStream());

            if (isCPUInfo64()) {
                writer.println("export LD_LIBRARY_PATH=/vendor/lib64:/system/lib64");
            } else {
                writer.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
            }

            writer.println("pm uninstall " + packageName);
            writer.flush();
            writer.close();

            value = process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return 0 == value;
    }

    /**
     * ??????apk
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("application/vnd.android.package-archive");
        intent.setData(Uri.fromFile(file));
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static boolean silentUninstall(String packageName) {
        PrintWriter writer = null;
        Process process = null;
        int value = -1;

        try {
            process = Runtime.getRuntime().exec(getRootCommand());

            writer = new PrintWriter(process.getOutputStream());

            if (isCPUInfo64()) {
                writer.println("export LD_LIBRARY_PATH=/vendor/lib64:/system/lib64");
            } else {
                writer.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
            }

            writer.println("pm uninstall " + packageName);
            LogUtil.d(TAG, "silent pm uninstall " + packageName);
            /// ????????????
            writer.println("sync");
            writer.println("exit");
            writer.flush();
            value = process.waitFor();

            writer.close();

            value = 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        LogUtil.d(TAG, "silent apk uninstall value = " + value);
        return (0 == value);
    }

    public static boolean silentInstall(String filePath) {
        if (null == filePath || filePath.length() <= 0) {
            return false;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }

        PrintWriter writer = null;
        Process process = null;
        int value = -1;

        try {
            process = Runtime.getRuntime().exec(getRootCommand());

            writer = new PrintWriter(process.getOutputStream());

            if (isCPUInfo64()) {
                writer.println("export LD_LIBRARY_PATH=/vendor/lib64:/system/lib64");
            } else {
                writer.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
            }

            writer.println("pm install -r " + filePath);
            LogUtil.d(TAG, "silent pm install -r " + filePath);
            /// ????????????
            writer.println("sync");
            writer.println("exit");
            writer.flush();
            value = process.waitFor();

            writer.close();

            value = 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        LogUtil.d(TAG, "silent apk install value = " + value);
        return (0 == value);
    }

    /**
     * ????????????apk
     *
     * @param filePath ????????????
     * @return true / false
     */
    public static boolean silentInstallMainApk(String filePath) {
        if (null == filePath || filePath.length() <= 0) {
            return false;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }

        PrintWriter writer = null;
        Process process = null;
        int value = -1;

        try {
            LogUtil.d(TAG, "-----> su");
            String suCmd = getRootCommand();
            LogUtil.d(TAG, "-----> su=" + suCmd);
            process = Runtime.getRuntime().exec(suCmd);

            writer = new PrintWriter(process.getOutputStream());
            LogUtil.d(TAG, "-----> chmod ");
            writer.println("chmod 777 " + filePath);

            if (isCPUInfo64()) {
                writer.println("export LD_LIBRARY_PATH=/vendor/lib64:/system/lib64");
            } else {
                writer.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
            }

            LogUtil.d(TAG, "-----> pm install");
            writer.println("pm install -r " + filePath);

            /// ????????????
            writer.println("sync");

            /// ????????????????????????apk, ???????????????kill?????????????????????????????????
            LogUtil.d(TAG, "-----> reboot");
            writer.println("reboot");

            LogUtil.d(TAG, "-----> exit");
            writer.println("exit");

            LogUtil.d(TAG, "-----> flush");
            writer.flush();

            value = process.waitFor();

            LogUtil.d(TAG, "-----> close ....");
            writer.close();

            value = 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        LogUtil.d(TAG, "-----> value = " + value);
        return (0 == value);
    }

    /// ??????
    public static boolean reboot() {
        LogUtil.d(TAG, "reboot");

        PrintWriter writer = null;
        Process process = null;
        int value = -1;

        /// ???????????????
        LogUtil.getInstance().stopLogToFile();
        ThreadUtils.sleepSecond(1);

        try {
            process = Runtime.getRuntime().exec(getRootCommand());
            writer = new PrintWriter(process.getOutputStream());
            writer.println("reboot");
            writer.println("exit");
            writer.flush();
            writer.close();

            value = process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return (0 == value);
    }

    /**
     * ??????
     *
     * @return true / false
     */
    public static boolean poweroff() {
        LogUtil.d(TAG, "poweroff");

        PrintWriter writer = null;
        Process process = null;
        int value = -1;

        /// ???????????????
        LogUtil.getInstance().stopLogToFile();
        ThreadUtils.sleepSecond(1);

        try {
            process = Runtime.getRuntime().exec(getRootCommand());
            writer = new PrintWriter(process.getOutputStream());
            if (isBeyondOS6() || isChuangPinPlatform()) {
                writer.println("reboot -p ");
            } else {
                writer.println("poweroff");
            }

            writer.println("exit");
            writer.flush();
            writer.close();

            value = process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return (0 == value);
    }


    public static boolean killPacknameByCommand(String packname) {
        LogUtil.d(TAG, "kill packname =" + packname);

        PrintWriter writer = null;
        Process process = null;
        int value = -1;

        try {
            process = Runtime.getRuntime().exec(getRootCommand());
            writer = new PrintWriter(process.getOutputStream());
            writer.println("am force-stop " + packname);
            writer.println("exit");
            writer.flush();
            writer.close();

            value = process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return (0 == value);
    }

    /**
     * ????????????????????????????????????
     */
    public static String getAppVersionName(Context context) {
        String version = "0";

        try {
            version = context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return version;
    }

    /**
     * ????????????????????????????????????
     */
    public static int getAppVersionCode(Context context) {
        int version = 0;
        try {
            version = context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return version;
    }

    /**
     * ??????home???????????????
     */
    public static void goHome(Context context) {
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(mHomeIntent);
    }

    /**
     * ??????????????????
     */
    public static String getSign(Context context, String pkgName) {
        try {
            PackageInfo pis = context
                    .getPackageManager()
                    .getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);

            return hexdigest(pis.signatures[0].toByteArray());
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(SystemTool.class.getName() + "the "
                    + pkgName + "'s application not found");
        }
    }

    /**
     * ????????????????????????????????????32?????????
     */
    private static String hexdigest(byte[] paramArrayOfByte) {
        final char[] hexDigits = {
                48, 49, 50, 51, 52,
                53, 54, 55, 56, 57,
                97, 98, 99, 100, 101, 102};
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramArrayOfByte);

            byte[] arrayOfByte = localMessageDigest.digest();
            char[] arrayOfChar = new char[32];
            for (int i = 0, j = 0; ; i++, j++) {
                if (i >= 16) {
                    return new String(arrayOfChar);
                }

                int k = arrayOfByte[i];
                arrayOfChar[j] = hexDigits[(0xF & k >>> 4)];
                arrayOfChar[++j] = hexDigits[(k & 0xF)];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    /**
     * ?????????????????????????????????
     *
     * @param cxt ?????????????????????context
     * @return ??????????????????
     */
    public static int getDeviceUsableMemory(Context cxt) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        if (null != am) {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);

            // ?????????????????????????????????
            return (int) (mi.availMem / (1024 * 1024));
        }

        return 0;
    }

    /**
     * ??????android????????????????????????
     */
    private static String getAvailMemory(Context mContext) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (null != am) {
            android.app.ActivityManager.MemoryInfo outInfo = new android.app.ActivityManager.MemoryInfo();
            am.getMemoryInfo(outInfo);
            long availMem = outInfo.availMem;

            // mi.availMem; ???????????????????????????
            // ?????????????????????????????????
            return Formatter.formatFileSize(mContext, availMem);
        }

        return "";
    }

    /**
     * ?????????????????????
     */
    private static String getTotalMemory(Context mContext) {
        String str1 = "/proc/meminfo"; //????????????????????????
        String str2 = null;
        String[] arrayOfString = null;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// ??????meminfo?????????????????????????????????

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }

            // ?????????????????????????????????KB?????????1024?????????Byte
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
            localBufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Formatter.formatFileSize(mContext, initial_memory);// Byte?????????KB??????MB????????????????????????
    }

    /**
     * ????????????MAC?????? ??????????????????wifi???????????????mac??????
     */
    public static String getMacAddress() {
        String macString = "";

        try {
            WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            if (wifi == null) {
                return "";
            }

            WifiInfo info = wifi.getConnectionInfo();
            if (info == null) {
                return "";
            }

            macString = info.getMacAddress();
            if (macString == null) {
                macString = "";
            }

            if (macString.equals("02:00:00:00:00:00") || macString.equals("")) {
                try {
                    macString = getMac();
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.s(TAG, " getMacAddress e :", e);
                }
            }
        } catch (Exception e) {
            LogUtil.s(TAG, "macString e  :", e);
        }

        if (macString == null) {
            macString = "";
        }

        LogUtil.d(TAG, "mac address =" + macString);

        return macString;
    }

    private static String getMac() {
        String str = "";
        String macSerial = null;

        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (macSerial == null || "".equals(macSerial)) {
            try {
                macSerial = loadFileAsString("/sys/class/net/eth0/address");
                if (null != macSerial) {
                    macSerial = macSerial.toUpperCase().substring(0, 17);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d(TAG, " loadFileAsString e:" + e.toString());
            }
        }

        if (macSerial == null || "".equals(macSerial)) {
            try {
                macSerial = macAddress();
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d(TAG, "macAddress e:" + e.toString());
            }
        }

        if (macSerial == null) {
            macSerial = "";
        }

        return macSerial;
    }

    public static String macAddress() throws SocketException {
        String address = "";
        // ???????????????????????????????????????????????? Enumeration?????????
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface netWork = interfaces.nextElement();
            // ????????????????????????????????????????????????????????????????????????????????????????????????????????? MAC??????
            byte[] by = netWork.getHardwareAddress();
            if (by == null || by.length == 0) {
                continue;
            }

            StringBuilder builder = new StringBuilder();
            for (byte b : by) {
                builder.append(String.format("%02X:", b));
            }

            if (builder.length() > 0) {
                builder.deleteCharAt(builder.length() - 1);
            }

            String mac = builder.toString();
            // ??????????????????????????????MAC?????????????????????????????????Wifi??? name ??? wlan0
            if (netWork.getName().equals("wlan0")) {
                address = mac;
            }

            if (netWork.getName().equals("eth0")) {
                address = mac;
            }
        }

        return address;
    }

    private static String loadFileAsString(String fileName) throws Exception {
        if (null != fileName) {
            File file = new File(fileName);
            if (file.exists()) {
                FileReader reader = new FileReader(fileName);

                String text = loadReaderAsString(reader);
                reader.close();

                return text;
            }
        }

        return null;
    }

    private static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);

        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }

        return builder.toString();
    }

    public static String getIPAddress() {
        return getIPAddress(UtilsApplication.getInstance().getApplicationContext());
    }

    /**
     * ??????Ip??????
     */
    public static String getIPAddress(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != manager) {
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getType() == ConnectivityManager.TYPE_MOBILE
                        || info.getType() == ConnectivityManager.TYPE_ETHERNET) {
                    //????????????2G/3G/4G?????? ?????????
                    try {
                        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                            NetworkInterface intf = en.nextElement();
                            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                                InetAddress inetAddress = enumIpAddr.nextElement();
                                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                    return inetAddress.getHostAddress();
                                }
                            }
                        }
                    } catch (SocketException e) {
                        LogUtil.s(TAG, "get Ip Address error", e);
                        e.printStackTrace();
                    }
                } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//????????????????????????
                    WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    if (null != wifiManager) {
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                        return intIP2StringIP(wifiInfo.getIpAddress());
                    }
                }
            } else {
                LogUtil.d(TAG, "network link disconnect");
                return "no network";
            }
        }

        return "unknown";
    }

    /**
     * ????????????int?????????IP?????????String??????
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "."
                + ((ip >> 8) & 0xFF) + "."
                + ((ip >> 16) & 0xFF) + "."
                + (ip >> 24 & 0xFF);
    }

    /**
     * ??????CPU??????
     */
    private static String[] getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""}; // 1-cpu?????? //2-cpu??????
        String[] arrayOfString = null;

        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }

            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cpuInfo;
    }




    public static String networkTypeName(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (null == telephonyManager) return "unknown";

        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:  //  ???????????????1xRTT ?????? 2.5
                return "1xRTT"; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:   // ??????????????? CDMA ?????????2g???
                return "CDMA"; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:   //EDGE?????????2g???
                return "EDGE"; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0: //???????????????EVDO  ??????0.?????????3g???
                return "EVDO_0"; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A: //EVDO   ??????A ?????????3g???
                return "EVDO_A"; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS: //GPRS ?????????2g
                return "GPRS"; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA: //HSDPA?????????3g???
                return "HSDPA"; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA: //HSPA
                return "HSPA"; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA: // HSUPA
                return "HSUPA"; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS: //???????????????UMTS?????????3g
                return "UMTS"; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:  //eHRPD
                return "EHRPD"; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B: //EVDO  ??????B?????????3g???
                return "EVDO_B"; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP: //HSPA+
                return "HSPAP"; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:  //iDen
                return "IDEN"; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:  //LTE(3g???4g???????????????????????????4g)
                return "LTE"; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "unknown";
            default:
                return "unknown";
        }
    }


    /**
     * ???????????????????????????
     *
     * @param cxt ?????????????????????context
     * @return ??????????????????
     */
    public static int gc(Context cxt) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        if (null == am) {
            return 0;
        }

        long i = getDeviceUsableMemory(cxt);
        int count = 0; // ?????????????????????
        // ?????????????????????service??????
        List<ActivityManager.RunningServiceInfo> serviceList = am.getRunningServices(100);

        if (serviceList != null) {
            for (ActivityManager.RunningServiceInfo service : serviceList) {
                if (service.pid == android.os.Process.myPid()) {
                    continue;
                }

                try {
                    android.os.Process.killProcess(service.pid);
                    count++;
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        }

        // ?????????????????????????????????
        List<ActivityManager.RunningAppProcessInfo> processList = am.getRunningAppProcesses();
        if (processList != null) {
            for (ActivityManager.RunningAppProcessInfo process : processList) {
                // ??????????????????RunningAppProcessInfo.IMPORTANCE_SERVICE?????????????????????????????????????????????
                // ??????????????????RunningAppProcessInfo.IMPORTANCE_VISIBLE????????????????????????????????????????????????????????????
                if (process.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    // pkgList ?????????????????????????????????
                    String[] pkgList = process.pkgList;
                    for (String pkgName : pkgList) {
                        try {
                            am.killBackgroundProcesses(pkgName);
                            count++;
                        } catch (Exception e) { // ??????????????????
                            e.getStackTrace();
                        }
                    }
                }
            }
        }

        LogUtil.d(TAG, "?????????" + (getDeviceUsableMemory(cxt) - i) + "M??????");
        return count;
    }


    public static String getMachineUUID() {
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            final String tmDevice = "" + tm.getDeviceId();
            final String tmSerial = "" + tm.getSimSerialNumber();
            final String androidId = "" + android.provider.Settings.Secure.getString(mContext.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(),
                    ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

            LogUtil.d(TAG, "deviceUuid =" + deviceUuid.toString());

            return deviceUuid.toString();
        }

        return "";
    }

    /**
     * ???????????????????????????ms
     *
     * @param second ???
     * @return ?????????ms??????
     */
    public static long secondToMillisecond(double second) {
        return (long) (second * 1000);
    }

    public static long minuteToMillisecond(double minute) {
        return secondToMillisecond(minute * 60);
    }

    public static long hourToMillisecond(double hour) {
        return minuteToMillisecond(hour * 60);
    }

    public static byte[] stringToByte(String string) {
        byte[] buf = null;
        String temp = null;
        int beginIndex = 0;

        if (null != string && string.length() > 0) {
            String src = string;
            if (src.length() % 2 != 0) {
                src = "0" + src;
            }

            buf = new byte[src.length() / 2];
            for (int i = 0; i < buf.length; i++) {
                beginIndex = i * 2;
                temp = src.substring(beginIndex, beginIndex + 2);
                buf[i] = (byte) (StringsUtils.toInteger(temp, 16) & 0xff);
            }
        }

        return buf;
    }

    /// ???byte[] ????????? Hex String
    public static String byteToHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();

        if (null != bytes) {
            String string = "";
            for (byte i : bytes) {
                string = "0x" + Integer.toHexString(i & 0xff) + " ";
                stringBuilder.append(string);
            }
        }

        return stringBuilder.toString();
    }

    public static String byteToHexStringWithout0X(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();

        if (null != bytes) {
            String string = "";
            for (byte i : bytes) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(" ");
                }

                string = Integer.toHexString(i & 0xff);
                stringBuilder.append(string);
            }
        }

        return stringBuilder.toString();
    }

    /// ???byte[] ????????? hex string , ??????0x
    public static String dataToHexStringWithout0x(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder();

        if (null != data) {
            for (byte i : data) {
                String temp = Integer.toHexString(i & 0xff) + " ";
                stringBuilder.append(temp);
            }
        }

        return stringBuilder.length() > 0 ? stringBuilder.toString() : null;
    }

    public static boolean isOS6() {
        return (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.M);
    }

    public static boolean isBeyondOS6() {
        return (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    public static boolean isOS7() {
        return (android.os.Build.VERSION.SDK_INT == 24
                || android.os.Build.VERSION.SDK_INT == 25);
    }

    public static boolean isOS8() {
        return (android.os.Build.VERSION.SDK_INT == 26
                || android.os.Build.VERSION.SDK_INT == 27);
    }


    /***
     * ???????????????????????????
     * @return
     */

//    public List<String> getAllExternalStorage() {
//        List<String> storagePath = new ArrayList<>();
//        StorageManager storageManager = (StorageManager) TerminalApplication.getInstance().getApplicationContext().getSystemService(Context.STORAGE_SERVICE);
//        StorageVolume[] storageVolumes;
//        try {
//            Method getVolumeList = StorageManager.class.getDeclaredMethod("getVolumeList");
//            storageVolumes = (StorageVolume[]) getVolumeList.invoke(storageManager);
//            Method getVolumeState = StorageManager.class.getDeclaredMethod("getVolumeState", String.class);
//            for (StorageVolume storageVolume : storageVolumes) {
//                Method getPath = null;
//                getPath = StorageVolume.class.getMethod("getPath");
//                String path = (String) getPath.invoke(storageVolume);
//                Log.e("sdcard", "====path==" + path);
//                String state = (String) getVolumeState.invoke(storageManager, path);
//                if (Environment.MEDIA_MOUNTED.equals(state)) {
//                    storagePath.add(path);
//                }
//            }
//        } catch (Exception e) {
//            Log.e("cdl", e.getMessage());
//        }
//        return storagePath;
//    }
    private static String getSDcardDir() {
        String sdcardDir = null;
        ArrayList<String> targetPathList = new ArrayList<String>();
        StorageManager mStorageManager = (StorageManager) mContext
                .getSystemService(Context.STORAGE_SERVICE);

        Class<?> volumeInfoClazz = null;
        Class<?> diskInfoClazz = null;
        try {
            diskInfoClazz = Class.forName("android.os.storage.DiskInfo");
            Method isSd = diskInfoClazz.getMethod("isSd");
            Method isUsb = diskInfoClazz.getMethod("isUsb");
            volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
            Method getType = volumeInfoClazz.getMethod("getType");
            Method getDisk = volumeInfoClazz.getMethod("getDisk");
            Field path = volumeInfoClazz.getDeclaredField("path");
            Method getVolumes = mStorageManager.getClass().getMethod("getVolumes");
            List<Class<?>> result = (List<Class<?>>) getVolumes.invoke(mStorageManager);
            for (int i = 0; i < result.size(); i++) {
                Object volumeInfo = result.get(i);
                if ((int) getType.invoke(volumeInfo) == 0) {
                    Object disk = getDisk.invoke(volumeInfo);
                    if (disk != null) {
                        if ((boolean) isSd.invoke(disk)) {
                            sdcardDir = (String) path.get(volumeInfo);

                            break;
                        }
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null != sdcardDir) {
            sdcardDir = sdcardDir + File.separator;
        }

        return sdcardDir;

    }

    public static ArrayList<String> getSdStoragePath() {
        ArrayList<String> targetPathList = new ArrayList<String>();
        StorageManager mStorageManager = (StorageManager) mContext
                .getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");

            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");

            Method getPath = storageVolumeClazz.getMethod("getPath");

            Object result = getVolumeList.invoke(mStorageManager);


//            Method getVolumes = mStorageManager.getClass().getMethod("getVolumes");
//            Class<?>   diskInfoClazz = Class.forName("android.os.storage.DiskInfo");
//
//            Method isSd = diskInfoClazz.getMethod("isSd");
//
//            Class<?>  volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
//            Method getType = volumeInfoClazz.getMethod("getType");
//            Method getDisk = volumeInfoClazz.getMethod("getDisk");

            final int length = Array.getLength(result);

            Method getUserLabel = storageVolumeClazz.getMethod("getUserLabel");
            Method getFsUuidMethod = storageVolumeClazz.getMethod("getUuid");

            for (int i = 0; i < length; i++) {

                Object storageVolumeElement = Array.get(result, i);

                String userLabel = (String) getUserLabel.invoke(storageVolumeElement);

                String path = (String) getPath.invoke(storageVolumeElement);

                String uuid = (String) getFsUuidMethod.invoke(storageVolumeElement);
                Log.d(TAG, "getStoragePath userLabel=" + userLabel + "," +
                        "path=" + path);
                if (uuid != null && !uuid.equals("")) {
                    if (userLabel.contains("SD")) {
                        targetPathList.add(path);
                        Log.d(TAG, "getStoragePath userLabel=" + userLabel + ",path=" + path);
                    } else if (!userLabel.contains("U???")) {
                        targetPathList.add(path);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return targetPathList;
    }

    /**
     * ????????????SD?????????
     *
     * @return ???????????????????????????
     */
    public static List<String> getExtSDCardPath() {
        List<String> lResult = new ArrayList<String>();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            lResult = getSdStoragePath();

            String sdcard = getSDcardDir();
            if (sdcard != null && sdcard.length() > 0) {
                lResult.add(sdcard);
            }
            Log.d(TAG, "getStoragePath storagePath=");
        } else {

            try {
                Runtime rt = Runtime.getRuntime();
                Process proc = rt.exec("mount");
                InputStream inputStream = proc.getInputStream();
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(isr);
                String line = null;

                while ((line = br.readLine()) != null) {
                    if (line.contains("external_storage/sdcard")
                            || (line.contains("/storage/") && !line.contains("/storage/emulated"))
                            || (line.contains("/mnt/external_sd"))
                            || (isXiaoWeiFootballPrinterPlatform() && line.contains("/storage/sdcard1"))) {
                        String[] arr = line.split(" ");
                        String path = null;
                        String fsType = null;
                        if (isOS7()) {
                            path = arr[2];
                            fsType = arr[4];
                        } else {
                            path = arr[1];
                            fsType = arr[2];
                        }

                        if (fsType.equalsIgnoreCase("vfat") || fsType.equalsIgnoreCase("fuse")) {
                            File file = new File(path);
                            if (file.isDirectory()) {
                                lResult.add(path);
                            }
                        }
                    }
                }

                isr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return lResult;
    }

    public static double getExternalStorageTotalSizeKB() {
        return getExternalStorageTotalSize() / 1024.0;
    }

    public static double getExternalStorageTotalSizeMB() {
        return getExternalStorageTotalSizeKB() / 1024.0;
    }

    public static double getExternalStorageTotalSizeGB() {
        return getExternalStorageTotalSizeMB() / 1024.0;
    }

    public static long getExternalStorageTotalSize() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            return getStorageTotalSize(sdcardDir.getPath());
        }

        return 0;
    }

    public static double getExternalStorageFreeSizeKB() {
        return getExternalStorageFreeSize() / 1024.0;
    }

    public static double getStorageFreeSizeKB(String path) {
        return getStorageFreeSize(path) / 1024.0;
    }

    public static double getExternalStorageFreeSizeMB() {
        return getExternalStorageFreeSizeKB() / 1024.0;
    }

    public static double getStorageFreeSizeMB(String path) {
        return getStorageFreeSizeKB(path) / 1024.0;
    }

    public static double getExternalStorageFreeSizeGB() {
        return getExternalStorageFreeSizeMB() / 1024.0;
    }

    public static double getStorageFreeSizeGB(String path) {
        return getStorageFreeSizeMB(path) / 1024.0;
    }

    public static double convertSizeKB(long size) {
        return size / 1024.0;
    }

    public static double convertSizeMB(long size) {
        return size / (1024.0 * 1024.0);
    }

    public static double convertSizeGB(long size) {
        return size / (1024.0 * 1024.0 * 1024.0);
    }

    public static long getExternalStorageFreeSize() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            Log.d(TAG, "getExternalStorageFreeSize sdcardDir=" + sdcardDir.getPath() + ",path=" + sdcardDir.getAbsolutePath());
            return getStorageFreeSize(sdcardDir.getPath());
        }

        return 0;
    }


    public static long getStorageTotalSize(String path) {
        try {
            if (null != path && path.length() > 0) {
                StatFs sf = new StatFs(path);
                if (sf != null) {
                    long blockSize = sf.getBlockSize();
                    long blockCount = sf.getBlockCount();

                    //?????????:blockSize*blockCount;
                    //????????????:availCount*blockSize;
                    return blockSize * blockCount;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return 0;
    }

    public static long getStorageFreeSize(String path) {
        try {
            if (null != path && path.length() > 0) {
                StatFs sf = new StatFs(path);
                if (sf != null) {
                    long blkSize = sf.getBlockSize();
                    long availCount = sf.getAvailableBlocks();

                    //?????????:blockSize * blockCount;
                    //????????????:availCount * blockSize;
                    return availCount * blkSize;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return 0;
    }


    public static boolean hasSimCard() {
        Context context = UtilsApplication.getInstance().getApplicationContext();
        TelephonyManager telMgr = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        LogUtil.d(TAG, "sim card status simState " + simState);
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                result = false; // ??????SIM???
                break;

            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false;
                break;
            default:
                break;
        }
        LogUtil.d(TAG, result ? "has sim card" : "no sim card");
        return result;
    }

    /**
     * ????????????
     *
     * @param timezoneString ??????
     *                       <timezones>
     *                       <timezone id="Pacific/Majuro">?????????</timezone>
     *                       <timezone id="Pacific/Midway">?????????</timezone>
     *                       <timezone id="Pacific/Honolulu">?????????</timezone>
     *                       <timezone id="America/Anchorage">????????????</timezone>
     *                       <timezone id="America/Los_Angeles">????????????????????? (?????????)</timezone>
     *                       <timezone id="America/Tijuana">????????????????????? (?????????)</timezone>
     *                       <timezone id="America/Phoenix">?????????????????? (?????????)</timezone>
     *                       <timezone id="America/Chihuahua">?????????</timezone>
     *                       <timezone id="America/Denver">?????????????????? (??????)</timezone>
     *                       <timezone id="America/Costa_Rica">?????????????????? (???????????????)</timezone>
     *                       <timezone id="America/Chicago">?????????????????? (?????????)</timezone>
     *                       <timezone id="America/Mexico_City">?????????????????? (????????????)</timezone>
     *                       <timezone id="America/Regina">?????????????????? (?????????)</timezone>
     *                       <timezone id="America/Bogota">?????????????????? (?????????)</timezone>
     *                       <timezone id="America/New_York">?????????????????? (??????)</timezone>
     *                       <timezone id="America/Caracas">?????????????????? (????????????)</timezone>
     *                       <timezone id="America/Barbados">??????????????? (????????????)</timezone>
     *                       <timezone id="America/Manaus">????????????????????? (?????????)</timezone>
     *                       <timezone id="America/Santiago">????????????</timezone>
     *                       <timezone id="America/St_Johns">??????????????? (?????????)</timezone>
     *                       <timezone id="America/Sao_Paulo">?????????</timezone>
     *                       <timezone id="America/Argentina/Buenos_Aires">?????????????????????</timezone>
     *                       <timezone id="America/Godthab">????????????</timezone>
     *                       <timezone id="America/Montevideo">??????????????? (???????????????)</timezone>
     *                       <timezone id="Atlantic/South_Georgia">????????????</timezone>
     *                       <timezone id="Atlantic/Azores">???????????????</timezone>
     *                       <timezone id="Atlantic/Cape_Verde">?????????</timezone>
     *                       <timezone id="Africa/Casablanca">???????????????</timezone>
     *                       <timezone id="Europe/London">???????????????????????? (??????)</timezone>
     *                       <timezone id="Europe/Amsterdam">?????????????????? (???????????????)</timezone>
     *                       <timezone id="Europe/Belgrade">?????????????????? (???????????????)</timezone>
     *                       <timezone id="Europe/Brussels">?????????????????? (????????????)</timezone>
     *                       <timezone id="Europe/Sarajevo">?????????????????? (????????????)</timezone>
     *                       <timezone id="Africa/Windhoek">????????????</timezone>
     *                       <timezone id="Africa/Brazzaville">???????????????????????? (????????????)</timezone>
     *                       <timezone id="Asia/Amman">?????????????????? (??????)</timezone>
     *                       <timezone id="Europe/Athens">?????????????????? (??????)</timezone>
     *                       <timezone id="Asia/Beirut">?????????????????? (?????????)</timezone>
     *                       <timezone id="Africa/Cairo">?????????????????? (??????)</timezone>
     *                       <timezone id="Europe/Helsinki">?????????????????? (????????????)</timezone>
     *                       <timezone id="Asia/Jerusalem">??????????????? (????????????)</timezone>
     *                       <timezone id="Europe/Minsk">?????????</timezone>
     *                       <timezone id="Africa/Harare">???????????????????????? (?????????)</timezone>
     *                       <timezone id="Asia/Baghdad">?????????</timezone>
     *                       <timezone id="Europe/Moscow">?????????</timezone>
     *                       <timezone id="Asia/Kuwait">?????????</timezone>
     *                       <timezone id="Africa/Nairobi">???????????????????????? (?????????)</timezone>
     *                       <timezone id="Asia/Tehran">?????????????????? (?????????)</timezone>
     *                       <timezone id="Asia/Baku">??????</timezone>
     *                       <timezone id="Asia/Tbilisi">????????????</timezone>
     *                       <timezone id="Asia/Yerevan">?????????</timezone>
     *                       <timezone id="Asia/Dubai">??????</timezone>
     *                       <timezone id="Asia/Kabul">??????????????? (?????????)</timezone>
     *                       <timezone id="Asia/Karachi">?????????</timezone>
     *                       <timezone id="Asia/Oral">?????????</timezone>
     *                       <timezone id="Asia/Yekaterinburg">???????????????</timezone>
     *                       <timezone id="Asia/Calcutta">????????????</timezone>
     *                       <timezone id="Asia/Colombo">?????????</timezone>
     *                       <timezone id="Asia/Katmandu">??????????????? (????????????)</timezone>
     *                       <timezone id="Asia/Almaty">????????????</timezone>
     *                       <timezone id="Asia/Rangoon">???????????? (??????)</timezone>
     *                       <timezone id="Asia/Krasnoyarsk">????????????????????????</timezone>
     *                       <timezone id="Asia/Bangkok">??????</timezone>
     *                       <timezone id="Asia/Shanghai">?????????????????? (??????)</timezone>
     *                       <timezone id="Asia/Hong_Kong">???????????? (??????)</timezone>
     *                       <timezone id="Asia/Irkutsk">????????????????????? (???????????????)</timezone>
     *                       <timezone id="Asia/Kuala_Lumpur">?????????</timezone>
     *                       <timezone id="Australia/Perth">??????</timezone>
     *                       <timezone id="Asia/Taipei">???????????? (??????)</timezone>
     *                       <timezone id="Asia/Seoul">??????</timezone>
     *                       <timezone id="Asia/Tokyo">???????????? (??????)</timezone>
     *                       <timezone id="Asia/Yakutsk">?????????????????? (????????????)</timezone>
     *                       <timezone id="Australia/Adelaide">????????????</timezone>
     *                       <timezone id="Australia/Darwin">?????????</timezone>
     *                       <timezone id="Australia/Brisbane">????????????</timezone>
     *                       <timezone id="Australia/Hobart">?????????</timezone>
     *                       <timezone id="Australia/Sydney">??????</timezone>
     *                       <timezone id="Asia/Vladivostok">??????????????? (?????????????????????)</timezone>
     *                       <timezone id="Pacific/Guam">??????</timezone>
     *                       <timezone id="Asia/Magadan">??????????????? (?????????)</timezone>
     *                       <timezone id="Pacific/Auckland">?????????</timezone>
     *                       <timezone id="Pacific/Fiji">??????</timezone>
     *                       <timezone id="Pacific/Tongatapu">????????????</timezone>
     *                       </timezones>
     */
    public static void setTimeZone(String timezoneString,boolean addLog) {
        AlarmManager timeZone = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        TimeZone curTimeZone = TimeZone.getDefault();
        String curTimeZoneId = curTimeZone.getID();
        if(addLog){
            LogUtil.d(TAG, "set time zone = " + timezoneString + ", curTimeZoneId = " + curTimeZoneId);
        }else{
            Log.d(TAG, "set time zone = " + timezoneString + ", curTimeZoneId = " + curTimeZoneId);
        }

        if (!curTimeZoneId.equalsIgnoreCase(timezoneString)) {
            timeZone.setTimeZone(timezoneString);
        }
    }



    /**
     * ??????????????????
     */
    public static void setChinaTimeZone(boolean addLog) {
        setTimeZone("Asia/Shanghai",addLog);
    }

    /**
     * ??????????????????
     */
    public static void setChinaTimeZone() {
        setTimeZone("Asia/Shanghai",true);
    }

    /**
     * ????????????
     */
    public static void checkTimeZone() {
        setChinaTimeZone(true);
    }

    public static void checkTimeZone(boolean addLog) {
        setChinaTimeZone( addLog);
    }


    public static String createGmtOffsetString(boolean includeGmt,boolean includeMinuteSeparator, int offsetMillis)
    {
        int offsetMinutes = offsetMillis / 60000;
        char sign = '+';
        if (offsetMinutes < 0) {
            sign = '-';
            offsetMinutes = -offsetMinutes;

        }
        StringBuilder builder = new StringBuilder(9);
        if (includeGmt) {
            builder.append("GMT");
        }
        builder.append(sign);
        appendNumber(builder, 2, offsetMinutes / 60);
        if (includeMinuteSeparator) {
            builder.append(':');
        }
        appendNumber(builder, 2, offsetMinutes % 60);
        return builder.toString();
    }

    private static void appendNumber(StringBuilder builder, int count, int value) {
        String string = Integer.toString(value);
        for (int i = 0; i < count - string.length(); i++) {
            builder.append('0');
        }

        builder.append(string);
    }

    public static String getCurrentTimeZone()    {
        TimeZone tz = TimeZone.getDefault();
        return createGmtOffsetString(true,true,tz.getRawOffset());
    }

    /**
     * ??????????????????
     *
     * @param datetimes ?????????date -s 20160913.133000, 2016???09???13??? 13???30???00??? [yyyyMMdd.HHmmss]
     * @return true / false
     */
    public static boolean setSystemTime(String datetimes ,boolean addLog) {
        setChinaTimeZone(addLog);
        boolean success = false;

        try {
            if(addLog){
                LogUtil.d(TAG, "set system time = " + datetimes);
            }else{
                Log.d(TAG, "set system time = " + datetimes);
            }

            Process process = Runtime.getRuntime().exec(getRootCommand());
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            if (isAlagroupPlatform()) {
                /// ?????????????????????/system/bin/date???????????????????????????bug.
                os.writeBytes("date -s " + datetimes + "\n");
            }else if(isCpYsWxPayRom()){
                os.writeBytes("/system/bin/date " + datetimes + "\n");
            } else {
                os.writeBytes("/system/bin/date -s " + datetimes + "\n");
            }

            os.writeBytes("clock -w\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();

            try {
                int value = process.waitFor();
                if(addLog) {
                    LogUtil.d(TAG, "set system time waitfor value = " + value);
                }else{
                    Log.d(TAG, "set system time waitfor value = " + value);
                }
                success = (0 == value);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d(TAG, e.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            if(addLog) {
                LogUtil.d(TAG, "set system time " + e.toString());
            }else{
                Log.d(TAG, "set system time " + e.toString());
            }
        }

        return success;
    }

    /**
     * ??????????????????, 6.0??????
     *
     * @param dateTime ??????: date 042817252009.00 2009???04???28??? ????????? 17:25:00 CST
     * @return true / false
     */
    public static boolean setSystemTimeWithOS6(String dateTime,boolean addLog) {
        setChinaTimeZone(addLog);
        boolean success = false;

        try {

            if(addLog){
                LogUtil.d(TAG, "setSystemTimeWithOS6 set system time = " + dateTime);
            }else{
                Log.d(TAG, "setSystemTimeWithOS6 set system time = " + dateTime);
            }

            Process process = Runtime.getRuntime().exec(getRootCommand());
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            if (isAlagroupPlatform()) {
                /// ?????????????????????/system/bin/date???????????????????????????bug.
                os.writeBytes("date " + dateTime + "\n");
                os.writeBytes("/system/bin/hwclock -f /dev/rtc0 -w\n");
            } else {
                os.writeBytes("/system/bin/date " + dateTime + "\n");
            }

            os.writeBytes("exit\n");
            os.flush();
            os.close();

            try {
                int value = process.waitFor();
                if(addLog) {
                    LogUtil.d(TAG, "setSystemTimeWithOS6 set system time waitfor value = " + value);
                }else{
                    Log.d(TAG, "setSystemTimeWithOS6 set system time waitfor value = " + value);
                }
                success = (0 == value);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d(TAG, e.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            if(addLog) {
                LogUtil.d(TAG, "setSystemTimeWithOS6 set system time " + e.toString());
            }else{
                Log.d(TAG, "setSystemTimeWithOS6 set system time " + e.toString());
            }
        }

        return success;
    }
    public static boolean setCurrentTimeMillis(long ms) {
       return setCurrentTimeMillis(ms,true);
    }
    /**
     * ????????????????????????
     *
     * @param ms ??????
     * @return true / false
     */
    public static boolean setCurrentTimeMillis(long ms,boolean addLog) {
        if(addLog){
            LogUtil.d(TAG, "set current time millis ms = " + ms);
        }

        if (ms > 0) {
            Date date = new Date(ms);
            String version = Build.VERSION.RELEASE;
            if (version.length() > 1) {
                String osVersion = version.substring(0, 1);
                if (StringsUtils.toInt(osVersion) >= 6) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmyyyy.ss");
                    String dateString = dateFormat.format(date);
                    if(addLog) {
                        LogUtil.d(TAG, "set current time millis dateString = " + dateString);
                    }
                    return setSystemTimeWithOS6(dateString,addLog);
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd.HHmmss");
                    String dateString = dateFormat.format(date);
                    if(addLog) {
                        LogUtil.d(TAG, "set current time millis dateString = " + dateString);
                    }
                    return setSystemTime(dateString,addLog);
                }
            }
        }

        return false;
    }

    /**
     * ??????????????????dns
     */
    public static InetAddress[] getDnsByCommand() {
        try {
            Process e = Runtime.getRuntime().exec("getprop");
            InputStream inputStream = e.getInputStream();
            LineNumberReader lnr = new LineNumberReader(new InputStreamReader(inputStream));
            String line = null;
            ArrayList servers = new ArrayList(5);

            while (true) {
                String property;
                String value;
                do {
                    int split;
                    do {
                        if ((line = lnr.readLine()) == null) {
                            if (servers.size() > 0) {
                                return (InetAddress[]) servers.toArray(new InetAddress[servers.size()]);
                            }

                            return null;
                        }

                        split = line.indexOf("]: [");
                    } while (split == -1);

                    property = line.substring(1, split);
                    value = line.substring(split + 4, line.length() - 1);
                } while (!property.endsWith(".dns") && !property.endsWith(".dns1") && !property.endsWith(".dns2") && !property.endsWith(".dns3") && !property.endsWith(".dns4"));

                InetAddress ip = InetAddress.getByName(value);
                if (ip != null) {
                    value = ip.getHostAddress();
                    if (value != null && value.length() != 0) {
                        servers.add(ip);
                    }
                }
            }
        } catch (IOException var9) {
            LogUtil.s(TAG, "getDnsByCommand Exception in findDNSByExec:", var9);
            return null;
        }
    }

    public static InetAddress[] getDnsByReflection() {
        try {
            Class e = Class.forName("android.os.SystemProperties");
            Method method = e.getMethod("get", new Class[]{String.class});
            ArrayList servers = new ArrayList(5);
            String[] var3 = new String[]{"net.dns1", "net.dns2", "net.dns3", "net.dns4"};
            int var4 = var3.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                String propKey = var3[var5];
                String value = (String) method.invoke((Object) null, new Object[]{propKey});
                if (value != null && value.length() != 0) {
                    InetAddress ip = InetAddress.getByName(value);
                    if (ip != null) {
                        value = ip.getHostAddress();
                        if (value != null && value.length() != 0 && !servers.contains(ip)) {
                            servers.add(ip);
                        }
                    }
                }
            }

            if (servers.size() > 0) {
                return (InetAddress[]) servers.toArray(new InetAddress[servers.size()]);
            }
        } catch (Exception var9) {
            LogUtil.s(TAG, "getDnsByCommand Exception in findDNSByReflection:", var9);
        }

        return null;
    }

    public static boolean isInstallAppExist(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    public static boolean execCommands(String[] cmds) {
        if (null != cmds && cmds.length > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String temp : cmds) {
                stringBuilder.append(temp + ";");
            }
            LogUtil.d(TAG, "exec commands " + stringBuilder.toString());

            try {
                Process process = Runtime.getRuntime().exec(getRootCommand());
                DataOutputStream os = new DataOutputStream(process.getOutputStream());
                for (int i = 0; i < cmds.length; i++) {
                    String command = cmds[i];
                    if (null != command && command.length() > 0) {
                        os.writeBytes(command + "\n");
                    }
                }

                os.writeBytes("exit\n");
                os.flush();
                os.close();

                try {
                    int value = process.waitFor();
                    if (0 == value) {
                        LogUtil.d(TAG, "exec commands success");

                        return true;
                    } else {
                        LogUtil.d(TAG, "exec command fail");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, "exec command err2 " + e.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d(TAG, "exec command err1 " + e.toString());
            }
        }

        return false;
    }

    private static final String PASSWORD_FILE = "/data/system/password.key";

    public static void clearPassword() {
        if (isPasswordFileExist()) {
            deletePasswordFile();
        }
    }

    public static void deleteSerialLog() {
        try {
            Process process = Runtime.getRuntime().exec(getRootCommand());
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("rm -rf /sdcard/LaidianTerminal/LogFile/Seri*\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();

            try {
                int value = process.waitFor();
                if (0 == value) {
                    Log.d(TAG, "exec success");
                } else {
                    Log.d(TAG, "exec fail");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "deleteSerialLog " + e.toString());
        }
    }

    private static void deletePasswordFile() {
        try {
            LogUtil.d(TAG, "delete password file");

            Process process = Runtime.getRuntime().exec(getRootCommand());
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("rm -rf /data/system/*.key\n");
            os.writeBytes("rm -rf /data/system/locksettings.db\n");
            os.writeBytes("rm -rf /data/system/locksettings.db-shm\n");
            os.writeBytes("rm -rf /data/system/locksettings.db-wal\n");
            os.writeBytes("reboot\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();

            try {
                int value = process.waitFor();
                if (0 == value) {
                    LogUtil.d(TAG, "exec success");
                } else {
                    LogUtil.d(TAG, "exec fail");
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d(TAG, e.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.d(TAG, "set system time " + e.toString());
        }
    }

    private static boolean isPasswordFileExist() {
        try {
            LogUtil.d(TAG, "check passwd exist");

            Process process = Runtime.getRuntime().exec(getRootCommand());
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("chmod 777 " + PASSWORD_FILE + "\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();

            try {
                int value = process.waitFor();
                if (0 == value) {
                    LogUtil.d(TAG, "exec success");
                } else {
                    LogUtil.d(TAG, "exec fail");
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d(TAG, e.toString());
            }

            File file = new File(PASSWORD_FILE);

            return file.exists();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.d(TAG, e.toString());
        }

        return false;
    }



    public static void stopADBService() {
        String[] cmds = {"stop adbd"};
        execCommands(cmds);
    }

    public static void startADBService() {
        String[] cmds = {"start adbd"};
        execCommands(cmds);
    }

    public static void stopADB() {
        stopADBService();
    }

    public static void startADB() {
        startADBService();
    }

    public static boolean isTableExist(Context context, String dbName, String tableName) {
        if (null != tableName && tableName.length() > 0) {
            boolean isExist = true;

            SQLiteDatabase db = context.openOrCreateDatabase(dbName, 0, null);
            Cursor c = db.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name=" + tableName, null);
            if (c.getInt(0) == 0) {
                isExist = false;
            }

            c.close();
            db.close();

            return isExist;
        }

        return false;
    }

    public static void setVolume(final int volume) {
        // ???????????????????????????????????????????????????????????????????????????
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AudioManager audioManager = (AudioManager) UtilsApplication
                            .getInstance()
                            .getApplicationContext()
                            .getSystemService(Context.AUDIO_SERVICE);
                    if (null != audioManager) {
                        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, volume, 0);
                        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, volume, 0);
                        audioManager.setStreamVolume(AudioManager.STREAM_RING, volume, 0);
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, 0);
                        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static void closelVolume() {
        setVolume(0);
    }

    public static void setStreamVolume(int streamType, int volume) {
        try {
            AudioManager audioManager = (AudioManager) UtilsApplication
                    .getInstance()
                    .getApplicationContext()
                    .getSystemService(Context.AUDIO_SERVICE);
            if (null != audioManager) {
                audioManager.setStreamVolume(streamType, volume, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getBrand() {
        return android.os.Build.BRAND;
    }

    public static String getProduct() {
        return Build.PRODUCT;
    }

    public static String getTerminalType() {
        return "";
    }


    public static int getApkVersionCode(String packageName) {
        int versionCode = 0;
        if (null != packageName && packageName.length() > 0) {
            PackageManager pm = UtilsApplication.getInstance().getPackageManager();

            List<PackageInfo> list = pm.getInstalledPackages(0);
            for (PackageInfo packageInfo : list) {
                if (packageInfo.packageName.equalsIgnoreCase(packageName)) {
                    versionCode = packageInfo.versionCode;

                    break;
                }
            }
        }

        return versionCode;
    }

    public static String getClientApkVersionName() {
        return getApkVersionName(PublicConstant.LAIDIAN_CLIENT_APP_NAME);
    }

    public static String getApkVersionName(String packageName) {
        return getApkVersion(packageName);
    }

    public static String getApkVersion(String packageName) {
        String version = "";
        if (null != packageName && packageName.length() > 0) {
            PackageManager pm = UtilsApplication.getInstance().getPackageManager();

            List<PackageInfo> list = pm.getInstalledPackages(0);
            for (PackageInfo packageInfo : list) {
                if (packageInfo.packageName.equalsIgnoreCase(packageName)) {
                    version = packageInfo.versionName;

                    break;
                }
            }
        }

        return version;
    }

    public static String getMapApkVersion(String horizontalPackageApp, String verticalPackageName) {
        String version = "";
        PackageManager pm = UtilsApplication.getInstance().getPackageManager();
        List<PackageInfo> list = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : list) {
            if (packageInfo.packageName.equalsIgnoreCase(horizontalPackageApp)
                    || packageInfo.packageName.equalsIgnoreCase(verticalPackageName)) {
                version = packageInfo.versionName;
                break;
            }
        }

        return version;
    }

    /// get string
    public static String getString(int resId) {
        return UtilsApplication.getInstance().getApplicationContext().getString(resId);
    }



    ///
    public static boolean isDebugable() {
        if (((mContext.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0)) {
            LogUtil.d(TAG, "debugable");
            return true;
        }

        if (android.os.Debug.isDebuggerConnected()) {
            LogUtil.d(TAG, "isDebuggerConnected");
            return true;
        }

        LogUtil.d(TAG, "isDebugable");
        return false;
    }

    public static boolean isRunningInEmulator() {
        LogUtil.d(TAG, "isRunningInEmulator");
        boolean qemuKernel = false;
        Process process = null;
        DataOutputStream os = null;
        InputStream is = null;

        try {
            process = Runtime.getRuntime().exec(getRootCommand());
            os = new DataOutputStream(process.getOutputStream());

            is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            os.writeBytes("getprop ro.kernel.qemu\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();

            String line = br.readLine();
            if (null != line && line.length() > 0) {
                qemuKernel = StringsUtils.toInt(line) == 1;
            }

            is.close();
            isr.close();
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }

                if (null != process) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        LogUtil.d(TAG, "qemuKernel = " + qemuKernel);
        return qemuKernel;
    }

    public static long getRandomTimeInterval(int timeSecond) {
        return new Random().nextInt(timeSecond) * 1000;
    }

    /**
     * 2018-09-17 Jun ??????
     * ??????????????????16???????????????????????????????????????????????????ping?????????unknown host, ?????????????????????????????????
     * ???????????????tun0????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    public static void restartTun0() {
        if (isAlagroupPlatform()) {
            LogUtil.d(TAG, "restartTun0");
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    execCommands(new String[]{"ifconfig tun0 down", "sleep 3", "ifconfig tun0 up"});

                    timer.cancel();
                }
            }, secondToMillisecond(15));
        } else {
            LogUtil.d(TAG, "This machine isn't support restartTun0");
        }
    }


    public static void powerOnLogoHidden() {
        execCommands(new String[]{"for name in `find /system/media -name *.zip`;do mv ${name} ${name}.bak; done"});
    }

    public static void powerOnLogoShow() {
        execCommands(new String[]{"for name in `find /system/media -name \"*.bak\"`;do src_name=`echo ${name} | busybox sed 's/.bak//g'`; mv ${name} ${src_name}; done"});
    }

    public static boolean isApplicationAvilible(String appPackageName) {
        boolean isSuccess = false;
        PackageManager packageManager = UtilsApplication.getInstance().getApplicationContext().getPackageManager();// ??????packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// ???????????????????????????????????????
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                LogUtil.d(TAG, "isApplicationAvilible appPackageName =" + appPackageName + ",pn=" + pn);
                if (appPackageName.equals(pn)) {
                    isSuccess = true;
                }
            }
        }

        if (isInstallAppExist(appPackageName)) {
            LogUtil.d(TAG, " isInstallAppExist true");
            isSuccess = true;
        }
        return isSuccess;

    }



    public static long getFileLastModifyTimestamp(String filePath) {
        if (null != filePath && filePath.length() > 0) {
            return getFileLastModifyTimestamp(new File(filePath));
        }

        return 0;
    }

    public static long getFileLastModifyTimestamp(File file) {
        if (null != file) {
            return file.lastModified();
        }

        return 0;
    }

    public static String getRootCommand() {
        String cmd = "";

        if (isFacePayRom()) {
            if (isCpFacePayRom()) {
                if (FileUtils.isExist("/system/xbin/cl_su")) {
                    cmd = "cl_su";
                }
            } else if (isQzFacePayRom()) {
                if (FileUtils.isExist("/system/xbin/alasu")) {
                    cmd = "alasu";
                }
            }
        }

        if (cmd.length() <= 0) {
            cmd = "su";
        }

        return cmd;
    }

    public static void cleanInternalCacheData() {
        String rooPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        double freeSize = getStorageFreeSizeMB(rooPath);
        LogUtil.d(TAG, "initDataFromThread freeSize =" + freeSize + ",rooPath=" + rooPath);
        if (freeSize < 400.0 && freeSize > 0) {
            FileUtils.clearInternalDataOfApplication();
        }
    }

}

