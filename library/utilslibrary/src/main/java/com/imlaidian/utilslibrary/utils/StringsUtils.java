package com.imlaidian.utilslibrary.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zbo on 16/6/7.
 */
public class StringsUtils {
    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    // private final static SimpleDateFormat dateFormater = new
    // SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // private final static SimpleDateFormat dateFormater2 = new
    // SimpleDateFormat("yyyy-MM-dd");

    private final static Pattern phone = Pattern.compile("^1[0-9]{10}$");

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };



    /**
     * 判断是不是一个合法的手机号码
     */
    public static boolean isPhone(CharSequence phoneNum) {
        if (isEmpty(phoneNum))
            return false;
        return phone.matcher(phoneNum).matches();
    }



    /**
     * 将字符串转位日期类型
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 将字符串转位日期类型
     */
    public static Date toDate(String sdate,String format) {
        try {
            SimpleDateFormat formatDate=new SimpleDateFormat(format);
            return formatDate.parse(sdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String formatTimeStamp(long timeStamp){
        return dateFormater.get().format(timeStamp);
    }


    public  static boolean isHttpUrl(String url){

     boolean isSuccess =false ;
        if(url !=null && !url.equals("")&&url.length() >0){
            if(url.startsWith("http://") ||url.startsWith("https://")){
                isSuccess =true ;
            }
        }
     return  isSuccess ;


    }

    public static boolean isResourceIdImage(String url){

        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(url).matches();


    }


    /**
     * 以友好的方式显示时间
     *
     * @param sdate
     * @return
     */
    public static String friendly_time(String sdate) {
        Date time = toDate(sdate);
        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        // 判断是否是同一天
        String curDate = dateFormater2.get().format(cal.getTime());
        String paramDate = dateFormater2.get().format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
            return ftime;
        }
        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天";
        } else if (days > 2 && days <= 10) {
            ftime = days + "天前";
        } else if (days > 10) {
            ftime = dateFormater2.get().format(time);
        }

        return ftime;
    }

    public static final class distanceHelp {
        public static final String transformMeterToKiloMeter(double distance) {
            distance = distance / 1000;
            DecimalFormat df2 = new DecimalFormat("0.00");
            String mKiloMeter = df2.format(distance);
            mKiloMeter = mKiloMeter + "KM以内";

            return mKiloMeter;
        }
    }



    /**
     * 将字符串转位日期类型
     */
    public static Date toYmdDDate(String sdate) {
        try {
            return dateFormater2.get().parse(sdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 判断给定字符串时间是否为今日
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toYmdDDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }

        return b;
    }

    public static boolean isSameDay(String sdate ,long timeStamp){

        boolean b = false;
        Date time = toYmdDDate(sdate);
        Date today = new Date(timeStamp);
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }

        return b;

    }


    //String换成时间戳提交
    public static long ymdToTimestamp(String dateString) throws ParseException {
        long timeStamp = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d;
        try{
            d = sdf.parse(dateString);
            timeStamp= d.getTime();
        } catch(ParseException e){
            e.printStackTrace();
        }
        return timeStamp;
    }




    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input) ) {
            return true;
        }

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }

        return true;
    }



    /**
     * 判断给定字符串是否空白串 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     */
    public static boolean isEmpty(CharSequence input) {
        if (input == null || "".equals(input) )
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmptyOrNull(CharSequence input){
        if (input == null || "".equals(input)  || "null".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0) {
            return false;
        }

        return emailer.matcher(email).matches();
    }


    /**
     * 是否email
     * @param strEmail email地址
     * @return true / false
     */
    public static boolean isEmailString(String strEmail) {
        String strPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        return m.matches();
    }

    /**
     * 字符串转整数
     */
    public static int toInt(String str, int defValue) {
        if (null != str && str.length() > 0) {
            try {
                return Integer.parseInt(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return defValue;
    }

    /**
     * 对象转整数
     */
    public static int toInt(Object obj) {
        if (obj == null) {
            return 0;
        }

        return toInt(obj.toString(), 0);
    }

    public static int toInteger(String obj, int radix) {
        if (null != obj && obj.length() > 0) {
            try {
                return Integer.parseInt(obj, radix);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 0;
    }


    public static float toFloat(String obj) {
        if (null == obj || obj.length() <= 0) {
            return 0.00f;
        }

        return Float.parseFloat(obj);
    }

    public static String trimCRLF(String string) {
        if (null != string) {
            int start = 0, last = string.length() - 1;
            int end = last;
            while ((start <= end) && (string.charAt(start) == 0x0a || string.charAt(start) == 0x0d)) {
                start++;
            }
            while ((end >= start) && (string.charAt(end) == 0x0a || string.charAt(start) == 0x0d)) {
                end--;
            }

            if (start == 0 && end == last) {
                return string;
            }
            return string.substring(start, end);
        }

        return null;
    }

    /**
     * 对象转整数
     */
    public static long toLong(String obj) {
        if (null != obj && obj.length() > 0) {
            try {
                return Long.parseLong(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public static long toLong(String obj, int radix) {
        if (null != obj && obj.length() > 0) {
            try {
                return Long.parseLong(obj, radix);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    /**
     * 字符串转布尔值
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public static String inputStream2String(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;

        while ((i = is.read()) != -1) {
            baos.write(i);
        }

        return baos.toString();
    }


    /**
     * 格式化小数数据,会四舍五入
     *
     * @param data       要格式化的小数
     * @param decimalNum 小数点的个数
     * @return string
     */
    public static String getFormatDecimal(double data, int decimalNum) {
        String format = "";

        try {
            StringBuilder fs = new StringBuilder();
            fs.append("0.");
            if (decimalNum < 1) {
                decimalNum = 3;
            }
            for (int i = 0; i < decimalNum; i++) {
                fs.append("0");
            }
            DecimalFormat df = new DecimalFormat(fs.toString());
            format = df.format(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return format;
    }

    public static String getFormatDecimal(String value, int decimalNum) {
        if (null != value) {
            if (decimalNum < 1) {
                decimalNum = 3;
            }

            StringBuilder suffixBuilder = new StringBuilder();
            for (int i=0; i<decimalNum; i++) {
                suffixBuilder.append("0");
            }

            int remain = 0;
            int pointIndex = value.indexOf('.');
            if (pointIndex >= 0) {
                pointIndex++;

                remain = value.length() - pointIndex;
                if (remain >= decimalNum) {
                    return value.substring(0, pointIndex + decimalNum);
                }

                return value + suffixBuilder.substring(0, decimalNum - remain);
            } else {
                return value + "." + suffixBuilder.toString();
            }
        }

        return "";
    }

    /**
     * 将距离格式化，返回带单位的字符串
     * decimalNum 小数点个数，默认3个小数点
     */
    public static String getFormatDistance(int mile, int decimalNum, String unitM, String unitKm) {
        String format = "";
        if (mile <= 1000) {
            format = mile + unitM;
        } else {
            float f = mile / 1000f;
            format = getFormatDecimal(f, decimalNum) + unitKm;
        }
        return format;
    }


    /**
     * 将距离格式化，返回带单位的字符串
     * decimalNum 小数点个数，默认3个小数点
     */
    public static String getFormatDistance(int mile, int decimalNum) {
        String format = "";

        float f = mile / 1000f;
        format = getFormatDecimal(f, decimalNum);

        return format;
    }

    /**
     * 小数四舍五入取整
     */
    public static int decimalRoundToInt(double data) {
        return new BigDecimal(data).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
    }

    /*
     * 判断字符串中是否仅包含字母数字和汉字 各种字符的unicode编码的范围：
     * 汉字：[0x4e00,0x9fa5]（或十进制[19968,40869]） 数字：[0x30,0x39]（或十进制[48, 57]）
     * 小写字母：[0x61,0x7a]（或十进制[97, 122]） 大写字母：[0x41,0x5a]（或十进制[65, 90]）
     */
    public static boolean isUserName(String str) {
        // String regex = "^[a-z0-9A-Z]+$";
        String strPattern = "^[a-zA-Z][a-zA-Z0-9_]+$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(str);
        return m.matches();
    }


    // 验证只允许输入中英文数字下划线，regEx表示不合法的字符
    public static boolean hasSpecialCharacter(String str) {
        String regEx = "[`~!#$%^&*()+=|{}':;',\\[\\]<>/?~！#￥%……&*（）——+|{}【】‘；：”“’，、？ ]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);

        return m.find();
    }

    /**
     * 生成重复的字符串
     * @param repeatValue 需要重复的字符串
     * @param length 长度
     * @return 重复的字符串
     */
    public static String generateRepeatString(String repeatValue, int length) {
        String tempString = "";
        for (int i=0; i<length; i++) {
            tempString += repeatValue;
        }

        return tempString;
    }

    /**
     * 字符串左边追加字符串
     * @param string 原字符串
     * @param length 字符串的长度
     * @param paddingValue 追加的字符串
     * @return 追加后的字符串
     */
    public static String paddingLeft(String string, int length, String paddingValue) {
        if (string.length() >= length) {
            return string;
        }

        return generateRepeatString(paddingValue, length - string.length()) + string;
    }

    /**
     * 字符串右边追加字符串
     * @param string 原字符串
     * @param length 字符串的长度
     * @param paddingValue 追加的字符串
     * @return 追加后的字符串
     */
    public static String paddingRight(String string, int length, String paddingValue) {
        if (string.length() >= length) {
            return string;
        }

        return string + generateRepeatString(paddingValue, length - string.length());
    }

    /**
     * 生成MD5
     * @param s 字符串
     * @return md5
     */
    public static String getMD5(String s) {
        if (null == s) {
            return null;
        }

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(s.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuilder strBuf = new StringBuilder();
            for (int i = 0; i < encryption.length; i++) {
                String temp = Integer.toHexString(0xff & encryption[i]);
                if (temp.length() == 1) {
                    strBuf.append("0");
                }

                strBuf.append(temp);
            }

            return strBuf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 根据年月获得 这个月总共有几天
     * @param year
     * @param month
     * @return
     */
    public static int getDay(int year, int month) {
        int day = 30;
        boolean flag = false;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }

    /// 获取路径的前面部分
    public static String getPathComponents(String path) {
        if (null != path && path.length() > 0) {
            int index = path.lastIndexOf(File.separatorChar);

            if (index >= 0) {
                return path.substring(0, index);
            }

            return path;
        }

        return "";
    }

    /// 获取路径最后的部分
    public static String getLastPathComponent(String path) {
        if (null != path && path.length() > 0) {
            int index = path.lastIndexOf(File.separatorChar);

            if (index >= 0) {
                return path.substring(index + 1);
            }
        }

        return "";
    }

    /// 返回路径目录
    public static String stringByDeletingLastPathComponent(String path) {
        if (null != path && path.length() > 0) {
            int index = path.lastIndexOf(File.separatorChar);

            if (index > 0) {
                return path.substring(0, index);
            }
        }

        return "";
    }

    /// 拼接文件路径
    public static String stringByAppendingPathComponent(String path, String component) {
        if (null != path && null != component) {
            if (path.endsWith(File.separator)) {
                return path + component;
            }

            return path + File.separator + component;
        }

        return "";
    }

    /// 获取路径扩展名
    public static String getPathExtension(String path) {
        if (null != path && path.length() > 0) {
            int index = path.lastIndexOf(File.separatorChar);
            if (index >= 0 && (index + 1) < path.length()) {
                return path.substring(index + 1);
            }
        }

        return "";
    }

    /// 保留两位小数
    public static String getTwoDecimal(float value) {
        DecimalFormat fnum = new DecimalFormat("##0.00");

        return fnum.format(value);
    }


    public static String getRequestId() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String requestId = uuid;
        String strRand = "";
        try {
            for (int i = 0; i < 32 - requestId.length(); i++) {
                strRand += String.valueOf((int) (Math.random() * 10));
            }
        } catch (Exception e) {
            LogUtil.d("StringsUtils", "getRequestId e =" + e);
        }

        return requestId + strRand;
    }

    public static boolean isEnglish(String charaString) {
        return charaString.matches("^[a-zA-Z]*");
    }

    public static boolean isChinese(String str) {
        String regEx = "[\\u4e00-\\u9fa5]+";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        if(m.find()) {
            return true;
        }

        return false;
    }

    public static boolean equals(String cs1, String cs2) {
        if (cs1 == cs2) {
            return true;
        } else if (cs1 != null && cs2 != null) {
            if (cs1.length() != cs2.length()) {
                return false;
            } else {
                if (cs1.equals(cs2)) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    public static boolean equalsIgnoreCase(String cs1, String cs2) {
        if (cs1 == cs2) {
            return true;
        } else if (cs1 != null && cs2 != null) {
            if (cs1.length() != cs2.length()) {
                return false;
            } else {
                if (cs1.equalsIgnoreCase(cs2)) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    public static <T extends CharSequence> T defaultIfEmpty(T str, T defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }
}

