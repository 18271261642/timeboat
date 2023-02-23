package net.sgztech.timeboat.bean;

import org.litepal.crud.LitePalSupport;

/**
 * Created by Admin
 * Date 2023/2/23
 * @author Admin
 */
public class CommRemindBean extends LitePalSupport {

    /**
     * 类型 0久坐，1喝水，3勿扰
     */
    private int type;

    /**
     * mac
     */
    private String deviceMac;

    //开关状态 0关，1开
    private int switchStatus;

    //开始小时
    private int startHour;

    //开始分钟
    private int startMinute;

    //结束小时
    private int endHour;

    //结束分钟
    private int endMinute;


    //间隔 分钟
    private int level;



    public String getHourAndMinute(){
        return String.format("%02d",startHour)+":"+String.format("%02d",startMinute);
    }

    public String getEndHourAndMinute(){
        return String.format("%02d",endHour)+":"+String.format("%02d",endMinute);
    }





    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSwitchStatus() {
        return switchStatus;
    }

    public void setSwitchStatus(int switchStatus) {
        this.switchStatus = switchStatus;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
