package com.imlaidian.utilslibrary.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;


import androidx.annotation.RequiresApi;

import com.imlaidian.utilslibrary.dataModel.CellInfo;

import java.util.ArrayList;
import java.util.List;

import static com.imlaidian.utilslibrary.utils.SystemTool.networkTypeName;


/**
 * Created by zbo on 17/6/23.
 */

public class NetWorkUtils {
    private static final  String TAG ="NetWorkUtils" ;
    /**
     * 初始化，获取基站信息
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static ArrayList<CellInfo> init(Context ctx) {
        ArrayList<CellInfo> cellInfos = new ArrayList<CellInfo>();

        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        //网络制式
        int type = tm.getNetworkType();
        /**
         * 获取SIM卡的IMSI码
         * SIM卡唯一标识：IMSI 国际移动用户识别码（IMSI：International Mobile Subscriber Identification Number）是区别移动用户的标志，
         * 储存在SIM卡中，可用于区别移动用户的有效信息。IMSI由MCC、MNC、MSIN组成，其中MCC为移动国家号码，由3位数字组成，
         * 唯一地识别移动客户所属的国家，我国为460；MNC为网络id，由2位数字组成，
         * 用于识别移动客户所归属的移动网络，中国移动为00，中国联通为01,中国电信为03；MSIN为移动客户识别码，采用等长11位数字构成。
         * 唯一地识别国内GSM移动通信网中移动客户。所以要区分是移动还是联通，只需取得SIM卡中的MNC字段即可
         */
        String imsi = tm.getSubscriberId();
        LogUtil.d(TAG, "imsi: " + imsi);
        //为了区分移动、联通还是电信，推荐使用imsi来判断(万不得己的情况下用getNetworkType()判断，比如imsi为空时)
        if (imsi != null && !"".equals(imsi)) {
            LogUtil.d(TAG, "imsi=" +imsi );
            if (imsi.startsWith("46000") || imsi.startsWith("46002")) {// 因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
                // 中国移动
                if (tm.getCellLocation() instanceof GsmCellLocation) {
                    mobile(cellInfos, tm);
                }
            } else if (imsi.startsWith("46001")) {
                // 中国联通
                if (tm.getCellLocation() instanceof GsmCellLocation) {
                    union(cellInfos, tm);
                }
            } else if (imsi.startsWith("46003")) {
                // 中国电信
                if (tm.getCellLocation() instanceof CdmaCellLocation) {
                    cdma(cellInfos, tm);
                }
            }
        } else {
            LogUtil.d(TAG, "type =" + type);
            // 在中国，联通的3G为UMTS或HSDPA，电信的3G为EVDO
            // 在中国，移动的2G是EGDE，联通的2G为GPRS，电信的2G为CDMA
            // String OperatorName = tm.getNetworkOperatorName();

            //中国电信
            if (type == TelephonyManager.NETWORK_TYPE_EVDO_A
                    || type == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || type == TelephonyManager.NETWORK_TYPE_CDMA
                    || type == TelephonyManager.NETWORK_TYPE_1xRTT) {
                cdma(cellInfos, tm);
            }
            //移动(EDGE（2.75G）是GPRS（2.5G）的升级版，速度比GPRS要快。目前移动基本在国内升级普及EDGE，联通则在大城市部署EDGE。)
            else if (type == TelephonyManager.NETWORK_TYPE_EDGE
                    || type == TelephonyManager.NETWORK_TYPE_GPRS) {
                mobile(cellInfos, tm);
            }
            //联通(EDGE（2.75G）是GPRS（2.5G）的升级版，速度比GPRS要快。目前移动基本在国内升级普及EDGE，联通则在大城市部署EDGE。)
            else if (type == TelephonyManager.NETWORK_TYPE_GPRS
                    || type == TelephonyManager.NETWORK_TYPE_EDGE
                    || type == TelephonyManager.NETWORK_TYPE_UMTS
                    || type == TelephonyManager.NETWORK_TYPE_HSDPA) {
                union(cellInfos, tm);
            }
        }

        return cellInfos;
    }

    /**
     * 电信
     *
     * @param cellInfos
     * @param tm
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void cdma(ArrayList<CellInfo> cellInfos, TelephonyManager tm) {
        CdmaCellLocation location = (CdmaCellLocation) tm.getCellLocation();
        CellInfo info = new CellInfo();
        info.setCellId(location.getBaseStationId());
        info.setLocationAreaCode(location.getNetworkId());
        info.setMobileNetworkCode(String.valueOf(location.getSystemId()));
        info.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
        info.setRadioType("cdma");
        cellInfos.add(info);

        //前面获取到的都是单个基站的信息，接下来再获取周围邻近基站信息以辅助通过基站定位的精准性
        // 获得邻近基站信息
        List<android.telephony.CellInfo> list = tm.getAllCellInfo();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            CellInfo cell = new CellInfo();
            cell.setCellId(location.getBaseStationId());
            cell.setLocationAreaCode(location.getNetworkId());
            cell.setMobileNetworkCode(String.valueOf(location.getSystemId()));
            cell.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
            cell.setRadioType("cdma");
            cellInfos.add(cell);
        }
    }


    /**
     * 移动
     *
     * @param cellInfos
     * @param tm
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void mobile(ArrayList<CellInfo> cellInfos,
                               TelephonyManager tm) {
        GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
        CellInfo info = new CellInfo();
        info.setCellId(location.getCid());
        info.setLocationAreaCode(location.getLac());
        info.setMobileNetworkCode(tm.getNetworkOperator().substring(3, 5));
        info.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
        info.setRadioType("gsm");
        cellInfos.add(info);

        //前面获取到的都是单个基站的信息，接下来再获取周围邻近基站信息以辅助通过基站定位的精准性
        // 获得邻近基站信息
        List<android.telephony.CellInfo> list = tm.getAllCellInfo();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            CellInfo cell = new CellInfo();
            cell.setCellId(location.getCid());
            cell.setLocationAreaCode(location.getLac());
            cell.setMobileNetworkCode(tm.getNetworkOperator().substring(3, 5));
            cell.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
            cell.setRadioType("gsm");
            cellInfos.add(cell);
        }
    }


    /**
     * 联通
     *
     * @param cellInfos
     * @param tm
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void union(ArrayList<CellInfo> cellInfos, TelephonyManager tm) {
        GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
        CellInfo info = new CellInfo();
        //经过测试，获取联通数据以下两行必须去掉，否则会出现错误，错误类型为JSON Parsing Error
        //info.setMobileNetworkCode(tm.getNetworkOperator().substring(3, 5));
        //info.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
        info.setCellId(location.getCid());
        info.setLocationAreaCode(location.getLac());
        info.setMobileNetworkCode("");
        info.setMobileCountryCode("");
        info.setRadioType("gsm");
        cellInfos.add(info);

        //前面获取到的都是单个基站的信息，接下来再获取周围邻近基站信息以辅助通过基站定位的精准性
        // 获得邻近基站信息
        List<android.telephony.CellInfo> list = tm.getAllCellInfo();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            CellInfo cell = new CellInfo();
            cell.setCellId(location.getCid());
            cell.setLocationAreaCode(location.getLac());
            cell.setMobileNetworkCode(tm.getNetworkOperator().substring(3, 5));
            cell.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
            cell.setRadioType("gsm");
            cellInfos.add(cell);
        }
    }

//    public static int getMobileName(Context context) {
//        int mobile = OTHER_OPERATOR_VALUE;
//        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        String operator = telManager.getSimOperator();
//        if (operator != null) {
//            if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
//                mobile = CHINA_MOBILE_VALUE;
//            } else if (operator.equals("46001")) {
//                mobile = CHINA_UNICOM_VALUE;
//            } else if (operator.equals("46003")) {
//                mobile = CHINA_TELECOM_VALUE;
//            }
//        }
//        return mobile;
//    }


    public static String getNetWorkType(Context context) {
        String networkType = networkTypeName(context);
        try {
            final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo mobNetInfoActivity = connectivityManager.getActiveNetworkInfo();

            if (mobNetInfoActivity == null
                    || !mobNetInfoActivity.isAvailable()) {
                // 注意一：
                // NetworkInfo 为空或者不可以用的时候正常情况应该是当前没有可用网络，
                // 但是有些电信机器，仍可以正常联网，
                // 所以当成net网络处理依然尝试连接网络。
                // （然后在socket中捕捉异常，进行二次判断与用户提示）。
                LogUtil.d(TAG, "sim card is networkType=" + networkType);

                return networkType;
            } else {
                // NetworkInfo不为null开始判断是网络类型
                int netType = mobNetInfoActivity.getType();

                LogUtil.d(TAG, "net link  is " + netType);
                if (netType == ConnectivityManager.TYPE_WIFI) {
                    // wifi net处理
                    LogUtil.d(TAG, "net link  is wifi");
                    networkType = "wifi";

                    return networkType;
                } else if (netType == ConnectivityManager.TYPE_ETHERNET) {
                    LogUtil.d(TAG, "net link  is ethernet");
                    networkType = "ethernet";

                    return networkType;
                } else if (netType == ConnectivityManager.TYPE_MOBILE) {
                    LogUtil.d(TAG, "net link  is networkType =" + networkType);
                    networkType = "mobile";
                    return networkType;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();

            return networkType;
        }

        return networkType;
    }

}
