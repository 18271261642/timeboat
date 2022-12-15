package com.imlaidian.utilslibrary.utils;

import com.imlaidian.utilslibrary.utils.LogUtil;
import com.imlaidian.utilslibrary.utils.StringsUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jun on 2017/8/5.
 * date util
 */

public class DateUtil {
    public static final long PER_DAY_OF_HOUR = 24;
    public static final long PER_HOUR_OF_MINUTE = 60;
    public static final long PER_MINUTE_OF_SECOND = 60;
    public static final long PER_SECOND_OF_MILLSECOND = 1000;

    /// 一分钟的毫秒数
    public static final long PER_MINUTE_OF_MILL_SECOND = PER_MINUTE_OF_SECOND * PER_SECOND_OF_MILLSECOND;
    /// 一小时的秒数
    public static final long PER_HOUR_OF_SECOND = PER_HOUR_OF_MINUTE * PER_MINUTE_OF_SECOND;

    /// 一小时的毫秒数
    public static final long PER_HOUR_OF_MILL_SECOND = PER_HOUR_OF_MINUTE * PER_MINUTE_OF_SECOND * PER_SECOND_OF_MILLSECOND;
    /// 一天的秒数
    public static final long PER_DAY_OF_SECOND = PER_DAY_OF_HOUR * PER_HOUR_OF_SECOND;
    /// 一天的毫秒数
    public static final long PER_DAY_OF_MILL_SECOND = PER_DAY_OF_SECOND * PER_SECOND_OF_MILLSECOND;

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd";

    /**
     * 时间有效检查，只判断最小值，不判断最大值
     * 原因：随时间推移，会到达上限的时间点，一旦出现上限时间点而设备没有升级，风险非常大。
     * @param date 日期时间
     * @return True / False
     */
    public static boolean isDateValid(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return (year >= 2017)
                && (month >= 1 && month <= 12)
                && (day >= 1 && day <= 31);
    }

    /**
     * 时间有效检查，只判断最小值，不判断最大值
     * 原因：随时间推移，会到达上限的时间点，一旦出现上限时间点而设备没有升级，风险非常大。
     * @param timestamp 时间戳
     * @return True / False
     */
    public static boolean isTimestampValid(long timestamp) {
        // 2017/01/01 00:00:00 -
        return timestamp >= 1483200000000L;
    }

    //小时 整点时间 例如 4：00
    public static boolean isOrderlyHourValid(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int minute = calendar.get(Calendar.MINUTE);

        if (minute <= 10) {
            return true;
        } else {
            return false;
        }
    }

    public static String getTimestampYYYYMMDD(long timestamp) {
        return getTimestampYYYYMMDD(timestamp, "-");
    }

    public static String getTimestampYYYYMMDD(long timestamp, String seperator) {
        Date date = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String monthString = (month < 10) ? ("0" + month) : ("" + month);
        String dayString = (day < 10) ? ("0" + day) : ("" + day);

        return year + seperator + monthString + seperator + dayString;
    }

    public static long getLastMonthTimeStamp(long timestamp){
        Date date = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
//        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int day = 1;

        if(month==1){
            year =year -1;
            month = 12;
        }else{
            month =month-1;
        }
        calendar.set(year ,month-1,day);

        return calendar.getTimeInMillis();
    }

    public static long getNextMonthTimeStamp(long timestamp){
        Date date = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = 1;

        if(month==12){
            year =year +1;
            month = 1;
        }else{
            month += 1;
        }

        calendar.set(year ,month-1,day);
        return calendar.getTimeInMillis();
    }

    public static long getLastYearTimeStamp(long timestamp){
        Date date = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) ;
        int day = 1;

        calendar.set(year-1 ,month ,day);
        return calendar.getTimeInMillis();
    }

    public static long getNextYearTimeStamp(long timestamp){
        Date date = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) ;
        int day = 1;

        calendar.set(year+1 ,month,day);
        return calendar.getTimeInMillis();
    }

    public static String getYear(long timestamp){
        Date date = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return "" + year +"年";
    }

    public static int  getWeek(long timestamp){
        Date date = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        return week ;
    }

    public static String  getWeekString(long timestamp){

        return  "第" +getWeek(timestamp) +"周" ;
    }


    public static String getTimestampYYYYMMDDHH(long timestamp, String seperator) {
        Date date = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String monthString = (month < 10) ? ("0" + month) : ("" + month);
        String dayString = (day < 10) ? ("0" + day) : ("" + day);
        String hourString = (hour < 10) ? ("0" + hour) : ("" + hour);

        return year + seperator + monthString + seperator + dayString + hourString;
    }

    public static String getTimestampYYYYMMDD_HHMMSS(long timestamp) {
        Date date = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String monthString = (month < 10) ? ("0" + month) : ("" + month);
        String dayString = (day < 10) ? ("0" + day) : ("" + day);
        String hourString = (hour < 10) ? ("0" + hour) : ("" + hour);
        String minuteString = (minute < 10) ? ("0" + minute) : ("" + minute);
        String secondString = (second < 10) ? ("0" + second) : ("" + second);

        return year + monthString + dayString + "_"
                + hourString + minuteString + secondString;
    }

    public static String getTimestampYYYYMMDDHHMMSS(long timestamp) {
        Date date = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String monthString = (month < 10) ? ("0" + month) : ("" + month);
        String dayString = (day < 10) ? ("0" + day) : ("" + day);
        String hourString = (hour < 10) ? ("0" + hour) : ("" + hour);
        String minuteString = (minute < 10) ? ("0" + minute) : ("" + minute);
        String secondString = (second < 10) ? ("0" + second) : ("" + second);

        return year + "-" + monthString + "-" + dayString + " "
                + hourString + ":" + minuteString + ":" + secondString;
    }


    public static String getTimestampHHMM(long timestamp) {
        Date date = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String hourString = (hour < 10) ? ("0" + hour) : ("" + hour);
        String minuteString = (minute < 10) ? ("0" + minute) : ("" + minute);

        return "" + hourString + ":" + minuteString ;
    }

    public static boolean isDateValid(long timestamp) {
        return isTimestampValid(timestamp);
    }

    public static boolean isSameDate(long timestamp1, long timestamp2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(new Date(timestamp1));

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date(timestamp2));

        return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
                && (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH))
                && (cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH));
    }

    public static boolean isSameDateHour(long timestamp1, long timestamp2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(new Date(timestamp1));

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date(timestamp2));

        return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
                && (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH))
                && (cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
                && (cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY)));
    }

    /// 获取前一天的23:59:59的timestamp
    public static long getPreDayEndTimestamp(long curTimestamp) {
        return getCurDayEndTimestamp(curTimestamp) - PER_DAY_OF_MILL_SECOND - 1000;
    }

    /// 获取明天的23:59:59的timestamp
    public static long getNextDayEndTimestamp(long curTimestamp) {
        return getCurDayEndTimestamp(curTimestamp) + PER_DAY_OF_MILL_SECOND - 1000;
    }

    /// 获取当天的00:00:00的timestamp
    public static long getCurDayBeginTimestamp(long curTimestamp) {
        Date date = new Date(curTimestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int ms = calendar.get(Calendar.MILLISECOND);

        return curTimestamp - ((hour * 60 * 60 + minute * 60 + second) * 1000 + ms);
    }

    /// 获取当前23:59:59的timestamp
    public static long getCurDayEndTimestamp(long curTimestamp) {
        Date date = new Date(curTimestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int ms = calendar.get(Calendar.MILLISECOND);

        long timeInterval = (hour * 60 * 60 + minute * 60 + second) * 1000 + ms;
        long curInterval = PER_DAY_OF_MILL_SECOND - timeInterval;

        return curTimestamp + curInterval;
    }

    /// 获取当前时间的前一小时整点 起始时间值，如果是 03:30:30 则为 02：00：00
    public static long getCurPreBeginHourTimestamp(long curTimestamp) {
        Date date = new Date(curTimestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int ms = calendar.get(Calendar.MILLISECOND);
        if (hour >= 1) {
            return curTimestamp - (((hour - 1) * 60 * 60 + minute * 60 + second) * 1000 + ms);
        } else {
            return curTimestamp - ((60 * 60 + minute * 60 + second) * 1000 + ms);
        }
    }

    /// 获取当前时间的前一小时整点 结束时间值，如果是 03:30:30 则为 02：59：59
    public static long getCurPreEndHourTimestamp(long curTimestamp) {
        return getCurHourStartTimestamp(curTimestamp) - 1000;
    }

    /// 获取当前时间的前一小时整点 起始时间值，如果是 03:30:30 则为 03：00：00
    public static long getCurHourStartTimestamp(long curTimestamp) {
        Date date = new Date(curTimestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int ms = calendar.get(Calendar.MILLISECOND);

        return curTimestamp - ((minute * 60 + second) * 1000 + ms);
    }

    /// 获取当前时间的前一小时整点 起始时间值，如果是 03:30:30 则为 03：59：59
    public static long getCurHourEndTimestamp(long curTimestamp) {
        Date date = new Date(curTimestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int ms = calendar.get(Calendar.MILLISECOND);

        return curTimestamp - ((minute * 60 + second) * 1000 + ms) + PER_HOUR_OF_MILL_SECOND - 1000;
    }

    /// 获取当前时间的前一小时整点 起始时间值，如果是 03:30:30 则为 04：00：00
    public static long getNextCurHourStartTimestamp(long curTimestamp) {
        Date date = new Date(curTimestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int ms = calendar.get(Calendar.MILLISECOND);

        return curTimestamp - ((minute * 60 + second) * 1000 + ms) + PER_HOUR_OF_MILL_SECOND;
    }

    /// 获取当前小时  最大 时间 如 03:30:30  为 04:59:59的timestamp
    public static long getNextHourEndTimestamp(long curTimestamp) {
        return getCurHourEndTimestamp(curTimestamp) + PER_HOUR_OF_MILL_SECOND;
    }

    /// 获取当天的00:x0:00的timestamp 小时  x <=24
    /// 如果是 04:30:30 , hour 为 5 则为 05：00：00
    public static long getCurDayNumHourTimeStatmp(long curTimestamp, int hourCount) {
        Date date = new Date(curTimestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int ms = calendar.get(Calendar.MILLISECOND);

        return curTimestamp - ((hour * 60 * 60 + minute * 60 + second) * 1000 + ms) + hourCount * PER_HOUR_OF_MILL_SECOND;
    }

    public static boolean isTimestampLess(long curTimestamp, int hour, int minute, int second) {
        Date date = new Date(curTimestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        int curMinute = calendar.get(Calendar.MINUTE);
        int curSecond = calendar.get(Calendar.SECOND);

        return (curHour < hour) || (curMinute < minute) || (curSecond < second);
    }

    public static String convertTimeInterval(long timeInterval) {
        if (timeInterval > 0) {
            double time = (double) timeInterval / 1000.0;
            LogUtil.d("DateUtil", "time=" + time);
            if (time > PER_DAY_OF_SECOND) {
                float days = (float) time / (float) PER_DAY_OF_SECOND;
                LogUtil.d("DateUtil", "days=" + days);
                return StringsUtils.getTwoDecimal(days) + "天";
            } else if (time > PER_HOUR_OF_SECOND) {
                float hours = (float) time / (float) PER_HOUR_OF_SECOND;

                return StringsUtils.getTwoDecimal(hours) + "小时";
            } else if (time > PER_MINUTE_OF_SECOND) {
                float minute = (float) time / (float) PER_MINUTE_OF_SECOND;

                return StringsUtils.getTwoDecimal(minute) + "分钟";
            } else {
                return StringsUtils.getTwoDecimal((float) time) + "秒";
            }
        }

        return null;
    }
    public static Date getDateFromString(String dateStr) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return date;
    }

    /**
     * 根据用户生日计算年龄
     */
    public static int getAgeByBirthday(Date birthday) {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthday)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(birthday);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }
        return age;
    }

    public static String getCurrentTime() {
        return getCurrentTime(DEFAULT_FORMAT);
    }

    /**
     * 获取当前时间
     *
     * @param format "yyyy-MM-dd HH:mm:ss"
     * @return 当前时间
     */
    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String getTimeStampYMD(long timeStamp){
        Date date = new Date(timeStamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_FORMAT, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String getTimeStampMDHMS(long timeStamp){
        Date date = new Date(timeStamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm:ss", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String getTimeStampYM(long timeStamp){
        Date date = new Date(timeStamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String getTimeStampFormat(String format ,long timeStamp){
        Date date = new Date(timeStamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String timeAddSubtract(String datetime, int dayAddNum) {
        return timeAddSubtract(datetime, dayAddNum, Calendar.DATE, DEFAULT_FORMAT);
    }



    /**
     * 时间加减
     *
     * @param datetime  如"2015-09-22","2015-09"
     * @param dayAddNum
     * @param field     如：Calendar.DATE
     * @param format    如"2015-09-22","2015-09"
     * @return
     */
    public static String timeAddSubtract(String datetime, int dayAddNum, int field, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.add(field, dayAddNum);
        date = cl.getTime();
        return sdf.format(date);
    }

    public static int compareDate(String date1, String date2) {
        return compareDate(date1, date2, DEFAULT_FORMAT);
    }

    /**
     * 时间比较大小
     *
     * @param date1  date1
     * @param date2  date2
     * @param format "yyyy-MM-dd HH:mm:ss"
     * @return 1:date1大于date2；
     * -1:date1小于date2
     */
    public static int compareDate(String date1, String date2, String format) {
        DateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }


    public static int getDayHourMinute(long timestamp) {
        Date date = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return hour*60+minute ;
    }

    public static String minuteToHour(int totalMinute){
        int hour = totalMinute / 60;
        int minute = totalMinute % 60;
        StringBuffer time = new StringBuffer();
        if (hour > 0) {
            String hourString = "" + hour + "小时" ;
            time.append(hourString);
        }
        if (minute > 0) {
            String minuteString = "" + minute + "分钟";
            time.append(minuteString);
        }
        return time.toString();
    }
}

