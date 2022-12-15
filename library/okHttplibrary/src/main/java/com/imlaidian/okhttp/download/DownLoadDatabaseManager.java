package com.imlaidian.okhttp.download;


import android.content.Context;

import com.imlaidian.utilslibrary.UtilsApplication;

import java.util.ArrayList;


/**
 * Created by zbo on 17/7/12.
 */

public class DownLoadDatabaseManager {

    private static DownLoadDatabaseManager instance ;
    private DownLoadDatabaseHelp databaseHelp ;
    public static DownLoadDatabaseManager getInstance(){

        if(instance ==null){

            synchronized (DownLoadDatabaseManager.class){

                if(instance ==null){
                    instance = new DownLoadDatabaseManager();
                }
            }

        }
        return  instance ;

    }

    public DownLoadDatabaseManager() {
        Context context = UtilsApplication.getInstance().getApplicationContext() ;
        databaseHelp = new DownLoadDatabaseHelp(context);
    }


    public DownLoadSourceInfoModel getUrl(int type  ,String url ){
       return databaseHelp.getInfoByUrl( type  ,url) ;
    }

    public DownLoadSourceInfoModel getInfoByTaskId(int type ,String taskId){
      return   databaseHelp.getInfoByTaskId(type ,taskId);

    }

    public DownLoadSourceInfoModel getLastTaskInfo(int type  ,String url){

        return   databaseHelp.getLastTaskInfo(type ,url);

    }

    public void putInfoByTaskId(int type ,String taskId, DownLoadSourceInfoModel sourceInfo){

          databaseHelp.putInfoByTaskId(type ,taskId ,sourceInfo);
    }

    public void putInfoByUrl(int type ,String url , DownLoadSourceInfoModel infoModel){
        databaseHelp.putInfoByUrl(type ,url , infoModel) ;
    }


    public ArrayList<DownLoadSourceInfoModel> getAllDownloadInfo(int type ){
        return  databaseHelp.getAllInfo( type );
    }

    public int delete(int type ,String taskId){
        return  databaseHelp.delete(type ,taskId);
    }



    public int  getDownLoadTimes(int type ,long timestamp , String md5 ,String url){
        return  databaseHelp.getDownloadTimes(type ,timestamp ,md5  ,url);
    }
}
