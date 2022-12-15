package com.imlaidian.utilslibrary.config;

import com.imlaidian.utilslibrary.utils.LogUtil;

public class RemoteApiConstant {
    private static final String TAG = "RemoteApiConstant";


    // 中国
    public  static final int LaiDianPartner = 0 ;
    // 印尼
    public  static final int IndonesiaPartner = 1 ;
    //俄罗斯
    public  static final int RussiaPartner = 2  ;
    // 澳洲
    public static final  int AustraliaPartner = 3 ;

    // 印尼 server 服务端自己处理
    public  static final int IndonesiaNoServerPartner = 4 ;


    // 印度 服务端自己处理
    public  static final int IndiaServerPartner = 5 ;

    // 美国
    public  static final int AmericanServerPartner = 6 ;


    public  static  int cooperationPartner = LaiDianPartner ;

    public static void setCooperationPartner(int partner){
        LogUtil.d(TAG, "setCooperationPartner  partner=" +partner);
        cooperationPartner = partner;
    }

    public static boolean isRussiaPartnerAidl(){
        return cooperationPartner == RussiaPartner;
    }

    public static boolean isIndiaPartnerAidl() {
        return cooperationPartner == IndiaServerPartner;
    }

    public static boolean isIndonesaPartnerAidl() {

        return  cooperationPartner == IndonesiaPartner ;
    }

    public static boolean isIndonesaNoServerPartnerAidl() {
        return cooperationPartner ==  IndonesiaNoServerPartner ;
    }

    public static boolean isInonesaPartnerAidl() {
        return cooperationPartner == IndonesiaPartner ;
    }

    public static boolean isAustraliaPartnerAidl() {
        return cooperationPartner==  AustraliaPartner ;
    }

    public static boolean isAmericanPartnerAidl(){
        // 美国
        return cooperationPartner==  AmericanServerPartner ;

    }

    public static boolean isLdPartnerAidl() {
        return cooperationPartner ==  LaiDianPartner;
    }

    public static boolean isOverSeaAidl(){
        return cooperationPartner ==  IndonesiaPartner ||
                cooperationPartner ==  RussiaPartner ||
                cooperationPartner==  AustraliaPartner ||
                cooperationPartner == IndonesiaNoServerPartner ||
                cooperationPartner ==IndiaServerPartner ||
                cooperationPartner == AmericanServerPartner ;
    }



}
