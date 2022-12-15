package com.imlaidian.utilslibrary.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.imlaidian.utilslibrary.UtilsApplication;

import java.util.concurrent.ExecutionException;

public class UToast {

    public static Context mContext = UtilsApplication.getInstance().getApplicationContext();

    public static void showShortToast(String msg) {
        try{
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void showLongToast(String msg) {

        try{
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void   showShortToastTop(String msg){
        try {
            Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 100);
            toast.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void   showShortToastMiddle(String msg){
        try{
            Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
