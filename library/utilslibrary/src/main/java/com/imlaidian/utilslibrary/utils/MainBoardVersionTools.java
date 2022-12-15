package com.imlaidian.utilslibrary.utils;
import android.util.DisplayMetrics;
import com.imlaidian.utilslibrary.UtilsApplication;
import static com.imlaidian.utilslibrary.utils.SystemTool.getInnerVersion;
import static com.imlaidian.utilslibrary.utils.SystemTool.getSystemVersion;

public class MainBoardVersionTools {
    /// 主板 类型
    /// 群智
    public static final int PLATFORM_TYPE_ALAGROUP = 0;
    /// 宏电
    public static final int PLATFORM_TYPE_HONGDIAN = 1;
    /// 四信
    public static final int PLATFORM_TYPE_FOURFAITH = 2;
    /// 小薇打印照片，rk3288
    public static final int PLATFORM_TYPE_XIAOWEI = 3;
    /// 小薇打印照，mtk
    public static final int PLATFORM_TYPE_XWFOOTBALLPRINTER = 4;
    /// 来电平台
    public static final int PLATFORM_TYPE_LAIDIAN = 5;
    /// 创品
    public static final int PLATFORM_TYPE_CHUANGPIN = 6;
    /// 通用平台
    public static final int PLATFORM_TYPE_PUBLIC = -1;
    /// 无效
    public static final int PLATFORM_TYPE_INVALID = -2;
    private static int mPlatformType = PLATFORM_TYPE_INVALID;
    private static final Object mPlatformTypeSynchronized = new Object();

    public static int getPlatformType() {
        if (PLATFORM_TYPE_INVALID == mPlatformType) {
            synchronized (mPlatformTypeSynchronized) {
                if (PLATFORM_TYPE_INVALID == mPlatformType) {
                    /// 默认为群智
                    mPlatformType = PLATFORM_TYPE_ALAGROUP;

                    String version = getInnerVersion();
                    String searchStr = ")_";
                    int index = version.indexOf(searchStr);
                    if (index >= 0) {
                        String tail = version.substring(index + searchStr.length());
                        String seperatorStr = "_";
                        String[] subItems = tail.split(seperatorStr);
                        if (subItems.length > 0) {
                            String identifier = subItems[subItems.length - 1];
                            if (identifier.equalsIgnoreCase("hongdian")) {
                                /// 宏电
                                mPlatformType = PLATFORM_TYPE_HONGDIAN;
                            } else if (identifier.equalsIgnoreCase("fourfaith")) {
                                /// 四信
                                mPlatformType = PLATFORM_TYPE_FOURFAITH;
                            } else if (identifier.equalsIgnoreCase("laidian")) {
                                /// 来电平台
                                mPlatformType = PLATFORM_TYPE_LAIDIAN;
                            } else if (identifier.equalsIgnoreCase("chuangpin")||identifier.equalsIgnoreCase("ys")){
                                /// 创品
                                mPlatformType = PLATFORM_TYPE_CHUANGPIN;
                            }
                        }
                    } else if (version.toLowerCase().startsWith(ROMTYPE_XW_L_43_XIAOWEI.toLowerCase())) {
                        /// 小薇照片平台
                        mPlatformType = PLATFORM_TYPE_XIAOWEI;
                    } else if (version.toLowerCase().startsWith(ROMTYPE_XW_M_FOOTBALL_PRINTER.toLowerCase())
                            || version.toLowerCase().startsWith("CB".toLowerCase())) {
                        mPlatformType = PLATFORM_TYPE_XWFOOTBALLPRINTER;
                    }
                }
            }
        }

        return mPlatformType;
    }

    public static boolean isCurrentPlatformEqualPlatformType(int platformType) {
        return getPlatformType() == platformType || PLATFORM_TYPE_PUBLIC == platformType;
    }

    /// 判断是否群智平台
    public static boolean detectionIsAlagroupPlatform(int platformType) {
        return PLATFORM_TYPE_ALAGROUP == platformType;
    }

    /// 判断是否宏电平台
    public static boolean detectionIsHongdianPlatform(int platformType) {
        return PLATFORM_TYPE_HONGDIAN == platformType;
    }

    /// 判断是否四信平台
    public static boolean detectionIsFourfaithPlatform(int platformType) {
        return PLATFORM_TYPE_FOURFAITH == platformType;
    }

    /// 判断是否通用平台
    public static boolean detectionIsPublicPlatform(int platformType) {
        return PLATFORM_TYPE_PUBLIC == platformType;
    }

    /// 判断是否小薇平台
    public static boolean detectionIsXiaoweiPlatform(int platformType) {
        return PLATFORM_TYPE_XIAOWEI == platformType;
    }

    /// 是否群智平台
    public static boolean isAlagroupPlatform() {
        return getPlatformType() == PLATFORM_TYPE_ALAGROUP;
    }

    /// 是否宏电平台
    public static boolean isHongdianPlatform() {
        return getPlatformType() == PLATFORM_TYPE_HONGDIAN;
    }

    /// 是否四信平台
    public static boolean isFourfaithPlatform() {
        return getPlatformType() == PLATFORM_TYPE_FOURFAITH;
    }

    /// 是否小薇平台
    public static boolean isXiaoWeiPlatform() {
        return getPlatformType() == PLATFORM_TYPE_XIAOWEI;
    }

    /// 是否小薇足球彩票机
    public static boolean isXiaoWeiFootballPrinterPlatform() {
        return getPlatformType() == PLATFORM_TYPE_XWFOOTBALLPRINTER;
    }

    /// 是否来电平台
    public static boolean isLaidianPlatform() {
        return getPlatformType() == PLATFORM_TYPE_LAIDIAN;
    }

    /// 是否创品平台
    public static boolean isChuangPinPlatform() {
        return getPlatformType() == PLATFORM_TYPE_CHUANGPIN;
    }

    //--------- 系统 rom 类型
    private static String mRomType = null;
    // ==== LDX-20 - 群智
    public static String ROMTYPE_LD_L_43_HID = "LD_L_43_HID";
    public static String ROMTYPE_LD_L_43_HID_SYS6 = "LD_L_43_HID_SYS6"; //S912 8核
    public static String ROMTYPE_LD_L_43_HID_S905D_SYS6 = "LD_L_43_S905D_SYS6"; //S905D 4核
    public static String ROMTYPE_LD_L_43_HID_RK3288 = "LD_L_43_HID_RK3288";
    // ==== LDX-20 - 宏电
    public static String ROMTYPE_LD_L_43_HID_HONGDIAN = "LD_L_43_HID_HONGDIAN";
    // ==== LDX-20 - 四信
    public static String ROMTYPE_LD_L_43_HID_FOURFAITH = "LD_L_43_HID_FOURFAITH";
    // ==== LDX-20 - 来电
    public static String ROMTYPE_LD_L_43_HID_LAIDIAN = "LD_L_43_HID_LAIDIAN";
    public static String ROMTYPE_LD_L_43_HID_SYS6_LAIDIAN = "LD_L_43_HID_SYS6_LAIDIAN";

    // ==== LDX-30 - 群智
    public static String ROMTYPE_LD_L_43_THIRTY_HID = "LD_L_43_THIRTY_HID";
    public static String ROMTYPE_LD_L_43_THIRTY_S905D_SYS6 = "LD_L_43_THIRTY_S905D_SYS6";
    public static String ROMTYPE_LD_L_43_THIRTY_HID_RK3288 = "LD_L_43_THIRTY_HID_RK3288";
    // ==== LDX-30 - 来电
    public static String ROMTYPE_LD_L_43_THIRTY_HID_LAIDIAN = "LD_L_43_THIRTY_HID_LAIDIAN";
    public static String ROMTYPE_LD_L_43_THIRTY_HID_SYS6_LAIDIAN = "LD_L_43_THIRTY_HID_LAIDIAN";

    // ==== LDX-20 - 小薇打印照片机器43寸
    public static String ROMTYPE_XW_L_43_XIAOWEI = "rk3288-userdebug";
    // ==== NAS-SAY-25 - 食安云, 群智
    public static String ROMTYPE_LD_L_43_THIRTY_SHIANYUN = "LD_L_43_THIRTY_SHIANYUN";
    // ==== NAS-SAY-25-A - 食安云, 创品
    public static String ROMTYPE_LD_L_43_MTK_SHIANYUN_CHUANGPIN = "LD_L_43_MTK_SHIANYUN_CHUANGPIN"; //创品

    // ==== GGJ-02-A 创品 mtk 广告机
    public static String ROMTYPE_LD_L_AD_LVDS_VER_MTK_CHUANGPIN = "LD_L_AD_LVDS_VER_MTK_CHUANGPIN"; //创品 mtk 广告 竖屏机器

    // ==== LDZ-40 - 群智
    public static String ROMTYPE_LD_L_27_HG = "LD_L_27_HG";
    public static String ROMTYPE_LD_L_27_HID = "LD_L_27_HID";
    public static String ROMTYPE_LD_L_27_S905D_SYS6 = "LD_L_27_S905D_SYS6"; //S905D 4核
    public static String ROMTYPE_LD_L_27_HID_RK3288 = "LD_L_27_HID_RK3288";
    // ==== LDZ-40 - 宏电
    public static String ROMTYPE_LD_L_27_HID_HONGDIAN = "LD_L_27_HID_HONGDIAN";
    // ==== LDZ-40 - 四信
    public static String ROMTYPE_LD_L_27_HID_FOURFAITH = "LD_L_27_HID_FOURFAITH";
    // ==== LDZ-40 - 来电
    public static String ROMTYPE_LD_L_27_HID_LAIDIAN = "LD_L_27_HID_LAIDIAN";
    public static String ROMTYPE_LD_L_27_HID_SYS6_LAIDIAN = "LD_L_27_HID_SYS6_LAIDIAN";

    // ==== LDZ-12 - 群智, 对应中型机10寸
    public static String ROMTYPE_LD_M_10 = "LD_M_10";
    // ==== LDZ-8 - 群智
    public static String ROMTYPE_LD_M_10_HID = "LD_M_10_HID";
    public static String ROMTYPE_LD_M_AM5300 = "LD_M_AM5300";
    public static String ROMTYPE_LD_M_10_HID_HONGDIAN = "LD_M_10_HID_HONGDIAN";
    public static String ROMTYPE_LD_M_10_HID_FOURFAITH = "LD_M_10_HID_FOURFAITH";

    // ==== LDX-16 - 群智, 对应15.4寸 中型机器
    public static String ROMTYPE_LD_L_15 = "LD_L_15";
    public static String ROMTYPE_LD_L_15_HID = "LD_L_15_HID";
    public static String ROMTYPE_LD_L_154_HID = "LD_L_154_HID";
    public static String ROMTYPE_LD_L_15_HDMI = "LD_L_15_HDMI";
    public static String ROMTYPE_LD_L_15_SYS6 = "LD_L_15_SYS6"; //S905D 4核
    public static String ROMTYPE_LD_L_15_24_LVDS = "LD_L_15_24_LVDS";
    public static String ROMTYPE_LD_L_15_24_SYS6 = "LD_L_15_24_SYS6";
    public static String ROMTYPE_LD_L_154_SYS6 = "LD_L_154_SYS6"; //S905D 4核 154
    public static String ROMTYPE_LD_L_15_RK3288 = "LD_L_15_RK3288";
    // ==== LDX-16 - 宏电
    public static String ROMTYPE_LD_L_15_HDMI_HONGDIAN = "LD_L_15_HDMI_HONGDIAN";
    public static String ROMTYPE_LD_L_15_LVDS = "LD_L_15_LVDS";
    // ==== LDX-16 - 来电
    public static String ROMTYPE_LD_L_15_LAIDIAN = "LD_L_15_LAIDIAN";
    public static String ROMTYPE_LD_L_15_SYS6_LAIDIAN = "LD_L_15_SYS6_LAIDIAN";
    public static String ROMTYPE_LD_L_15_24_LAIDIAN = "LD_L_15_24_LAIDIAN";
    public static String ROMTYPE_LD_L_15_24_SYS6_LAIDIAN = "LD_L_15_24_SYS6_LAIDIAN";

    // ==== LDZ-6 - 群智
    public static String ROMTYPE_LD_S_NONE = "LD_S_NONE";
    // ==== LDX-8 - 群智
    public static String ROMTYPE_LD_S_NONE_AM5400 = "LD_S_NONE_AM5400";
    // ==== LDX-8 - 宏电
    public static String ROMTYPE_LD_S_NONE_HONGDIAN = "LD_S_NONE_HONGDIAN";
    // ==== LDX-8 - 来电
    public static String ROMTYPE_LD_S_NONE_LAIDIAN = "LD_S_NONE_LAIDIAN";

    // ==== LDX-60 - 群智
    public static String ROMTYPE_LD_L_21_HID = "LD_L_21_HID";
    public static String ROMTYPE_LD_L_21_S905D_SYS6 = "LD_L_21_S905D_SYS6";
    public static String ROMTYPE_LD_L_21_HID_RK3288 = "LD_L_21_HID_RK3288";
    // ==== LDX-60 - 来电
    public static String ROMTYPE_LD_L_21_HID_LAIDIAN = "LD_L_21_HID_LAIDIAN";

    /// 小薇，足球彩票打印机
    public static String ROMTYPE_XW_M_FOOTBALL_PRINTER = "CB010_FBPT_";

    // ==== DHJ-40 - 群智
    public static String ROMTYPE_LD_L_55_HID = "LD_L_55_CAP_SYS6";
    // ==== DHJ-40 - 来电
    public static String ROMTYPE_LD_L_55_LAIDIAN = "LD_L_55_CAP_LAIDIAN";
    // ==== DHJ-40 - 群智, 支持4K屏幕
    public static String ROMTYPE_LD_L_55_CAP_4K_SYS6 = "LD_L_55_CAP_4K_SYS6";
    // ==== DHJ-40 - 群智, 4K显示成1920x1080，为石家庄万达定制的版本
    public static String ROMTYPE_LD_L_55_CAP_4K_TMP1920x1080_SYS6 = "LD_L_55_CAP_4K_TMP1920x1080_SYS6";
    // ==== DHJ-40 - 群智, RK3399 4K
    public static String ROMTYPE_LD_L_55_CAP_SYS7_RK3399 = "LD_L_55_CAP_SYS7_RK3399";
    // ==== DHJ-40 - 群智, RK3399 2K
    public static String ROMTYPE_LD_L_55_CAP_SYS7_2K_RK3399 = "LD_L_55_CAP_SYS7_2K_RK3399";
    // ==== DHJ-40 - 群智, 导航机无租借部分
    public static String ROMTYPE_LD_L_55_CAP_4K_NO_SLOT = "LD_L_55_CAP_4K_NO_SLOT";
    // ==== DHJ-40-A - 创品
    public static String ROMTYPE_LD_L_55_CAP_4K_CHUANGPIN = "LD_L_55_CAP_4K_CHUANGPIN";
    // ==== DHJ-40-A - 创品, 导航机无租借部分
    public static String ROMTYPE_LD_L_55_CAP_4K_NO_SLOT_CHUANGPIN = "LD_L_55_CAP_4K_NO_SLOT_CHUANGPIN";

    // ==== DHJ-18 - 群智
    public static String ROMTYPE_LD_L_CAP_27_HID = "LD_L_27_CAP_SYS6";
    public static String ROMTYPE_LD_L_27_CAP_NO_SLOT = "LD_L_27_CAP_NO_SLOT"; // 无租借
    // ==== DHJ-18 - 来电
    public static String ROMTYPE_LD_L_CAP_27_LAIDIAN = "LD_L_27_CAP_LAIDIAN";
    // ==== DHJ-18 - 群智, RK3399 导航机
    public static String ROMTYPE_LD_L_27_CAP_SYS7_RK3399 = "LD_L_27_CAP_SYS7_RK3399";
    // ==== DHJ-18-A - 创品
    public static String ROMTYPE_LD_L_27_CAP_CHUANGPIN = "LD_L_27_CAP_CHUANGPIN";
    // ==== DHJ-24-A - 创品
    public static String ROMTYPE_LD_L_27_CAP_SLOT24_CHUANGPIN = "LD_L_27_CAP_SLOT24_CHUANGPIN";
    public static String ROMTYPE_LD_L_27_CAP_NO_SLOT_CHUANGPIN = "LD_L_27_CAP_NO_SLOT_CHUANGPIN";

    //贵州机场项目24口¨
    public static String  ROMTYPE_LD_M_10_24_RK3399_CHUANGPIN = "LD_M_10_24_RK3399_CHUANGPIN" ;

    // ==== NAS-50 - 群智
    public static String ROMTYPE_LD_L_43_SAY_MODEL_FIFTY = "LD_L_43_SAY_MODEL_FIFTY";
    public static String ROMTYPE_LD_L_43_FACE_PAY = "LD_L_43_FACE_PAY"; //微信，群智
    public static String ROMTYPE_LD_L_43_FACE_ALIPAY = "LD_L_43_FACE_ALIPAY"; //支付宝，群智

    public static String ROMTYPE_LD_L_43_SAY_MODEL_FIFTY_RK3288 = "LD_L_43_SAY_MODEL_FIFTY_RK3288";
    // ==== NAS-50 - 来电
    public static String ROMTYPE_LD_L_43_SAY_MODEL_FIFTY_LAIDIAN = "LD_L_43_SAY_MODEL_FIFTY_LAIDIAN";
    // ==== NAS-SAY-25 - 群智, 食安云换来电主板
    public static String ROMTYPE_LD_L_43_SAY_MODEL_TWENTYFIVE_RK3288 = "LD_L_43_SAY_MODEL_TWENTYFIVE_RK3288";

    // ==== NAS-50-A - 创品
    public static String ROMTYPE_LD_L_40_FIFTY_RK3288_CAM_CHUANGPIN = "LD_L_40_FIFTY_RK3288_CAM_CHUANGPIN";
    // ==== NAS-50-A - 创品, RK3288 去掉微信sdk
    public static String ROMTYPE_LD_L_40_FIFTY_RK3288_NO_CAM_CHUANGPIN = "LD_L_40_FIFTY_RK3288_NO_CAM_CHUANGPIN";
    // ==== NAS-50-A - 创品, 支付宝
    public static String ROMTYPE_LD_L_43_FACE_ALIPAY_CHUANGPIN = "LD_L_43_FACE_ALIPAY_CHUANGPIN";
    // ==== NAS-50-A - 创品 亿晟主板
    public static String ROMTYPE_LD_L_40_FIFTY_RK3288_CAM_CP_YS = "LD_L_40_FIFTY_RK3288_CAM_CP_YS";

    // ==== NAS-30-A - 创品
    public static String ROMTYPE_LD_L_15_THIRTY_RK3288_CAM_CHUANGPIN = "LD_L_15_THIRTY_RK3288_CAM_CHUANGPIN";
    public static String ROMTYPE_LD_L_15_THIRTY_RK3288_NO_CAM_CHUANGPIN = "LD_L_15_THIRTY_RK3288_NO_CAM_CHUANGPIN";
    public static String ROMTYPE_LD_L_15_FACE_ALIPAY_CHUANGPIN = "LD_L_15_FACE_ALIPAY_CHUANGPIN";
    // ==== NAS-50-A - 创品 亿晟主板
    public static String ROMTYPE_LD_L_15_THIRTY_RK3288_CAM_CP_YS = "LD_L_15_THIRTY_RK3288_CAM_CP_YS";

    // ==== GGJ-01 - 群智
    /// 广告机设备(横屏)
    public static String ROMTYPE_LD_L_SYS6_AD_HDMI_HOR = "LD_L_SYS6_AD_HDMI_HOR";
    /// 广告机设备(横屏)
    public static String ROMTYPE_LD_L_SYS6_HDMI_HOR = "LD_L_SYS6_HDMI";
    /// 广告机设备(竖屏)
    public static String ROMTYPE_LD_L_SYS6_AD_LVDS_VER = "LD_L_SYS6_AD_LVDS_VER";

    /**
     * 获取系统rom类型
     *
     * @return 系统类型，如上定义
     */
    public static String getRomType() {
        if (null == mRomType) {
            synchronized (SystemTool.class) {
                if (null == mRomType) {
                    String version = getInnerVersion();
                    String searchStr = ")_";
                    int index = version.indexOf(searchStr);
                    if (index >= 0) {
                        mRomType = version.substring(index + searchStr.length());
                    } else if (version.toLowerCase().startsWith(ROMTYPE_XW_L_43_XIAOWEI.toLowerCase())) {
                        mRomType = ROMTYPE_XW_L_43_XIAOWEI;
                    } else if (version.toLowerCase().startsWith(ROMTYPE_XW_M_FOOTBALL_PRINTER.toLowerCase())
                            || version.toLowerCase().startsWith("CB".toLowerCase())) {
                        mRomType = ROMTYPE_XW_M_FOOTBALL_PRINTER;
                    } else {
                        /// 作个兼容
                        DisplayMetrics dm = UtilsApplication.getInstance().getApplicationContext().getResources().getDisplayMetrics();
                        int screenPixelWidth = dm.widthPixels; // 宽
                        int screenPixelHeight = dm.heightPixels + 48; // 高 机器本身获取的dpi 有问题 需要增加 状态栏 48

                        if (screenPixelWidth > 1280 && screenPixelHeight > 800) {
                            mRomType = ROMTYPE_LD_L_27_HG;
                        } else if (screenPixelWidth >= 1080 && screenPixelHeight >= 1920) {
                            mRomType = ROMTYPE_LD_L_43_HID;
                        } else if (screenPixelWidth <= 800 && screenPixelWidth > 600
                                && screenPixelHeight <= 1280 && screenPixelHeight > 1024) {
                            mRomType = ROMTYPE_LD_L_154_HID;
                        } else if (screenPixelWidth <= 1280 && screenPixelWidth > 1024
                                && screenPixelHeight <= 720 && screenPixelHeight > 600) {
                            mRomType = ROMTYPE_LD_S_NONE;
                        } else if (screenPixelHeight <= 600 && screenPixelWidth <= 1024) {
                            mRomType = ROMTYPE_LD_M_10;
                        } else {
                            mRomType = ROMTYPE_LD_M_10;
                        }
                    }
                }
            }
        }

        return mRomType;
    }

    /**
     * 共7位数字（0 - 6）
     * 第5-6位表示供应商类型
     * 第3-4位表示主板类型
     * 第0-2位表示系统类型
     */
    public static int getPlatformIntValue() {
        int basePlatform = 0;
        int boardType = 0;
        int osType = 0;

        if (isHongdianPlatform()) {
            basePlatform = 2;
        } else if (isFourfaithPlatform()) {
            basePlatform = 3;
        } else {
            basePlatform = 1;
        }

        basePlatform *= 1000000;

        if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_HG)) {
            boardType = 10;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_HID)) {
            boardType = 11;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_S905D_SYS6)) {
            boardType = 12;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_HID_SYS6_LAIDIAN)) {
            boardType = 13;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_HID_HONGDIAN)) {
            boardType = 14;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_HID_FOURFAITH)) {
            boardType = 15;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_HID_LAIDIAN)) {
            boardType = 16;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_HID_HONGDIAN)) {
            boardType = 17;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_HID_FOURFAITH)) {
            boardType = 18;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_HID_SYS6)) {
            boardType = 19;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_HID)) {
            boardType = 20;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_XW_L_43_XIAOWEI)) {
            boardType = 21;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_THIRTY_S905D_SYS6)) {
            boardType = 22;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_HID_SYS6_LAIDIAN)) {
            boardType = 23;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_THIRTY_HID_SYS6_LAIDIAN)) {
            boardType = 24;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_HID_S905D_SYS6)) {
            boardType = 25;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_MTK_SHIANYUN_CHUANGPIN)) {
            boardType = 26;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_SAY_MODEL_FIFTY)) {
            boardType = 27;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_SAY_MODEL_FIFTY_LAIDIAN)) {
            boardType = 28;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_FACE_PAY)) {
            boardType = 29;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_M_10)) {
            boardType = 30;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_M_10_HID)) {
            boardType = 31;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_M_AM5300)) {
            boardType = 32;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_XW_M_FOOTBALL_PRINTER)) {
            boardType = 33;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15)) {
            boardType = 40;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_HID)) {
            boardType = 41;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_HDMI)) {
            boardType = 42;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_154_HID)) {
            boardType = 43;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_LVDS)) {
            boardType = 44;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_LAIDIAN)) {
            boardType = 45;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_SYS6)) {
            boardType = 46;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_SYS6_LAIDIAN)) {
            boardType = 47;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_154_SYS6)) {
            boardType = 48;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_THIRTY_RK3288_CAM_CHUANGPIN)) {
            boardType = 49;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_S_NONE)) {
            boardType = 50;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_S_NONE_AM5400)) {
            boardType = 51;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_S_NONE_LAIDIAN)) {
            boardType = 52;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_THIRTY_RK3288_NO_CAM_CHUANGPIN)) {
            boardType = 53;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_21_HID)) {
            boardType = 60;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_21_HID_LAIDIAN)) {
            boardType = 61;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_21_S905D_SYS6)) {
            boardType = 62;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_SAY_MODEL_TWENTYFIVE_RK3288)) {
            boardType = 63;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_HID_RK3288)) {
            boardType = 64;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_THIRTY_HID_RK3288)) {
            boardType = 65;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_HID_RK3288)) {
            boardType = 66;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_RK3288)) {
            boardType = 67;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_21_HID_RK3288)) {
            boardType = 68;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_SAY_MODEL_FIFTY_RK3288)) {
            boardType = 69;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_THIRTY_HID)) {
            boardType = 70;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_HID_LAIDIAN)) {
            boardType = 71;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_THIRTY_HID_LAIDIAN)) {
            boardType = 72;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_THIRTY_SHIANYUN)) {
            boardType = 73;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_FACE_ALIPAY)) {
            boardType = 74;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_M_10_HID_HONGDIAN)) {
            boardType = 75;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_M_10_HID_FOURFAITH)) {
            boardType = 76;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_HDMI_HONGDIAN)) {
            boardType = 77;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_24_LVDS)) {
            boardType = 78;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_24_LAIDIAN)) {
            boardType = 79;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_24_SYS6)) {
            boardType = 80;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_24_SYS6_LAIDIAN)) {
            boardType = 81;
        } else if (getRomType().equalsIgnoreCase(ROMTYPE_LD_S_NONE_HONGDIAN)) {
            boardType = 82;
        }

        boardType *= 1000;

        int osValue = 0;
        String osVersion = getSystemVersion();
        if (null != osVersion) {
            osVersion = osVersion.replace(".", "");
            osValue = StringsUtils.toInt(osVersion);
        }

        return basePlatform + boardType + osValue;
    }


    public static boolean isADHDMIHorRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_SYS6_AD_HDMI_HOR) || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_SYS6_HDMI_HOR);
    }

    public static boolean isADLVDSVerRom() {
        return isQzAdLVDSVerRom() || isCpAdLVDSVerRom();
    }

    public static boolean isQzAdLVDSVerRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_SYS6_AD_LVDS_VER);
    }

    public static boolean isCpAdLVDSVerRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_AD_LVDS_VER_MTK_CHUANGPIN);
    }

    public static boolean isMiddleLargeScreenRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_HID)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_RK3288)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_154_HID)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_HDMI)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_HDMI_HONGDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_LVDS)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_LAIDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_154_SYS6)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_SYS6)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_SYS6_LAIDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_24_LVDS)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_24_LAIDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_24_SYS6)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_24_SYS6_LAIDIAN);
    }

    //群智的hdmi 15.4
    public static boolean isMiddleLargeScreenHdmiRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_HDMI);
    }

    /// 15.4的设备，对应24个通道
    public static boolean isMiddleLarge24SlotRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_24_LVDS)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_24_LAIDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_24_SYS6)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_24_SYS6_LAIDIAN);
    }

    /**
     * 判断是否无屏幕的rom
     *
     * @return true / false
     */
    public static boolean isSmallNoneScreenRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_S_NONE)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_S_NONE_AM5400)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_S_NONE_HONGDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_S_NONE_LAIDIAN);
    }

    public static boolean isSmallNoneAM5400Rom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_S_NONE_AM5400)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_S_NONE_HONGDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_S_NONE_LAIDIAN);
    }

    /**
     * 判断是否大屏的rom
     *
     * @return true / false
     */
    public static boolean isLargeScreenRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_HID)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_HID_RK3288)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_HG)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_HID_HONGDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_HID_FOURFAITH)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_HID_LAIDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_21_HID)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_21_HID_RK3288)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_21_HID_LAIDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_S905D_SYS6)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_HID_SYS6_LAIDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_21_S905D_SYS6);
    }

    /**
     * 判断是否是大屏广告机的Rom
     * return false /true
     */
    public static boolean isLargerAdvertiseScreenRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_HID)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_HID_HONGDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_HID_FOURFAITH)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_HID_SYS6)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_HID_RK3288)
                || getRomType().equalsIgnoreCase(ROMTYPE_XW_L_43_XIAOWEI)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_THIRTY_HID)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_THIRTY_HID_RK3288)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_THIRTY_S905D_SYS6)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_HID_LAIDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_HID_SYS6_LAIDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_THIRTY_HID_LAIDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_HID_S905D_SYS6)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_THIRTY_SHIANYUN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_MTK_SHIANYUN_CHUANGPIN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_SAY_MODEL_FIFTY)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_SAY_MODEL_FIFTY_RK3288)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_SAY_MODEL_FIFTY_LAIDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_FACE_PAY)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_FACE_ALIPAY)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_40_FIFTY_RK3288_CAM_CHUANGPIN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_40_FIFTY_RK3288_CAM_CP_YS)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_40_FIFTY_RK3288_NO_CAM_CHUANGPIN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_SAY_MODEL_TWENTYFIVE_RK3288)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_THIRTY_RK3288_CAM_CHUANGPIN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_THIRTY_RK3288_CAM_CP_YS)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_THIRTY_RK3288_NO_CAM_CHUANGPIN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_FACE_ALIPAY_CHUANGPIN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_FACE_ALIPAY_CHUANGPIN);
    }

    public static boolean isSayModeFifty3288() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_SAY_MODEL_FIFTY_RK3288);
    }


    /// 是否是食安云项目
    public static boolean isShiAnYunRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_THIRTY_SHIANYUN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_MTK_SHIANYUN_CHUANGPIN);
    }

    //是否是刷脸设备，包括群智的和创品的
    public static boolean isFacePayRom() {
        return isQzFacePayRom() || isCpFacePayRom();
    }

    // 群智扫脸版本，包括微信刷脸和支付宝刷脸
    public static boolean isQzFacePayRom() {
        return isQzFaceWeChatPayRom() || isQzFaceAlipayRom();
    }

    //创品扫脸版本，包括微信刷脸和支付宝刷脸
    public static boolean isCpFacePayRom() {
        return isCpFaceWeChatPayRom() || isCpFaceAlipayRom();
    }

    //创品43寸刷脸设备，包括微信刷脸和支付宝刷脸
    public static boolean isCpFiftyRom() {
        return isCpWechatFiftyPayRom() || isCpAliFiftyPayRom();
    }

    // 创品微信扫脸版本
    public static boolean isCpFaceWeChatPayRom() {
        return isCpWechatFiftyPayRom() || isCpWechatThirtyPayRom();
    }

    //创品支付宝刷脸，包括43寸和15寸
    public static boolean isCpFaceAlipayRom() {
        return isCpAliFiftyPayRom() || isCpAliThirtyPayRom();
    }

    public static boolean isQzFaceWeChatPayRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_FACE_PAY);
    }

    //群智ali扫脸
    public static boolean isQzFaceAlipayRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_FACE_ALIPAY);
    }

    public static boolean isCpWechatThirtyPayRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_THIRTY_RK3288_CAM_CHUANGPIN) ||
                getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_THIRTY_RK3288_CAM_CP_YS) ||
                getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_THIRTY_RK3288_NO_CAM_CHUANGPIN);
    }

    public static boolean isCpYsWxPayRom(){
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_THIRTY_RK3288_CAM_CP_YS) ||
                getRomType().equalsIgnoreCase(ROMTYPE_LD_L_40_FIFTY_RK3288_CAM_CP_YS);
    }

    public static boolean isCpAliThirtyPayRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_FACE_ALIPAY_CHUANGPIN) ||
                getRomType().equalsIgnoreCase(ROMTYPE_LD_L_15_THIRTY_RK3288_CAM_CP_YS) ;
    }

    public static boolean isCpWechatFiftyPayRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_40_FIFTY_RK3288_CAM_CHUANGPIN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_40_FIFTY_RK3288_CAM_CP_YS)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_40_FIFTY_RK3288_NO_CAM_CHUANGPIN);
    }

    public static boolean isCpAliFiftyPayRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_FACE_ALIPAY_CHUANGPIN);
    }

    public static boolean isSAYModelTwentyFiveRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_SAY_MODEL_TWENTYFIVE_RK3288);
    }

    /// 是否50口设备
    public static boolean isSAYModelFiftyRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_SAY_MODEL_FIFTY)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_SAY_MODEL_FIFTY_RK3288)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_SAY_MODEL_FIFTY_LAIDIAN)
                || isCpFiftyRom();
    }

    public static boolean isMtkShiAnYunAdvertise() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_MTK_SHIANYUN_CHUANGPIN);
    }

    public static boolean isNasChannelMachineRom() {
        return isNasFiveColChannelMachine();
    }

    public static boolean isNasTwoColChannelMachine() {
        return isMiddleAdThirtyScreenRom();
    }

    public static boolean isNasFiveColChannelMachine() {
        return isSAYModelFiftyRom() || isShiAnYunRom() || isQzFacePayRom() || isSAYModelTwentyFiveRom() ;
    }

    /**
     * 判断时候是55寸大屏地图导航机器
     * return false /true
     **/
    public static boolean isXLargerMapNavigationScreenRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_55_HID)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_55_LAIDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_55_CAP_4K_SYS6)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_55_CAP_4K_TMP1920x1080_SYS6)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_55_CAP_SYS7_RK3399)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_55_CAP_4K_CHUANGPIN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_55_CAP_4K_NO_SLOT_CHUANGPIN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_55_CAP_4K_NO_SLOT)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_55_CAP_SYS7_2K_RK3399);
    }

    public static boolean isCpMapNaviRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_55_CAP_4K_CHUANGPIN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_55_CAP_4K_NO_SLOT_CHUANGPIN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_CAP_CHUANGPIN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_CAP_NO_SLOT_CHUANGPIN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_CAP_SLOT24_CHUANGPIN);
    }

    public static boolean isCpNoMainBoardPlaneRom(){
        return  getRomType().equalsIgnoreCase(ROMTYPE_LD_M_10_24_RK3399_CHUANGPIN);
    }

    public static boolean isNoSlotMapNavScreenRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_55_CAP_4K_NO_SLOT)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_55_CAP_4K_NO_SLOT_CHUANGPIN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_CAP_NO_SLOT_CHUANGPIN);
    }

    /**
     * 判断时候是27寸大屏地图导航机器
     * return false /true
     **/
    public static boolean isLargeMapNavigationScreenRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_CAP_27_HID)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_CAP_27_LAIDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_CAP_NO_SLOT_CHUANGPIN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_CAP_NO_SLOT)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_CAP_CHUANGPIN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_CAP_SYS7_RK3399)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_CAP_SLOT24_CHUANGPIN);

    }

    public static boolean isLargeMapNavigation24SlotScreenRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_CAP_SLOT24_CHUANGPIN);
    }

    // 群智 Rk3399
    public static boolean isQzRk3399Rom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_27_CAP_SYS7_RK3399)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_55_CAP_SYS7_RK3399);
    }

    /**
     * 判断是否为中型机ROM
     *
     * @return true / false
     */
    public static boolean isMiddleScreenRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_M_10)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_M_AM5300)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_M_10_HID)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_M_10_HID_HONGDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_M_10_HID_FOURFAITH)
                || getRomType().equalsIgnoreCase(ROMTYPE_XW_M_FOOTBALL_PRINTER);
    }

    public static boolean isMiddleHIDScreenRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_M_10_HID)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_M_10_HID_HONGDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_M_10_HID_FOURFAITH);
    }

    public static boolean isLargerAdvertiseThirtyScreenRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_THIRTY_HID)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_THIRTY_HID_RK3288)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_THIRTY_S905D_SYS6)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_THIRTY_HID_LAIDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_THIRTY_HID_SYS6_LAIDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_THIRTY_SHIANYUN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_MTK_SHIANYUN_CHUANGPIN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_SAY_MODEL_FIFTY)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_SAY_MODEL_FIFTY_RK3288)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_43_SAY_MODEL_FIFTY_LAIDIAN);
    }

    public static boolean isMiddleAdThirtyScreenRom() {
        return isCpWechatThirtyPayRom() || isCpAliThirtyPayRom();
    }

    public static boolean isLarger21ScreenRom() {
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_21_HID)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_21_HID_RK3288)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_21_HID_LAIDIAN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_21_S905D_SYS6);
    }

    public static boolean is4KMapNavigationScreenRom(){
        return getRomType().equalsIgnoreCase(ROMTYPE_LD_L_55_CAP_4K_SYS6)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_55_CAP_4K_CHUANGPIN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_55_CAP_4K_NO_SLOT_CHUANGPIN)
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_L_55_CAP_4K_NO_SLOT);
    }

    public static boolean haveSaleLineModule() {
        if (isLargerAdvertiseThirtyScreenRom() || isLarger21ScreenRom()) {
            return false;
        }

        if (getRomType().equalsIgnoreCase(ROMTYPE_XW_L_43_XIAOWEI)
                || getRomType().equalsIgnoreCase(ROMTYPE_XW_M_FOOTBALL_PRINTER)) {
            return false;
        }

        return isLargerAdvertiseScreenRom() || isLargeScreenRom() || isMiddleScreenRom()
                || getRomType().equalsIgnoreCase(ROMTYPE_LD_S_NONE);
    }

    public static boolean haveChannelDeviceModel() {
        if (isADLVDSVerRom() || isADHDMIHorRom() || isNoSlotMapNavScreenRom()) {
            return false;
        }

        return true;
    }

    public static boolean haveTimingDeviceModule() {
        if (isSmallNoneAM5400Rom() || isADHDMIHorRom() || isCpAdLVDSVerRom()|| isCpNoMainBoardPlaneRom()) {
            return false;
        }

        return true;
    }

    public static boolean haveRecyleModule() {
        if (isSmallNoneAM5400Rom() || isSmallNoneScreenRom() || isADLVDSVerRom()
                || isADHDMIHorRom() ||isFacePayRom() || isSAYModelFiftyRom()
                || isShiAnYunRom() || isSAYModelTwentyFiveRom() || isMiddleAdThirtyScreenRom()|| isCpNoMainBoardPlaneRom()) {
            return false;
        }

        return true;
    }

    public static boolean haveSmokeModule() {
        if (isSmallNoneScreenRom() || isSmallNoneAM5400Rom() || isMiddleLargeScreenRom()|| isCpNoMainBoardPlaneRom()) {
            return false;
        }

        return true;
    }

    public static boolean haveLockModule() {
        if (getRomType().equalsIgnoreCase(ROMTYPE_LD_M_10)) {
            return true;
        }

        return false;
    }

    public static boolean haveMainCtrlBoardModule() {
        if (isXiaoWeiPlatform()
                || isXiaoWeiFootballPrinterPlatform()
                || isADHDMIHorRom()
                || isCpAdLVDSVerRom()
                || isSmallNoneAM5400Rom()) {
            return false;
        }

        return true;
    }

    public static boolean isMainboardSupportPowerOff() {
        if (isSmallNoneScreenRom() || isSmallNoneAM5400Rom()) {
            return false;
        }

        return true;
    }


    public static boolean checkIsAndroidRom(String romVesrion) {
        int platformType = PLATFORM_TYPE_PUBLIC;

        String searchStr = ")_";
        int index = romVesrion.indexOf(searchStr);
        if (index >= 0) {
            String tail = romVesrion.substring(index + searchStr.length());
            String seperatorStr = "_";
            String[] subItems = tail.split(seperatorStr);
            if (subItems.length > 0) {
                String identifier = subItems[subItems.length - 1];
                if (identifier.equalsIgnoreCase("hongdian")) {
                    platformType = PLATFORM_TYPE_HONGDIAN;
                } else if (identifier.equalsIgnoreCase("fourfaith")) {
                    platformType = PLATFORM_TYPE_FOURFAITH;
                } else if (identifier.equalsIgnoreCase("laidian")) {
                    platformType = PLATFORM_TYPE_LAIDIAN;
                } else if (identifier.equalsIgnoreCase("chuangpin")) {
                    platformType = PLATFORM_TYPE_CHUANGPIN;
                } else {
                    platformType = PLATFORM_TYPE_ALAGROUP;
                }
            }
        } else {
            platformType = PLATFORM_TYPE_PUBLIC;
        }

        return platformType >= -1;
    }




}
