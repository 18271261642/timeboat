package com.blala.blalable.timeboat;

import android.util.Log;

import com.blala.blalable.Utils;

import java.io.UnsupportedEncodingException;

/**
 * Created by Admin
 * Date 2023/1/11
 * @author Admin
 */
public class TimeBoatConstance {


    /**
     * 设置公英制和温度单位
     * @param isKM 是否是公制
     * @param isTemp 是否是英制
     * @return bytearray
     */
    public byte[] setUnitAndTemp(boolean isKM,boolean isTemp){
        return new byte[]{(byte) 0xAA, (byte) (isKM ? 0x00 : 0x01), (byte) (isTemp ? 0x00 : 0x01)};
    }


    public byte[] syncWeatherData(String cityName,int maxTemp,int minTemp,int currentTemp,int day,int weatherStatus){
        byte[] weatherArray = new byte[67];

        weatherArray[0] = (byte) 0xAA;
        weatherArray[1] = 0x33;
        //最低温
        weatherArray[2] = (byte) minTemp;
        //最高温
        weatherArray[3] = (byte) maxTemp;
        //当前
        weatherArray[4] = (byte) currentTemp;
        //日期
        weatherArray[5] = (byte) day;
        //天气索引
        weatherArray[6] = (byte) (weatherStatus & 0xff);
        weatherArray[7] = (byte) ((weatherStatus >> 8) & 0xff);

        byte[] cityArray = new byte[60];
        try {
            byte[] nameArray = cityName.getBytes("UTF-8");
            System.arraycopy(nameArray,0,cityArray,0,nameArray.length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.arraycopy(cityArray,0,weatherArray,8,cityArray.length-1);

        Log.e("TIMEBOAT","-------天气="+ Utils.formatBtArrayToString(cityArray)+"\n"+Utils.formatBtArrayToString(weatherArray));

        return weatherArray;

    }
}
