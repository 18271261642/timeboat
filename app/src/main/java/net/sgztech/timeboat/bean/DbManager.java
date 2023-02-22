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
            for(int i = 0;i<3;i++){
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
        Log.e(TAG,"--------修改闹钟="+isUpdate);

    }
}
