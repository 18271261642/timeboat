package com.imlaidian.utilslibrary.utilsManager;

import android.content.Context;
import android.util.DisplayMetrics;


import com.imlaidian.utilslibrary.UtilsApplication;

import static com.imlaidian.utilslibrary.utils.MainBoardVersionTools.isADHDMIHorRom;
import static com.imlaidian.utilslibrary.utils.MainBoardVersionTools.isADLVDSVerRom;
import static com.imlaidian.utilslibrary.utils.MainBoardVersionTools.isCpNoMainBoardPlaneRom;
import static com.imlaidian.utilslibrary.utils.MainBoardVersionTools.isFacePayRom;
import static com.imlaidian.utilslibrary.utils.MainBoardVersionTools.isLargeMapNavigationScreenRom;
import static com.imlaidian.utilslibrary.utils.MainBoardVersionTools.isLargeScreenRom;
import static com.imlaidian.utilslibrary.utils.MainBoardVersionTools.isLargerAdvertiseScreenRom;
import static com.imlaidian.utilslibrary.utils.MainBoardVersionTools.isMiddleLargeScreenRom;
import static com.imlaidian.utilslibrary.utils.MainBoardVersionTools.isMiddleScreenRom;
import static com.imlaidian.utilslibrary.utils.MainBoardVersionTools.isShiAnYunRom;
import static com.imlaidian.utilslibrary.utils.MainBoardVersionTools.isSmallNoneScreenRom;
import static com.imlaidian.utilslibrary.utils.MainBoardVersionTools.isXLargerMapNavigationScreenRom;
import static com.imlaidian.utilslibrary.utilsManager.AppUiInfoManager.MachineType.ADVERTISE_FACE_PAY;
import static com.imlaidian.utilslibrary.utilsManager.AppUiInfoManager.MachineType.ADVERTISE_PUBLISH_HOR_MACHINE;
import static com.imlaidian.utilslibrary.utilsManager.AppUiInfoManager.MachineType.ADVERTISE_PUBLISH_VER_MACHINE;
import static com.imlaidian.utilslibrary.utilsManager.AppUiInfoManager.MachineType.LARGER_ADVERTISE_MACHINE;
import static com.imlaidian.utilslibrary.utilsManager.AppUiInfoManager.MachineType.LARGE_MACHINE;
import static com.imlaidian.utilslibrary.utilsManager.AppUiInfoManager.MachineType.MAP_NAVIGATION_LARGE_MACHINE;
import static com.imlaidian.utilslibrary.utilsManager.AppUiInfoManager.MachineType.MAP_NAVIGATION_X_LARGER_MACHINE;
import static com.imlaidian.utilslibrary.utilsManager.AppUiInfoManager.MachineType.MIDDLE_LARGE_SCREEN_MACHINE;
import static com.imlaidian.utilslibrary.utilsManager.AppUiInfoManager.MachineType.MIDDLE_MACHINE;
import static com.imlaidian.utilslibrary.utilsManager.AppUiInfoManager.MachineType.NOSCREEN_MACHINE;
import static com.imlaidian.utilslibrary.utilsManager.AppUiInfoManager.MachineType.SAY_LARGER_ADVERTISE_MACHINE;

/**
 * Created by zbo on 16/9/8.
 */

public class AppUiInfoManager {
    public static AppUiInfoManager instance = null;
    private static ScreenInfo screenInfo  =null ;

    public enum MachineType {
        /// 27寸机器
        LARGE_MACHINE(1),
        /// 43寸机器
        LARGER_ADVERTISE_MACHINE(2) ,
        /// 15.4寸机器
        MIDDLE_LARGE_SCREEN_MACHINE(3),
        /// 10.1寸机器
        MIDDLE_MACHINE(4),
        /// 无屏幕机器
        NOSCREEN_MACHINE(5),
        /// 小机器 (立式)
        SMALL_MACHINE(6),
        // 55寸导航机
        MAP_NAVIGATION_X_LARGER_MACHINE(7) ,
        // 27寸导航机
        MAP_NAVIGATION_LARGE_MACHINE(8),
        // 食安云
        SAY_LARGER_ADVERTISE_MACHINE(9) ,
        /// 广告竖屏
        ADVERTISE_PUBLISH_VER_MACHINE(10),
        /// 广告横屏
        ADVERTISE_PUBLISH_HOR_MACHINE(11),
        /// 人脸支付
        ADVERTISE_FACE_PAY(12);

        private int value;

        private MachineType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }


        public static MachineType setValue(int value){
            switch (value){
                case 1:
                    return LARGE_MACHINE;
                case 2:
                    return LARGER_ADVERTISE_MACHINE;
                case 3:
                    return MIDDLE_LARGE_SCREEN_MACHINE;
                case 4:
                    return MIDDLE_MACHINE;
                case 5 :
                    return NOSCREEN_MACHINE;
                case 6 :
                    return SMALL_MACHINE;
                case  7 :
                    return  MAP_NAVIGATION_X_LARGER_MACHINE;
                case  8 :
                    return  MAP_NAVIGATION_LARGE_MACHINE;
                case  9:
                    return  SAY_LARGER_ADVERTISE_MACHINE ;
                case 10:
                    return  ADVERTISE_PUBLISH_VER_MACHINE ;
                case 11:
                    return  ADVERTISE_PUBLISH_HOR_MACHINE ;
                case 12:
                    return  ADVERTISE_FACE_PAY ;
            }

            return null ;
        }
    }

    public static AppUiInfoManager getInstance() {
        if(instance == null) {
            synchronized (AppUiInfoManager.class) {
                if(instance ==null) {
                    instance = new AppUiInfoManager();
                }
            }
        }

        return instance;
    }


    public AppUiInfoManager() {

    }

    public static AppUiInfoManager.ScreenInfo getScreenInfo() {
        if(screenInfo == null) {
            Context context = UtilsApplication.getInstance().getApplicationContext();
            screenInfo = AppUiInfoManager.getInstance().getScreenInfo(context) ;
        }
        return screenInfo;
    }

    /// 27寸设备
    public boolean isLarge27InchMachine() {
        return getScreenInfo().getMachineType() == MachineType.LARGE_MACHINE;
    }

    /// 43寸设备
    public boolean isLarge43InchMachine() {
        return getScreenInfo().getMachineType() == MachineType.LARGER_ADVERTISE_MACHINE;
    }

    /// 15.4寸设备
    public boolean isMiddle15InchMachine() {
        return getScreenInfo().getMachineType() == MachineType.MIDDLE_LARGE_SCREEN_MACHINE;
    }

    /// 10寸设备
    public boolean isMiddle10InchMachine() {
        return getScreenInfo().getMachineType() == MachineType.MIDDLE_MACHINE;
    }

    public boolean isXLargerMapNavigation(){
        return getScreenInfo().getMachineType() == MachineType.MAP_NAVIGATION_X_LARGER_MACHINE;
    }

    public boolean isLargeMapNavigation(){
        return getScreenInfo().getMachineType() == MachineType.MAP_NAVIGATION_LARGE_MACHINE;
    }

    public boolean isSAYLargeAdMachine(){
        return getScreenInfo().getMachineType() == MachineType.SAY_LARGER_ADVERTISE_MACHINE;
    }
    public boolean isAdVerPublishMachine(){
        return getScreenInfo().getMachineType() == ADVERTISE_PUBLISH_VER_MACHINE;
    }

    public boolean isAdHorPublishMachine(){
        return getScreenInfo().getMachineType() == ADVERTISE_PUBLISH_HOR_MACHINE;
    }

    public boolean isFacePayMachine(){
        return getScreenInfo().getMachineType() == ADVERTISE_FACE_PAY;
    }


    public class ScreenInfo {
        int screenPixelWidth;
        int screenPixelHeight;
        float screenDensity;
        int screenDpi;
        int screenDPWidth ;
        int screenDpHeight ;
        MachineType machineType;


        public ScreenInfo(int width, int height, int dpWidth ,int dpHeight , float density, int dpi) {
            super();

            screenPixelWidth = width;
            screenPixelHeight = height;
            screenDPWidth = dpWidth ;
            screenDpHeight =  dpHeight;
            screenDensity = density;
            screenDpi = dpi;

            // 中型机: 宽1024 高552 dpi 1.0  // 大型机 宽

            if (isLargeScreenRom()) {
                machineType = LARGE_MACHINE;
            } else if (isMiddleScreenRom()|| isCpNoMainBoardPlaneRom()) {
                machineType = MIDDLE_MACHINE;
            } else if (isSmallNoneScreenRom()) {
                machineType = NOSCREEN_MACHINE;
            } else if(isMiddleLargeScreenRom()) {
                machineType = MIDDLE_LARGE_SCREEN_MACHINE;
            } else if(isLargerAdvertiseScreenRom()) {
                if (isShiAnYunRom()) {
                    machineType = SAY_LARGER_ADVERTISE_MACHINE;
                }else if (isFacePayRom()) {
                    machineType = ADVERTISE_FACE_PAY;
                } else {
                    machineType = LARGER_ADVERTISE_MACHINE;
                }
            } else if (isXLargerMapNavigationScreenRom()) {
                machineType = MAP_NAVIGATION_X_LARGER_MACHINE;
            } else if (isLargeMapNavigationScreenRom()) {
                machineType =  MAP_NAVIGATION_LARGE_MACHINE;
            } else if (isADLVDSVerRom()) {
                machineType = ADVERTISE_PUBLISH_VER_MACHINE;
            } else if (isShiAnYunRom()) {
                machineType = SAY_LARGER_ADVERTISE_MACHINE;
            } else if (isADHDMIHorRom()) {
                machineType = ADVERTISE_PUBLISH_HOR_MACHINE;
            } else {
                machineType = MIDDLE_MACHINE;
            }
        }

        public MachineType getMachineType(){
             return machineType;
        }

        public  int getScreenPixelHeight() {
            return screenPixelHeight;
        }

        public  int getScreenPixelWidth() {
            return screenPixelWidth;
        }

        public  int getScreenDpi() {
            return screenDpi;
        }

        public  float getScreenDensity() {
            return screenDensity;
        }

        public  int getScreenDPWidth() {
            return screenDPWidth;
        }

        public  int getScreenDpHeight() {
            return screenDpHeight;
        }
    }


    //dpi = (宽的平方 + 高的平方)的2次平方根 /屏幕尺寸
    //px=dp*(dpi/160)
    public  ScreenInfo getScreenInfo(Context mContext) {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int screenPixelWidth = dm.widthPixels; // 宽
        int screenPixelHeight = dm.heightPixels +48; // 高 机器本身获取的dpi 有问题 需要增加 状态栏 48
        float density = dm.density; // 屏幕密度（0.75 / 1.0 (mdpi)/ 1.5(hdpi） /2.0(xhdpi) /3 (xxhdpi)
        int densityDpi = dm.densityDpi;
        int screenDPWidth  =  (int) (screenPixelWidth * density + 0.5f);
        int screenDPHeight =  (int) (screenPixelHeight * density + 0.5f);
        return new ScreenInfo(screenPixelWidth, screenPixelHeight, screenDPWidth ,screenDPHeight ,density, densityDpi);
    }
}


