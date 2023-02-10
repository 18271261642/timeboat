package com.blala.blalable.timeboat;

/**
 * TimeBoat同步表盘二维码
 * Created by Admin
 * Date 2023/1/12
 * @author Admin
 */
public interface OnTimeBoatSyncQrcodeListener {
    
    void onSyncStatus(int countSchedule,int current) ;
}
