package com.blala.blalable;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.blala.blalable.bean.WeatherBean;
import com.blala.blalable.blebean.AlarmBean;
import com.blala.blalable.blebean.CommBleSetBean;
import com.blala.blalable.blebean.CommTimeBean;
import com.blala.blalable.listener.BleConnStatusListener;
import com.blala.blalable.listener.ConnStatusListener;
import com.blala.blalable.listener.OnBleStatusBackListener;
import com.blala.blalable.listener.OnCommBackDataListener;
import com.blala.blalable.listener.OnCommTimeSetListener;
import com.blala.blalable.listener.OnExerciseDataListener;
import com.blala.blalable.listener.OnMeasureDataListener;
import com.blala.blalable.listener.OnRealTimeDataListener;
import com.blala.blalable.listener.OnSendWriteDataListener;
import com.blala.blalable.listener.OnWatchFaceVerifyListener;
import com.blala.blalable.listener.OnWriteProgressListener;
import com.blala.blalable.listener.WriteBack24HourDataListener;
import com.blala.blalable.listener.WriteBackDataListener;
import com.blala.blalable.timeboat.CommandType;
import com.blala.blalable.timeboat.OnTimeBoatSyncQrcodeListener;
import com.blala.blalable.timeboat.TimeBoatConstance;
import com.google.gson.Gson;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.annotation.NonNull;


/**
 * Created by Admin
 * Date 2022/8/8
 */
public class BleOperateManager {

    private static final String TAG = "BleOperateManager";

    private static BleOperateManager bleOperateManager;

    private final BleManager bleManager = BleApplication.getInstance().getBleManager();

    private final BleConstant bleConstant = new BleConstant();
    public static BleOperateManager getInstance(){
        if(bleOperateManager == null){
            synchronized (BleOperateManager.class){
                if(bleOperateManager == null)
                    bleOperateManager = new BleOperateManager();
            }
        }
        return bleOperateManager;
    }

    public   BleOperateManager() {
    }



    //???????????????????????????
    private OnTimeBoatSyncQrcodeListener onTimeBoatSyncQrcodeListener;

    public static void setBleOperateManager(BleOperateManager bleOperateManager) {
        BleOperateManager.bleOperateManager = bleOperateManager;
    }

    //????????????????????????
    public void setOnOperateSendListener(OnSendWriteDataListener onOperateSendListener){
        bleManager.setOnSendWriteListener(onOperateSendListener);
    }


    //??????
    public void scanBleDevice(SearchResponse searchResponse, int duration, int times){
        bleManager.startScanBleDevice(searchResponse,duration,times);
    }

    //??????
    public void scanBleDevice(SearchResponse searchResponse, boolean isScanClass,int duration, int times){
        bleManager.startScanBleDevice(searchResponse,isScanClass,duration,times);
    }

    //????????????
    public void stopScanDevice(){
        bleManager.stopScan();
    }

    //????????????????????????????????????????????????
    public void setBleConnStatusListener(BleConnStatusListener bleConnStatusListener){
        bleManager.setBleConnStatusListener(bleConnStatusListener);
    }


    /**
     * ???????????????
     * @param isKm
     * @param isTemp
     */
    public void setDeviceUnitAndTemp(boolean isKm,boolean isTemp){
        bleManager.writeDataToDevice(new TimeBoatConstance().setUnitAndTemp(isKm,isTemp),writeBackDataListener);
    }




    //?????????????????????
    public void setRealTimeDataListener(OnRealTimeDataListener onRealTimeDataListener){
        bleManager.setOnRealTimeDataListener(onRealTimeDataListener);
    }

    //????????????????????????
    public void setMeasureDataListner(OnMeasureDataListener onMeasureDataListener){
        bleManager.setOnMeasureDataListener(onMeasureDataListener);
    }


    public void setClearListener(){
        bleManager.setClearListener();
    }

    public void setClearExercisListener(){
        bleManager.setClearExercise();
    }

    //????????????????????????????????????????????????????????????
    public void setBleBackStatus(OnBleStatusBackListener onBleStatusBackListener){
        bleManager.setOnBleBackListener(onBleStatusBackListener);
    }

    //??????
    public void connYakDevice(String bleName, String bleMac, ConnStatusListener connStatusListener){
        bleManager.connBleDeviceByMac(bleMac,bleName,connStatusListener);
    }

    //????????????
    public void disConnYakDevice(){
        bleManager.disConnDevice();
    }

    public void disConnNotRemoveMac(){
        bleManager.disConnDeviceNotRemoveMac();
    }

    //????????????????????????????????????
    public void writeCommonByte(byte[] bytes,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bytes,writeBackDataListener);
    }



    //??????????????????
    public void setIntoTestModel(WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.intoTestModel(),writeBackDataListener);
    }


    //????????????????????????????????????
    public void writeCommonByte(ArrayList<byte[]> listBytes,WriteBackDataListener writeBackDataListener){
        for(byte[] bt : listBytes)
          bleManager.writeDataToDevice(bt,writeBackDataListener);
    }


    //????????????
    public void findDevice(WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.findDevice(),writeBackDataListener);
    }
    //????????????
    public void findDevice(){
        bleManager.writeDataToDevice(bleConstant.findDevice(),writeBackDataListener);
    }

    //????????????
    public void readBattery(OnCommBackDataListener onCommBackDataListener){
        bleManager.readDeviceBatteryValue(onCommBackDataListener);
    }

    //?????????????????????????????? 1?????????2????????????
    public void setDevicePowerOrRecycler(int value,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.powerOff(value),writeBackDataListener);
    }

    //????????????
    public void setIntoTakePhotoStatus(WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.setIntoPhoto(),writeBackDataListener);
    }

    //??????????????????
    public void getDeviceVersionData(WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(new byte[]{(byte) 0xAA,0x57},writeBackDataListener);
    }

    //??????????????????
    public void getDeviceVersionData(OnCommBackDataListener onCommBackDataListener){
        bleManager.writeDataToDevice(bleConstant.getDeviceVersion(), new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                if((data[0]& 0xff)==15 && (data[1] & 0xff) == 255 && data[2] == 1){

                    //????????????24???????????????
                    int validDay = data[5] &0xff;


                    //????????????
                    int hardVersion = data[8] & 0xff;
                    //????????????
                    byte[] sortArray = new byte[8];
                    System.arraycopy(data,9,sortArray,0,sortArray.length);
                    try {
                        String versionStr = new String(sortArray,"UTF-8");

                        onCommBackDataListener.onStrDataBack(hardVersion+"",versionStr,String.valueOf(validDay));

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    //????????????
    public void getDeviceBattery(WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.getDeviceBattery(),writeBackDataListener);
    }

    //????????????
    public void getDeviceTime(WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.getCurrTime(),writeBackDataListener);
    }

    //????????????
    public void syncDeviceTime(WriteBackDataListener writeBackDataListener){
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH) + 1;
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int minute = calendar.get(Calendar.MINUTE);
//        int seconds = calendar.get(Calendar.SECOND);

        byte[] syncTimeArray = CommandType.buildSyncTimeCommand((byte) 0x01);

        Log.e(TAG,"---??????="+Utils.formatBtArrayToString(syncTimeArray ));

        bleManager.writeDataToDevice(syncTimeArray,writeBackDataListener);
    }


    //??????????????????
    public void setUserInfoData(int year,int month,int day,int weight,int height,int sex,int maxHeart,int minHeart,WriteBackDataListener writeBackDataListener){
       bleManager.writeDataToDevice(bleConstant.syncUserInfo(year,month,day,weight,height,sex,maxHeart,minHeart),writeBackDataListener);
    }

    //??????????????????
    public void getUserInfoData(WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.getUserInfo(),writeBackDataListener);
    }

    //??????????????????
    public void getLongSitData(WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.getLongSitData(),writeBackDataListener);
    }


    //??????????????????
    public void setStepTarget(int step,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.stepGoal(step),writeBackDataListener);
    }
    //??????????????????
    public void readStepTarget(OnCommBackDataListener onCommBackDataListener){
        bleManager.writeDataToDevice(bleConstant.readStepGoal(), new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) { //04 ff 0c 00 32 c8
                if(data[0]==0x04 && (data[1] & 0xff) == 0xff && (data[2] & 0xff) == 0x0C){
                    int step = Utils.getIntFromBytes((byte) 0x00,data[5],data[4],data[3]);
                    Log.e(TAG,"-----????????????="+step);
                    if(onCommBackDataListener != null)
                        onCommBackDataListener.onIntDataBack(new int[]{step});
                }
            }
        });
    }



    //??????????????????
    public void getLongSitData(OnCommTimeSetListener onCommTimeSetListener){
        bleManager.writeDataToDevice(bleConstant.getLongSitData(), new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                if(data.length<4)
                    return;
                if(data[0] ==6 && (data[1] & 0xff) == 0xff & data[2] == 3){
                    //??????
                    int level = data[3] & 0xff;
                    int startHour = data[4] & 0xff;
                    int startMinute = data[5] & 0xff;
                    int endHour = data[6] & 0xff;
                    int endMinute = data[7] & 0xff;

                    CommTimeBean commTimeBean = new CommTimeBean(0,startHour,startMinute,endHour,endMinute);
                    commTimeBean.setLevel(level);

                    boolean isOpen = level != 0;
                    commTimeBean.setSwitchStatus(isOpen?1: 0);
                    if(onCommTimeSetListener != null)
                        onCommTimeSetListener.onCommTimeData(commTimeBean);
                    bleManager.setClearListener();
                }
            }
        });
    }


    //??????????????????
    public void setLongSitData(int startHour,int startMinute,int interval ,int endHour,int endMinute,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.setLongSitData(startHour,startMinute,interval,endHour,endMinute),writeBackDataListener);
    }

    //??????????????????
    public void setLongSitData(CommTimeBean commTimeBean,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.setLongSitData(commTimeBean.getStartHour(),commTimeBean.getStartMinute(),commTimeBean.getLevel(),commTimeBean.getEndHour(),commTimeBean.getEndMinute()),writeBackDataListener);
    }


    //??????????????????
    public void getWristData(WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.getWristData(),writeBackDataListener);
    }
    //??????????????????
    public void getWristData(OnCommTimeSetListener onCommTimeSetListener){
        bleManager.writeDataToDevice(bleConstant.getWristData(), new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                if(data.length<4)
                    return;
                if(data[0] ==6 && (data[1] & 0xff) == 0xff & data[2] == 8){
                    //??????
                    int isOpen = data[3] & 0xff;
                    int startHour = data[4] & 0xff;
                    int startMinute = data[5] & 0xff;
                    int endHour = data[6] & 0xff;
                    int endMinute = data[7] & 0xff;

                    CommTimeBean commTimeBean = new CommTimeBean(isOpen,startHour,startMinute,endHour,endMinute);
                    if(onCommTimeSetListener != null)
                        onCommTimeSetListener.onCommTimeData(commTimeBean);
                    bleManager.setClearListener();
                }
            }
        });
    }



    //??????????????????
    public void setWristData(boolean isOpen,int startHour,int startMinute,int endHour,int endMinute,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.setWristData(isOpen,startHour,startMinute,endHour,endMinute),writeBackDataListener);
    }
    //??????????????????
    public void setWristData(CommTimeBean commTimeBean,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.setWristData(commTimeBean.getSwitchStatus() == 1,commTimeBean.getStartHour(),commTimeBean.getStartMinute(),commTimeBean.getEndHour(),commTimeBean.getEndMinute()),writeBackDataListener);
    }



    //????????????????????????
    public void getHeartStatus(WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.getHeartStatus(),writeBackDataListener);
    }

    //????????????????????????
    public void setHeartStatus(boolean isOpen,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.setHeartStatus(isOpen),writeBackDataListener);
    }

    //??????????????????
    public void  getDNTStatus(WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.getDNTStatus(),writeBackDataListener);
    }
    //??????????????????
    public void  getDNTStatus(OnCommTimeSetListener onCommTimeSetListener){
        bleManager.writeDataToDevice(bleConstant.getDNTStatus(), new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                if(data.length<4)
                    return;
                if(data[0] ==6 && (data[1] & 0xff) == 255 && data[2] == 6){
                    //??????
                    int isOpen = data[3] & 0xff;
                    int startHour = data[4] & 0xff;
                    int startMinute = data[5] & 0xff;
                    int endHour = data[6] & 0xff;
                    int endMinute = data[7] & 0xff;

                    CommTimeBean commTimeBean = new CommTimeBean(isOpen,startHour,startMinute,endHour,endMinute);
                    if(onCommTimeSetListener != null)
                        onCommTimeSetListener.onCommTimeData(commTimeBean);
                    bleManager.setClearListener();
                }
            }
        });
    }

    //??????????????????
    public void setDNTStatus(boolean isOpen,int startHour,int startMinute,int endHour,int endMinute,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.setDNTStatus(isOpen,startHour,startMinute,endHour,endMinute),writeBackDataListener);
    }
    //??????????????????
    public void setDNTStatus(CommTimeBean commTimeBean,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.setDNTStatus(commTimeBean.getSwitchStatus() == 1,commTimeBean.getStartHour(),commTimeBean.getStartMinute(),commTimeBean.getEndHour(),commTimeBean.getEndMinute()),writeBackDataListener);
    }


    //????????????
    public void intoTakePhoto(WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.intoPhoto(),writeBackDataListener);
    }

    //??????????????????
    public void measureSo2Status(boolean isOpen,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.measureSpo2(isOpen),writeBackDataListener);
    }

    //????????????
    public void measureHeartStatus(boolean isOpen,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.measureHeartStatus(isOpen),writeBackDataListener);
    }

    //????????????
    public void measureBloodStatus(boolean isOpen,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.measureBloodStatus(isOpen),writeBackDataListener);
    }

    //??????????????????
    public void getCommonSetting(WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.getCommonSetData(),writeBackDataListener);
    }


    //????????????
    public void setCommonSetting(boolean isKm,boolean is24Hour,boolean is24HourHeart,boolean isTmp,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.setCommonData(isKm,is24Hour,is24HourHeart,isTmp),writeBackDataListener);
    }

    private final StringBuilder setBuilder = new StringBuilder();
    //????????????????????????????????????
    public void setCommonSetting(CommBleSetBean commBleSetBean, WriteBackDataListener writeBackDataListener){
        setBuilder.delete(0,setBuilder.length());
        byte[] byArray = new byte[12];
        byArray[0] = 0x0a;
        byArray[1] = 0x32;
        //??????????????????
        //0?????????
        setBuilder.append(commBleSetBean.getMetric());
        setBuilder.append(commBleSetBean.getLanguage());
        setBuilder.append(commBleSetBean.getTimeType());
        setBuilder.append(0);
        setBuilder.append(commBleSetBean.getIs24Heart());
        setBuilder.append(commBleSetBean.getTemperature());
        setBuilder.append("00");
        String setStr = setBuilder.reverse().toString();
        Log.e(TAG,"--????????????="+setStr);
        byte bt = Utils.bitToByte(setStr);
        byArray[3] = bt;
        byArray[4] = 3;
        // Utils.bitToByte()
        bleManager.writeDataToDevice(byArray,writeBackDataListener);
    }


    //??????????????????
    public void getLocalDial(WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.getLocalDial(),writeBackDataListener);
    }

    //??????????????????
    public void setLocalDial(int index ,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.setLocalDial(index),writeBackDataListener);
    }

    //??????????????????
    public void getBackLight(WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.getBackLight(),writeBackDataListener);
    }

    //???????????????????????????
    public void setBackLight(int light,int interval,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.setBackLight(light,interval),writeBackDataListener);
    }


    //??????????????????
    public void getDayForData(int day,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.getDaySport(day),writeBackDataListener);
    }

    //??????????????????
    public void getDay24HourForData(int day,WriteBack24HourDataListener writeBack24HourDataListener){
        bleManager.write24HourDataToDevice(bleConstant.getDaySport(day),writeBack24HourDataListener);
    }

    /**???back??????**/
    public void setDay24HourClear(){
        bleManager.clearListener();
    }


    public void getCountDayData(int day,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.getCountData(day),writeBackDataListener);
    }


    /**??????????????????**/
    public void getExerciseData(int num,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.getExerciseByte(num),writeBackDataListener);
    }


    /**????????????????????????**/
    public void setExerciseDataListener(OnExerciseDataListener onExerciseDataListener){
        bleManager.setOnExerciseDataListener(onExerciseDataListener);
    }

    //??????????????????
    public void clearAllDeviceExerciseData(WriteBackDataListener writeBackDataListener){
        bleManager.writeExcDataToDevice(bleConstant.clearExerciseByte(),writeBackDataListener);
    }

    //????????????
    public void setAlarmId(AlarmBean alarmBean,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.setAlarm(alarmBean.getAlarmIndex(),alarmBean.isOpen(),alarmBean.getRepeat(),alarmBean.getHour(),alarmBean.getMinute()),writeBackDataListener);
    }

    //????????????
    public void readAlarm(int alarmId,WriteBackDataListener writeBackDataListener){
        bleManager.writeDataToDevice(bleConstant.readAlarm(alarmId), writeBackDataListener);
    }

    private List<AlarmBean> list = new ArrayList<>();

    public void readAllAlarm(){
        list.clear();
        int alarmIndex = 0;
        readAlarm(alarmIndex);
    }

    private void readAlarm(int id){
       readAlarm(id, new WriteBackDataListener() {
           @Override
           public void backWriteData(byte[] data) {
               if((data[0]&0xff) == 18 && (data[1] &0xff) == 255){
                   if(data[3] == 0x00){
                       AlarmBean alarmBean = analysisAlarm(data);
                       list.add(alarmBean);
                       readAlarm(0x01);
                   }

                   if(data[3] == 0x01){
                       AlarmBean alarmBean = analysisAlarm(data);
                       list.add(alarmBean);
                       readAlarm(0x02);
                   }
                   if(data[3] == 0x02){
                       AlarmBean alarmBean = analysisAlarm(data);
                       list.add(alarmBean);
                   }
               }
           }
       });
    }


    public AlarmBean analysisAlarm(byte[] array){
        AlarmBean alarmBean = new AlarmBean();
        alarmBean.setAlarmIndex(array[3] & 0xff);
        alarmBean.setOpen(array[4] == 0x01);
        alarmBean.setRepeat(array[5]);
        alarmBean.setHour(array[6] & 0xff);
        alarmBean.setMinute(array[7] &0xff);
        byte[] msgArray = new byte[12];
        System.arraycopy(array,8,msgArray,0,msgArray.length);
        alarmBean.setMsg(new String(msgArray));
        return alarmBean;

    }


    //??????????????????
    public void readCurrentDial(OnCommBackDataListener onCommBackDataListener){
        bleManager.writeDataToDevice(bleConstant.getLocalDial(), new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                if((data[0] & 0xff) == 2 && (data[1] & 0xff) == 0xff){
                    //??????
                    int id = data[3] & 0xff;
                    onCommBackDataListener.onIntDataBack(new int[]{id});
                }
            }
        });
    }

    //??????????????????
    public void setLocalDial(int id){
        bleManager.writeDataToDevice(bleConstant.setLocalDial(id),writeBackDataListener);
    }




    private ArrayList<byte[]> appList = new ArrayList<>();

    private final Handler msgHandler = new Handler(Looper.myLooper()){
        @NonNull
        @Override
        public String getMessageName(@NonNull Message message) {
            return super.getMessageName(message);


        }
    };



    /**
     * ??????app??????
     * @param type ??????
     * @param title ??????
     * @param content ??????
     * @param writeBackDataListener
     */
    public static String formatTwoStr(int number) {
        String strNumber = String.format("%02d", number);
        return strNumber;
    }

    private  static List<byte[]> setBytes(String content){

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String strMonth = formatTwoStr(calendar.get(Calendar.MONTH) + 1);
        String strDay = formatTwoStr(calendar.get(Calendar.DAY_OF_MONTH));
        String strHour = formatTwoStr(calendar.get(Calendar.HOUR_OF_DAY));
        String strMin = formatTwoStr(calendar.get(Calendar.MINUTE));
        String strSecond = formatTwoStr(calendar.get(Calendar.SECOND));
        //String time = "20200110T1036";
        String time = year + strMonth + strDay + "T" + strHour + strMin + strSecond;


        List<byte[]> allList = new ArrayList<>();
        byte[] contentLength = content.getBytes();

        byte[] b1Array = new byte[20];
        b1Array[0] = 0x01;
        b1Array[1] = 0x10;
        b1Array[2] = 0x02;
        b1Array[3] = 0x00;
        b1Array[4] = (byte) contentLength.length;
        b1Array[5] = 0x00;

        allList.add(b1Array);

        byte[] b2Array = new byte[20];
        b2Array[0] = 0x02;
        b2Array[1] = 0x10;
        b2Array[2] = 0x00;
        allList.add(b2Array);


        byte[] b3Array = new byte[20];
        System.arraycopy(contentLength,0,b3Array,1,contentLength.length+1);

        b3Array[0]= 0x03;
        allList.add(b3Array);
        byte[] timeArray = time.getBytes();


        byte[] timeA = new byte[20];
        System.arraycopy(timeArray,0,timeA,1,timeArray.length+1);

        allList.add(timeA);


        return allList;

    }


    int tempIndex = 0;
    public void sendAPPNoticeMessage(int type,String title,String content,WriteBackDataListener writeBackDataListener){
        tempIndex = 0;
        appList.clear();

        bleManager.clearRequest();

        /**
         * 01 10 04 00 35 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
         * 02 10 E6 88 91 E7 9A 84 E7 94 B5 E8 84 91 20 28 32 E6 9D A1
         * 04 10 36 36 36 36 36 00 00 00 00 00 00 00 00 00 00 00 00 00
         * 03 10 32 30 32 32 31 31 31 38 54 31 36 35 33 34 34 00 00 00
         */
        String st1 = "0110040035000000000000000000000000000000";
        String st2 = "0210E68891E79A84E794B5E88491202832E69DA1";
        String st3 = "0310323032323131313854313635333434000000";
        String st4 = "0410363636363600000000000000000000000000";
        byte[] b1 = Utils.hexStringToByte(st1);
        byte[] b2 = Utils.hexStringToByte(st2);
        byte[] b3 = Utils.hexStringToByte(st3);
        byte[] b4 = Utils.hexStringToByte(st4);


        List<byte[]> appByte = Utils.sendMessageData(type,title,content);
//        appList.addAll(appByte);
        appList.clear();
        appList.add(b1);
        appList.add(b2);
        appList.add(b3);
        appList.add(b4);

        Log.e(TAG,"------??????="+new Gson().toJson(appByte));

//        for(int i = 0 ;i<appByte.size();i++){
//            byte[] bt = appByte.get(i);
//          Log.e(TAG,"????????????="+Utils.formatBtArrayToString(bt));
//
//            bleManager.writeDataToDevice(bt);
//        }
        int size = appByte.size();


        bleManager.writeDataToDevice(appByte.get(0), new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                Log.e(TAG,"---------????????????="+Utils.formatBtArrayToString(data)+" "+tempIndex);
                if(tempIndex+1>=appByte.size()){
                    bleManager.setClearListener();
                    return;
                }
                tempIndex = tempIndex+1;

                byte[] v = appByte.get(tempIndex);
                bleManager.writeDataToDevice(v);
            }
        });
    }





    //????????????
    public void setMusicStatus(String musicName,String musicContTime,String musicCurrentTime,WriteBackDataListener writeBackDataListener){
        ArrayList<byte[]> musicLt = bleConstant.sendMusic(musicName,musicContTime,musicCurrentTime);

        for(int i = 0;i<musicLt.size();i++){
            byte[] bt = musicLt.get(i);
            bleManager.writeDataToDevice(bt,writeBackDataListener);
        }
    }


    //????????????
    public void sendWeatherData(String cityName,WriteBackDataListener writeBackDataListener){

        byte[] secondByte = new byte[20];
        secondByte[0] = 0x04;
        secondByte[1] = 12;
        secondByte[2] = (byte) 2;
        secondByte[3] = 0x20;
        secondByte[4] = (byte) (20 >> 8);
        secondByte[5] = 0x21;
        secondByte[6] = 0x30;
        secondByte[7] = 0x10;
        secondByte[8] = 0x25;

        byte[] wb = new byte[]{0x04,0x12 ,0x02, 0x13, 0x00 ,0x18, 0x17, 0x12, 0x01 };

        bleManager.writeDataToDevice(wb,writeBackDataListener);
//        ArrayList<byte[]> weatherLt = bleConstant.weatherListByte(cityName);
//        for(int i = 0;i<weatherLt.size();i++){
//            byte[] wtByte = weatherLt.get(i);
//            bleManager.writeDataToDevice(wtByte,writeBackDataListener);
//        }
    }


    /**
     * ????????????
     * @param weatherList ????????????
     */
    public void sendWeatherData(List<WeatherBean> weatherList,WriteBackDataListener writeBackDataListener){
        int index = 1;
        for(WeatherBean wb : weatherList){
            index = index+1;
            bleManager.writeDataToDevice(bleConstant.weatherList(index,wb.getAirQuality(),wb.getTemperature(),wb.getMaxTemper(),wb.getMinTemper(),wb.getWeather()),writeBackDataListener);
        }

    }

    public void sendIndexBack(int index,OnWatchFaceVerifyListener onWatchFaceVerifyListener){
        bleManager.writeWatchFaceData(bleConstant.sendCurrWatchFace(index), new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                if(data.length == 4){
                    if(data[0] ==2 && ((data[1] & 0xff) == 255) && (data[2] & 0xff) == 97)
                        if(onWatchFaceVerifyListener != null)
                            onWatchFaceVerifyListener.isVerify(true,index);
                }
            }
        });
    }

    public void sendWatchFaceIndex(int index,byte[] data,OnWatchFaceVerifyListener onWatchFaceVerifyListener){
        bleManager.writeWatchFaceData(data, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                if(data.length == 4){
                    if(data[0] ==2 && ((data[1] & 0xff) == 255) && (data[2] & 0xff) == 97){
                        if(onWatchFaceVerifyListener != null)
                            onWatchFaceVerifyListener.isVerify(true,index);
                    }
                }

            }
        });
    }


    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if(msg.what == 0x01){
                Log.e(TAG,"-----????????????index="+timeBoatSize +"  "+ timeDialList.size());
                if(timeBoatSize <timeDialList.size()-1){
                    byte[] currentDial = timeDialList.get(timeBoatSize);
                    timeBoatSize++;
                    sendToItemDial(currentDial);

                }else{
                    Log.e(TAG,"------???????????????");
                    if(onTimeBoatSyncQrcodeListener != null){
                        onTimeBoatSyncQrcodeListener.onSyncStatus(timeDialList.size(),timeBoatSize);
                    }
                }

            }
        }
    };



    public void sendSelectDial(List<RawFileBean> list, OnWriteProgressListener onWriteProgressListener){


        sendIndexBack(1, new OnWatchFaceVerifyListener() {
            @Override
            public void isVerify(boolean isSuccess, int position) {
                Log.e(TAG,"----?????????????????????="+isSuccess);
                if(isSuccess){
                    sendDialContent(list,1);
                }
            }
        });
    }

    int tmpIndex = 0;
    private List<byte[]> currListByte = new ArrayList<>();

    private void sendDialContent(List<RawFileBean> list,int index){
        tmpIndex = -1;
        currListByte.clear();
        byte[] faceByteArray = AppUtils.getArrayByFaceInd(list,index-1);

        currListByte = Utils.dialByteList(faceByteArray);
        Log.e(TAG,"-----??????????????????="+currListByte.size()+"  "+tmpIndex);
        handler.sendEmptyMessage(0x00);
    }


    private void writeData(byte[] b){
        bleManager.writeWatchFaceData(b, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                Log.e(TAG,"----??????????????????="+Arrays.toString(data));
                handler.sendEmptyMessageDelayed(0x00,100);
            }
        });
    }



    //????????????

    //??????????????????1,2,3,4,0
    int faceIndex = 1;
    private boolean isSync = false;
    public void sendCusWatchFace(List<RawFileBean> list,WriteBackDataListener writeBackDataListener){
        faceIndex = 1;
        isSync = false;
        Log.e(TAG,"-----??????????????????----faceIndex="+faceIndex+" isSync="+isSync);
//        do {

            sendIndexBack(faceIndex, new OnWatchFaceVerifyListener() {
                @Override
                public void isVerify(boolean isSuccess, int position) {
                    Log.e(TAG,"---?????????????????????="+faceIndex+" ?????????"+position+" isSuccess="+isSuccess);
                    if(isSuccess){  //?????????????????????????????????????????????????????????????????????
                        byte[] faceByteArray = AppUtils.getArrayByFaceInd(list,faceIndex-1);
                        sendIndexFace(faceIndex, faceByteArray, new OnWatchFaceVerifyListener() {
                            @Override
                            public void isVerify(boolean isSuccess, int position) {
                                Log.e(TAG,"----????????????????????????="+isSuccess + position);
                                if(isSuccess){
                                    isSync = true;
                                    if (faceIndex != 4) {
                                        faceIndex++;
                                    }else{
                                        faceIndex = 0;
                                    }

                                    if(position == 0)  //????????????????????????
                                        isSync = false;
                                }
                            }
                        });
                    }
                }
            });

//        }while (isSync);



        Log.e(TAG,"----?????????????????????-----");

    }



    int positionIndex = 0;
    ArrayList<byte[]> listBytes = new ArrayList<>();

    private void sendIndexFace(int facePackIndex,byte[] arrays,OnWatchFaceVerifyListener onWatchFaceVerifyListener){
        listBytes.clear();
        listBytes = Utils.dialByteList(arrays);

        Log.e(TAG,"--22--????????????????????????="+listBytes.size());

        //???????????????
        do {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendWatchFaceIndex(positionIndex, listBytes.get(positionIndex), new OnWatchFaceVerifyListener() {
                    @Override
                    public void isVerify(boolean isSuccess, int position) {
                        Log.e(TAG,"----???????????????="+isSuccess+" position="+position);
                        if(isSuccess){
                            Message message = handler.obtainMessage();
                            message.what = 0x00;
                            Bundle bundle = new Bundle();
                            bundle.putInt("index",positionIndex++);

                        }
                            positionIndex++;
                        Log.e(TAG,"---????????????="+isSuccess+" "+position);
                    }
                });
            }
        }, 200);

        }while ( positionIndex != listBytes.size()-1);

        facePackIndex++;
        Log.e(TAG,"--????????????????????????="+facePackIndex);
        if(onWatchFaceVerifyListener != null)
            onWatchFaceVerifyListener.isVerify(true,facePackIndex);
    }


    /**
     * TimeBoat??????----------------------------
     */

    //????????????
    public void findTimeBoatDevice(){
        bleManager.writeDataToDevice(new byte[]{(byte) 0xAA,0x38,0x01,0x00, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0,0x01},writeBackDataListener);
    }

    //???????????????????????????????????????????????????????????????
    public void sendTimeBoatStartDial(){
        bleManager.writeWatchFaceData(new byte[]{0x03,0x01},writeBackDataListener);
    }

    //???????????????????????????????????????????????????????????????
    public void sendTimeBoatStartDial(WriteBackDataListener writeBackDataListener){
        bleManager.writeWatchFaceData(new byte[]{0x03,0x00},writeBackDataListener);
    }

    //???????????????????????????????????????????????????????????????
    public void sendTimeBoatStartDial(int dialId,WriteBackDataListener writeBackDataListener){
        bleManager.writeWatchFaceData(new byte[]{0x03, (byte) dialId},writeBackDataListener);
    }

    //??????
    public void sendTimeBoatEndDial(){
        bleManager.writeWatchFaceData(new byte[]{0x05,0x00},writeBackDataListener);
    }
    //??????
    public void sendTimeBoatEndDial(WriteBackDataListener writeBackDataListener){
        bleManager.writeWatchFaceData(new byte[]{0x05,0x00},writeBackDataListener);
        if(onTimeBoatSyncQrcodeListener != null){
            onTimeBoatSyncQrcodeListener = null;
        }
    }



    private List<byte[]> timeDialList = new ArrayList<>();
    private int timeBoatSize = 0;

    public void sendTimeBoatDial(List<byte[]> sourceList,OnTimeBoatSyncQrcodeListener timeBoatSyncQrcodeListener){
        this.onTimeBoatSyncQrcodeListener = timeBoatSyncQrcodeListener;
        timeBoatSize = 0;
        timeDialList.clear();
        timeDialList.addAll(sourceList);

        int size = sourceList.size();
        Log.e(TAG,"-----?????????="+size);

        handler.sendEmptyMessageDelayed(0x01,10);
    }


    private void sendToItemDial(byte[] dialArray){

        bleManager.writeWatchFaceData(dialArray, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {

                Log.e(TAG,"---------??????????????????="+Utils.formatBtArrayToString(data));

                //aa 47 04 00 f0 f0 f0 f0 04 01 00 00

                //??????
                if((data.length == 12) && (data[0] & 0xff) == 170 && data[8] == 4 && data[9] == 1 && data[11] == 0 ){
                    if(onTimeBoatSyncQrcodeListener != null){
                        onTimeBoatSyncQrcodeListener.onSyncStatus(timeDialList.size(),timeBoatSize);
                    }
                    handler.sendEmptyMessage(0x01);
                   // handler.sendEmptyMessageDelayed(0x01,0);
                }


                //?????????????????????????????????
                if((data.length == 12) && (data[0] & 0xff) == 170 && data[8] == 4 && data[9] == 2 && data[11] == 0 ){
                    if(onTimeBoatSyncQrcodeListener != null){
                        onTimeBoatSyncQrcodeListener.onSyncStatus(-1,-1);
                    }
                }

                //?????? aa 47 02 00 f0 f0 f0 f0 05 02
                //    aa 47 04 00 f0 f0 f0 f0 04 02 01 00
                if((data.length == 10) && (data[0] & 0xff) == 170  && data[2] == 2 && data[8] == 5 && data[9] == 2){
                    Log.e(TAG,"---------????????????=");
                    if(onTimeBoatSyncQrcodeListener != null){
                        onTimeBoatSyncQrcodeListener.onSyncStatus(-1,-1);
                    }
                }

                else{
                    Log.e(TAG,"---------????????????????????????="+Utils.formatBtArrayToString(data));

                }
            }
        });
//        handler.sendEmptyMessageDelayed(0x01,30);
    }




    private final WriteBackDataListener writeBackDataListener = new WriteBackDataListener() {
        @Override
        public void backWriteData(byte[] data) {

        }
    };


}
