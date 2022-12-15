package com.imlaidian.okhttp.download;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.imlaidian.utilslibrary.utils.LogUtil;
import com.imlaidian.utilslibrary.utils.Preconditions;
import java.util.ArrayList;

import static com.imlaidian.okhttp.download.DownloadManager.MEDIA_DOWNLOAD_TYPE;
import static com.imlaidian.okhttp.download.DownloadManager.NORMAL_DOWNLOAD_TYPE;
import static com.imlaidian.okhttp.download.DownloadManager.OTHER_DOWNLOAD_TYPE;
import static com.imlaidian.okhttp.download.DownloadManager.PATCH_DOWNLOAD_TYPE;
import static com.imlaidian.okhttp.download.DownloadManager.ZIP_DOWNLOAD_TYPE;


/**
 * Created by zbo on 17/7/12.
 */

public class DownLoadDatabaseHelp extends SQLiteOpenHelper {

    private static final String TAG = DownLoadDatabaseHelp.class.getSimpleName();
    private static final String DA_NAME = "downLoad.db";

    //下载server app 记录
    private static final String MEDIA_TABLE_NAME = "media_down_load";
    // 下载 皮肤包 记录
    private static final String ZIP_TABLE_NAME = "zip_down_load";

    private static final String NORMAL_TABLE_NAME = "normal_down_load";

    private static final String OTHER_TABLE_NAME = "other_down_load";

    private static final String PATCH_TABLE_NAME = "patch_down_load";

    private DownloadDataTable zipDownLoadDataTable;
    private DownloadDataTable mediaDownloadDataTable;
    private DownloadDataTable otherDownLoadDataTable;
    private DownloadDataTable normalDownLoadDataTable;
    private DownloadDataTable patchDownLoadDataTable;

    @SuppressLint("RestrictedApi")
    public DownLoadDatabaseHelp(Context context) {
        super(context, DA_NAME, null, 2);
        Preconditions.checkNotNull(context);
        normalDownLoadDataTable = new DownloadDataTable(this, NORMAL_TABLE_NAME);
        zipDownLoadDataTable = new DownloadDataTable(this, ZIP_TABLE_NAME);
        mediaDownloadDataTable = new DownloadDataTable(this, MEDIA_TABLE_NAME);
        otherDownLoadDataTable = new DownloadDataTable(this, OTHER_TABLE_NAME);
        patchDownLoadDataTable = new DownloadDataTable(this, PATCH_TABLE_NAME);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtil.d(TAG, "onCreate");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtil.d(TAG, "update no action");
    }

    public DownLoadSourceInfoModel getInfoByUrl(int type, String url) {
        return getTableByType(type).getInfoByUrl(url);
    }

    public DownLoadSourceInfoModel getInfoByTaskId(int type, String taskId) {
        return getTableByType(type).getInfoByTaskId(taskId);
    }

    public DownLoadSourceInfoModel getLastTaskInfo(int type, String url) {

        return getTableByType(type).getLastTaskInfo(url);

    }

    public int getDownloadTimes(int type, long timeStamp, String md5, String url) {
        return getTableByType(type).getDownloadTimes(timeStamp, md5, url);
    }


    public void putInfoByUrl(int type, String url, DownLoadSourceInfoModel sourceInfo) {
        getTableByType(type).putInfoByUrl(url, sourceInfo);
    }

    public ArrayList<DownLoadSourceInfoModel> getAllInfo(int type) {
        return getTableByType(type).getAllInfo();
    }


    public void putInfoByTaskId(int type, String taskId, DownLoadSourceInfoModel sourceInfo) {
        getTableByType(type).putInfoByTaskId(taskId, sourceInfo);
    }

    public int delete(int type, String taskId) {
        return getTableByType(type).delete(taskId);

    }


    private DownloadDataTable getTableByType(int type) {

        switch (type) {
            case MEDIA_DOWNLOAD_TYPE:
                return mediaDownloadDataTable;

            case ZIP_DOWNLOAD_TYPE:
                return zipDownLoadDataTable;
            case NORMAL_DOWNLOAD_TYPE:
                return normalDownLoadDataTable;
            case OTHER_DOWNLOAD_TYPE:
                return otherDownLoadDataTable;
            case PATCH_DOWNLOAD_TYPE:
                return patchDownLoadDataTable;
            default:
                return normalDownLoadDataTable;
        }

    }

}
