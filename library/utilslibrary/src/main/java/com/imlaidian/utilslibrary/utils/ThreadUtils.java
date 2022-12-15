package com.imlaidian.utilslibrary.utils;

/**
 * Author: Created by Dream on 16/8/10.
 * Email: caojengineer@126.com
 * Version: V1.0.0
 */
public class ThreadUtils {
    public static long eightHourPeriod = 8*60*60*1000;
    public static long oneDay = 24*60*60*1000;
    /**
     * 休眠
     * @param ms 毫秒
     */
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 休眠 秒
     * @param second 秒
     */
    public static void sleepSecond(double second) {
        sleep((long)(second * 1000));
    }

    /**
     * 休眠 分钟
     * @param minute 分钟
     */
    public static void sleepMinute(double minute) {
        sleepSecond(minute * 60);
    }

    public static void sleepHour(double hour) {
        sleepMinute(hour * 60);
    }
}
