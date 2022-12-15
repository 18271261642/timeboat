package com.imlaidian.utilslibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.imlaidian.utilslibrary.UtilsApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;


public class SharedPreferencesUtil {
    private static SharedPreferences.Editor mSaveEditor = null;
    private static SharedPreferences mSaveInfo = null;
    private static SharedPreferencesUtil mInstance = null;

    public static synchronized SharedPreferencesUtil getInstance() {
        if (null == mInstance) {
            mInstance = new SharedPreferencesUtil();
        }
        return mInstance;
    }

    private SharedPreferencesUtil() {
        final String SettingName = "net.sgztech.timeboat.savedata";
        Context context = UtilsApplication.getInstance().getApplicationContext();
        mSaveInfo = context.getSharedPreferences(SettingName, Context.MODE_PRIVATE);
        mSaveEditor = mSaveInfo.edit();
    }

    public boolean isContainKey(String key) {
        return mSaveInfo.contains(key);
    }

    public String getString(String key) {
        return mSaveInfo.getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        return mSaveInfo.getString(key, defaultValue);
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, String> getAll() {
        return (HashMap<String, String>)mSaveInfo.getAll();
    }

    public boolean setString(String key, String value) {
        if (mSaveInfo.contains(key)) {
            mSaveEditor.remove(key);
        }

        mSaveEditor.putString(key, value);

        return mSaveEditor.commit();
    }

    public boolean setLong(String key, long value) {
        if (mSaveInfo.contains(key)) {
            mSaveEditor.remove(key);
        }

        mSaveEditor.putLong(key, value);

        return mSaveEditor.commit();
    }

    public Long getLong(String key) {
        return  mSaveInfo.getLong(key, 0);
    }


    public boolean setInt(String key, int value) {
        if (mSaveInfo.contains(key)) {
            mSaveEditor.remove(key);
        }

        mSaveEditor.putInt(key, value);

        return mSaveEditor.commit();
    }

    public int getInt(String key) {
        return  mSaveInfo.getInt(key, 0);
    }


    public boolean remove(String key) {
        if(mSaveInfo.contains(key)){
            mSaveEditor.remove(key);
        }
        return mSaveEditor.commit();
    }

    public void clearAll() {
        mSaveEditor.clear();
        mSaveEditor.commit();
    }



    public void putObject(String key ,Object object){
        String objectClass = "" ;
        if(object!=null && object.getClass()!=null){

            objectClass = object.getClass().getName();

            objectClass= jsonUtil.getObject(object);
            putObjectString(key , objectClass);
        }
    }

    public void putObjectString(String key ,String json){

        setString(key ,json);
    }

    public  <T> T getObjectString(String key , Class<T> classname){
           String jsonString =  getString(key );
      return   jsonUtil.getObject(jsonString , classname) ;

    }

    public synchronized  void setSerializable(String key, Object object) {
        // 此处不能写log 否者 Logutils 会出问题
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            // Device为自定义类
            // 创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);

            // 将对象写入字节流
            oos.writeObject(object);

            // 将字节流编码成base64的字符串
            String oAuth_Base64 = new String(Base64.encode(outputStream.toByteArray(),
                    Base64.DEFAULT));

            mSaveEditor.putString(key, oAuth_Base64);
            mSaveEditor.commit();
        } catch (Exception e) {
            // 此处不能写log 否者 Logutils 会出问题
            e.printStackTrace();
            Log.d("SharedPreferencesUtil" , "e =" + e);
        }
    }

    public Object getSerializable(String key) {
        Object readObj = null;

        if (mSaveInfo.contains(key)) {

            try {
                String productBase64 = mSaveInfo.getString(key, null);

                // 读取字节
                byte[] base64 = Base64.decode(productBase64.getBytes(),
                        Base64.DEFAULT);

                // 封装到字节流
                ByteArrayInputStream bais = new ByteArrayInputStream(base64);
                // 再次封装
                ObjectInputStream bis = new ObjectInputStream(bais);

                // 读取对象
                readObj = bis.readObject();
            } catch (Exception e) {
                Log.d("SharedPreferencesUtil" , "getSerializable error="  ,e );
                e.printStackTrace();
                return null ;
            }
        }

        return readObj;
    }


    public boolean setBoolean(String key, boolean value) {
        if (mSaveInfo.contains(key)) {
            mSaveEditor.remove(key);
        }

        mSaveEditor.putBoolean(key, value);

        return mSaveEditor.commit();
    }

    public boolean getBoolean(String key) {
        return mSaveInfo.getBoolean(key, false);
    }

    public boolean getBoolean(String key ,boolean defaultValue) {
        return mSaveInfo.getBoolean(key, defaultValue);
    }
}
