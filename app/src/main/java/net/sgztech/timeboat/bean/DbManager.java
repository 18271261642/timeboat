package net.sgztech.timeboat.bean;

import android.util.Log;

import com.google.gson.Gson;

import org.litepal.LitePal;

import java.util.List;

/**
 * Created by Admin
 * Date 2023/2/22
 * @author Admin
 */
public class DbManager {

    private static final String TAG = "DbManager";


    public static volatile DbManager dbManager = null;

    private final Gson gson = new Gson();


    public synchronized static DbManager getInstance(){
        synchronized (DbManager.class){
            if(dbManager == null){
                dbManager = new DbManager();
            }
        }
        return dbManager;
    }

    private  DbManager() {
    }


    /**
     * 初始化3个闹钟
     */
    public void initAlarm(){
        List<AlarmBean> list = getAllAlarm();
        if(list == null){
            for(int i = 0;i<6;i++){
                AlarmBean alarmBean = new AlarmBean();
                alarmBean.setAlarmIndex(i);
                alarmBean.setOpen(false);
                alarmBean.setHour(0);
                alarmBean.setMinute(0);
                alarmBean.setRepeat((byte) 0x00);
                alarmBean.save();
            }
        }
    }

    /**
     * 查询所有闹钟
     */
    public List<AlarmBean> getAllAlarm(){
        try {
            List<AlarmBean> list = LitePal.findAll(AlarmBean.class);

            return list == null || list.isEmpty() ? null : list;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 修改单个闹钟
     */
    public void updateAlarm(AlarmBean alarmBean){
        AlarmBean ab = new AlarmBean();
        ab.setAlarmIndex(alarmBean.getAlarmIndex());
        ab.setRepeat(alarmBean.getRepeat());
        ab.setOpen(alarmBean.isOpen());
        ab.setHour(alarmBean.getHour());
        ab.setMinute(alarmBean.getMinute());
      
        boolean isUpdate = ab.saveOrUpdate("alarmIndex = ?",alarmBean.getAlarmIndex()+"");
        Log.e(TAG,"--------修改闹钟="+isUpdate+" "+new Gson().toJson(ab));

    }


    /**
     * 根据类型查询数据
     * @param type 类型
     * @param mac Mac地址
     * @return 对象
     */
    public CommRemindBean getDataForType(int type,String mac){
        try {
            List<CommRemindBean> list = LitePal.where("type = ? and deviceMac = ?",String.valueOf(type),mac).find(CommRemindBean.class);
            return list == null || list.isEmpty() ? null : list.get(0);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    public void initCommRemind(int type,String mac){
        CommRemindBean sCR = getDataForType(type,mac);
        if(sCR == null){
           CommRemindBean commRemindBean = new CommRemindBean();
           commRemindBean.setType(type);
           commRemindBean.setDeviceMac(mac);
           commRemindBean.save();
        }
    }


    /**
     * 保存数据
     * @param type 类型
     * @param commRemindBean data
     */
    public void saveCommRemindForType(int type,CommRemindBean commRemindBean){
        try {
            CommRemindBean cb = new CommRemindBean();
            cb.setDeviceMac(commRemindBean.getDeviceMac());
            cb.setType(type);
            cb.setStartHour(commRemindBean.getStartHour());
            cb.setStartMinute(commRemindBean.getStartMinute());
            cb.setEndMinute(commRemindBean.getEndMinute());
            cb.setEndHour(commRemindBean.getEndHour());
            cb.setLevel(commRemindBean.getLevel());
            cb.setSwitchStatus(commRemindBean.getSwitchStatus());

            Log.e(TAG,"------保存="+new Gson().toJson(cb));

            boolean isSave = cb.saveOrUpdate("type = ? and deviceMac = ?",String.valueOf(type), commRemindBean.getDeviceMac());
            Log.e(TAG,"------保存设置="+isSave);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
