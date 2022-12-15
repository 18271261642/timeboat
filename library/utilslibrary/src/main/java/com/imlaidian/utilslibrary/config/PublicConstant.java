package com.imlaidian.utilslibrary.config;

import android.os.Environment;



import java.io.File;



public class PublicConstant {
    public static  boolean  openAutoTest = false ;

    public static final String APP_NAME = "Terminal" ;

    public static final String SAVE_APP_DOWNLOAD = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "appDownload" + File.separator;

    public static final String LOCAL_AD_RESOURCES_PATH = "AdResources/AdResConfig.xml";

    public static final String LAIDIAN_CLIENT_APP_NAME = "com.imlaidian.laidianclient" ;

    public static String getSaveAppFolder() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static final String SAVE_FOLDER = "LaidianTerminal";

    public static final String CLIENT_SAVE_FOLDER = "LaidianClient";

    public static final String SAVE_FILE_PATH = getSaveAppFolder() + File.separator + SAVE_FOLDER + File.separator;

    public static final String CLIENT_SAVE_FILE_PATH = getSaveAppFolder() + File.separator + CLIENT_SAVE_FOLDER + File.separator;

    /// 保存log路径
    public static final String SAVE_LOG_DIR_NAME = "LogFile";
//    public static final String SAVE_LOG_FILE_PATH = SAVE_FILE_PATH + SAVE_LOG_DIR_NAME + File.separator;
    public static final String SAVE_SD_LOG_FILE_PATH = File.separator + SAVE_FOLDER + File.separator + SAVE_LOG_DIR_NAME + File.separator;

    /// 保存client log路径
    public static final String CLIENT_SAVE_LOG_FILE_PATH = CLIENT_SAVE_FILE_PATH + "LogFile" + File.separator;

    // 保存截图目录
    public static final String SAVE_SCREEN_SHOT_DIR_PATH = SAVE_FOLDER + File.separator + "ScreenShotFile" + File.separator;

    // .hprof(内存快照)目录
    public static final String SAVE_HEAP_DUMP_PATH = SAVE_FOLDER + File.separator + "HeapDump" + File.separator;

    /// 保存webview cache
    public static final String WEB_VIEW_FILE_PATH = SAVE_FILE_PATH + "WebView" + File.separator;

    /// 异常crash文件路径
    public static final String SAVE_CRASH_PATH = SAVE_FILE_PATH + "TerminalLog.log";

    /// 多任务下载保存路径
    public static final String MULTI_TASK_DOWNLOAD_PATH = SAVE_FILE_PATH + "MultiTask";

    ///client 异常crash文件路径
    public static final String CLIENT_SAVE_CRASH_PATH = CLIENT_SAVE_FILE_PATH + "TerminalLog.log";
    /*
      由于使用Environment.getExternalStorageDirectory().getAbsolutePath()获取的路径，在shell脚本里面不识别，
      因此此处需要使用直接绝对路径。
     */
    public static final String SAVE_FILE_BASH_PATH = "/sdcard" + File.separator + SAVE_FOLDER + File.separator;

    /// 串口日志文件,此路径必须为绝对路径，不能通过java api获取路径拼接
    public static final String SAVE_SERIAL_FILE_NAME = "SerialLog";
    public static final String SAVE_SERIAL_FILE_PATH = "/sdcard/LaidianTerminal/LogFile/" + SAVE_SERIAL_FILE_NAME;

    /// start app 日志,此路径必须为绝对路径，不能通过java api获取路径拼接
    public static final String SAVE_START_APP_LOG_FILE_NAME = "startApp.log";
    public static final String SAVE_START_APP_LOG_ROOT_PATH = "/sdcard/LaidianTerminal/LogFile/CheckAppProcess/";
    public static final String SAVE_START_APP_LOG_FILE_PATH = SAVE_START_APP_LOG_ROOT_PATH + SAVE_START_APP_LOG_FILE_NAME;

    //查看本地磁盘文件大小的数量
    public static final int FILE_SIZE_COUNT = 30;

    public static final int scanQrMapWidth = 80;
    public static final int scanQrMapHeight = 80;

    public static final int scanQr4KMapWidth = 200;
    public static final int scanQr4KMapHeight = 200;

    public static final int advertiseSmallWidth = 100;
    public static final int advertiseSmallHeight = 100;

    public static final int scanQrMiddleWidth = 155;
    public static final int scanQrMiddleHeight = 155;

    public static final int threeScanQrMiddleWidth = 120;
    public static final int threeScanQrMiddleHeight = 120;

    public static final int threeScanQrLargeWidth = 185 ;
    public static final int threeScanQrLargeHeight= 185 ;

    public static final int faceScanQrSmallWidth = 134;
    public static final int faceScanQrSmallHeight = 134;

    public static final int faceScanQrMiddleWidth = 274;
    public static final int faceScanQrMiddleHeight = 274;

    public static final int faceScanQrLargerWidth = 360;
    public static final int faceScanQrLargerHeight = 360;

    public static final int scanQrLargeWidth = 300;
    public static final int scanQrLargeHeight = 300;
    public static final int scanQrLargeWidthHeight = 250;



    /**二维码 start**/
    public static final int QR_VERSION_TYPE_DEFAULT = 0;
    // 微信
    public static final int QR_VERSION_TYPE_WECHAT = 1;
    // ali
    public static final int QR_VERSION_TYPE_ALIPAY = 2;
    //微信和ali
    public static final int QR_VERSION_TYPE_WECHAT_ALIPAY = 3;
    //boao
    public static final int QR_VERSION_TYPE_BOAO = 4;
    //微信和ali和360
    public static final int QR_VERSION_TYPE_WECHAT_ALIPAY_360 = 5;
    //KA 模式
    public static final int QR_VERSION_TYPE_UNION_BRAND =6 ;
    //二码合一
    public static final int QR_VERSION_TYPE_SINGLE_UNION = 7 ;
    //微信人脸识别
    public static final int QR_VERSION_TYPE_FACE_IDENTIFY = 8 ;
    //支付宝人脸识别
    public static final int QR_VERSION_TYPE_ALI_FACE = 9 ;
    //微信人脸识别腾讯独家
    public static final int QR_VERSION_TYPE_FACE_TENCENT_EXCLUSIVE = 10 ;

    //二维码功能/用途类型
    //租借二维码
    public static final int QR_FUNCTION_TYPE_BORROW = 1;
    //附近网点二维码
    public static final int QR_FUNCTION_TYPE_NEAR_SHOP = 2;
    //回收仓二维码
    public static final int QR_FUNCTION_TYPE_RECYCLE = 3;
    //客服二维码
    public static final int QR_FUNCTION_TYPE_CUSTOM_SERVICE = 4;

    //二维码模板类型
    //双二维码模式：有两个二维码
    public static final int QR_TEMPLATE_TYPE_DOUBLE_QRCODE = 1;
    //左按钮+右二维码模式：一个按钮加一个二维码
    public static final int QR_TEMPLATE_TYPE_LEFT_BTN = 2;
    //右按钮+左二维码模式：一个按钮加一个二维码
    public static final int QR_TEMPLATE_TYPE_RIGHT_BTN = 3;
    //单二维码模式：只有一个二维码
    public static final int QR_TEMPLATE_TYPE_SINGLE_QRCODE = 4;
    //单按钮模式：只有一个按钮
    public static final int QR_TEMPLATE_TYPE_SINGLE_BTN = 5;
    /**
     * 二维码 end
     **/



    // 中国的请求类型
    public static final  int  REQUEST_IN_CHINA_TYPE = 1 ;
    // 海外的 请求类型 请求两个 接口
    public static final  int REQUEST_IN_SEA_TYPE =2 ;

    //VideoList
    public static final int  SELF_INTRODUCE_ADVERTISE_TYPE = 0 ;
    //自己的广告
    public static final int  SELF_ADVERTISE_TYPE =1 ;


    // 上区域屏幕
    public static final int TOP_ADVERTISE_POSITION =1 ;
    // 下区域屏幕
    public static final int BOTTOM_ADVERTISE_POSITION =2 ;
    // 全部区域屏幕
    public static final int FULL_ADVERTISE_POSITION =3 ;

    // 位置区域屏幕
    public static final int UNKNOW_ADVERTISE_POSITION =0 ;


    //空白
    public static final int ADVERTISE_TYPE_NONE = 0;

    /// 图片
    public static final int ADVERTISE_TYPE_IMG = 1;

    /// 视频
    public static final int ADVERTISE_TYPE_VIDEO = 2;

    /// GIF
    public static final int ADVERTISE_TYPE_GIF = 3;


    public static final int DNS_REQUEST_TYPE = 1 ;
    public static final int NORMAL_REQUEST_TYPE = 0 ;

    public static final  String  LANGUAGE_CHINESE ="zh" ;

    public static final  String  LANGUAGE_ENGLISH="en" ;
    /**
     * 七牛上传token, 99999小时
     */
    public static final String QI_NIU_LOG_UPLOAD_TOKEN = "A8SIAnj_MoLaBFRnPlmdCi78eLSUdY57VbMgFJZy:NjZHsexPCrdKcmBRio9yrAr4bDQ=:eyJzY29wZSI6ImxvZ3NkYXRhIiwiZGVhZGxpbmUiOjE4MzA3MTA4MzV9";
    public static final String QI_NIU_LOG_UPLOAD_IP = "183.136.139.10";

    public static  int wanDaSkinTheme = 1  ;

    public static  int NormalSkinTheme = 0 ;

    public static int SKIN_THEME = NormalSkinTheme ;


    //归还充电宝时各个状态
    public static final int GiveBackListenSend = 100;
    public static final int GiveBackSuccessStart = 200;
    public static final int GiveBackSuccessEnd = 300;
    public static final int GiveBackFinishResult = 400;

    public static final String NETTY_COMMUNICATE_PROXY = "/netty/nettyCommunicate";

}
