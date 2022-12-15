package com.imlaidian.okhttp.download;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.imlaidian.utilslibrary.utils.DateUtil;
import com.imlaidian.utilslibrary.utils.LogUtil;

import java.util.ArrayList;

import static com.imlaidian.utilslibrary.utils.Preconditions.checkAllNotNull;
import static com.imlaidian.utilslibrary.utils.Preconditions.checkNotNull;

public class DownloadDataTable {

    private static final  String  TAG = DownloadDataTable.class.getSimpleName() ;
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_URL = "url";
    private static final String COLUMN_LENGTH = "length";
    private static final String COLUMN_COMPLETED_SIZE="completed_size";
    private static final String COLUMN_SAVE_FILE_PATH="save_file_path";
    private static final String COLUMN_SAVE_FILE_NAME="save_file_name";
    private static final String COLUMN_TASK_ID = "task_id" ;
    private static final String COLUMN_IP = "ip" ;
    private static final String COLUMN_MIME = "mime";
    private static final String COLUMN_RELEASE_ID = "province" ;
    private static final String COLUMN_MATERIAL_ID ="city" ;
    private static final String COLUMN_DOWNLOAD_STATUS = "down_load_status";
    private static final String COLUMN_MD5 ="md5" ;
    private static final String COLUMN_TYPE= "type" ;
    private static final String COLUMN_REPEAT_TIMES = "repeat_times" ;
    private static final String COLUMN_TIME_STAMP="time_stamp" ;
    private static final String COLUMN_RESERVE1= "reserve1" ;
    private static final String COLUMN_RESERVE2= "reserve2" ;
    private static final String COLUMN_RESERVE3= "reserve3" ;
    private static final String COLUMN_RESERVE4= "reserve4" ;

    private static final int keepDayCount = 14 ;
    private long recordWeekTimer = keepDayCount*24*60*60*1000 ;

    private int KEEP_RECORD_COUNT = 30000 ;

    private SQLiteOpenHelper mSQLiteOpenHelper ;
    private String TABLE_NAME = "" ;

    public DownloadDataTable(SQLiteOpenHelper helper ,String tableName) {
        mSQLiteOpenHelper = helper;
        TABLE_NAME = tableName ;
        createTable();
        cleanNoUseData();
    }

    private void createTable() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL(createSql());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(TAG, e.toString());
        }
    }


    private String   createSql(){
        String CREATE_SQL =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        COLUMN_URL + " TEXT NOT NULL," +
                        COLUMN_MIME + " TEXT," +
                        COLUMN_MD5 + " TEXT," +
                        COLUMN_TIME_STAMP + " TEXT," +
                        COLUMN_RELEASE_ID + " TEXT," +
                        COLUMN_MATERIAL_ID + " TEXT," +
                        COLUMN_COMPLETED_SIZE + " INTEGER," +
                        COLUMN_DOWNLOAD_STATUS + " INTEGER," +
                        COLUMN_TYPE + " INTEGER," +
                        COLUMN_REPEAT_TIMES + " INTEGER," +
                        COLUMN_SAVE_FILE_NAME + " TEXT," +
                        COLUMN_SAVE_FILE_PATH + " TEXT," +
                        COLUMN_TASK_ID + " TEXT," +
                        COLUMN_IP + " TEXT," +
                        COLUMN_RESERVE1 + " TEXT," +
                        COLUMN_RESERVE2 + " TEXT," +
                        COLUMN_RESERVE3 + " TEXT," +
                        COLUMN_RESERVE4 + " TEXT," +
                        COLUMN_LENGTH + " INTEGER" +
                        ");";

        return  CREATE_SQL ;
    }

    private DownLoadSourceInfoModel convert(Cursor cursor) {
        DownLoadSourceInfoModel infoModel = new DownLoadSourceInfoModel() ;
        infoModel.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        infoModel.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL)));
        infoModel.setTaskId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_ID)));
        infoModel.setIp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IP)));
        infoModel.setLength(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_LENGTH)));
        infoModel.setMime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MIME)))  ;
        infoModel.setMd5(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MD5)));
        infoModel.setTimeStamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME_STAMP)));
        infoModel.setAdReleaseId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RELEASE_ID)));
        infoModel.setMaterialId( cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MATERIAL_ID)));
        infoModel.setSaveFilePath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SAVE_FILE_PATH)));
        infoModel.setCompletedSize(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED_SIZE)));
        infoModel.setSaveFileName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SAVE_FILE_NAME)));
        infoModel.setDownloadStatus(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DOWNLOAD_STATUS)));
        infoModel.setRepeatTime(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REPEAT_TIMES)));
        infoModel.setType(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TYPE)));
        infoModel.setReserve1(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESERVE1)));
        infoModel.setReserve2(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESERVE2)));
        infoModel.setReserve3(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESERVE3)));
        infoModel.setReserve4(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESERVE4)));
        return infoModel ;
    }

    private ContentValues convert(DownLoadSourceInfoModel sourceInfo) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_URL, sourceInfo.getUrl());
        values.put(COLUMN_LENGTH, sourceInfo.getLength());
        values.put(COLUMN_MIME, sourceInfo.getMime());
        values.put(COLUMN_MD5 ,sourceInfo.getMd5());
        values.put(COLUMN_TIME_STAMP ,sourceInfo.getTimeStamp());
        values.put(COLUMN_RELEASE_ID ,sourceInfo.getAdReleaseId());
        values.put(COLUMN_MATERIAL_ID ,sourceInfo.getMaterialId());
        values.put(COLUMN_COMPLETED_SIZE ,sourceInfo.getCompletedSize());
        values.put(COLUMN_SAVE_FILE_PATH,sourceInfo.getSaveFilePath());
        values.put(COLUMN_SAVE_FILE_NAME ,sourceInfo.getSaveFileName());
        values.put(COLUMN_DOWNLOAD_STATUS ,sourceInfo.getDownloadStatus());
        values.put(COLUMN_TASK_ID ,sourceInfo.getTaskId());
        values.put(COLUMN_IP ,sourceInfo.getIp());
        values.put(COLUMN_TYPE ,sourceInfo.getType());
        values.put(COLUMN_REPEAT_TIMES ,sourceInfo.getRepeatTime());
        values.put(COLUMN_RESERVE1 ,sourceInfo.getReserve1());
        values.put(COLUMN_RESERVE2 ,sourceInfo.getReserve2());
        values.put(COLUMN_RESERVE3 ,sourceInfo.getReserve3());
        values.put(COLUMN_RESERVE4 ,sourceInfo.getReserve4());
        return values;
    }

    private SQLiteDatabase getWritableDatabase() {
        return  mSQLiteOpenHelper.getWritableDatabase();
    }

    private SQLiteDatabase getReadableDatabase() {
        return mSQLiteOpenHelper.getReadableDatabase();
    }

    public synchronized ArrayList<DownLoadSourceInfoModel> getAllInfo(){
        ArrayList<DownLoadSourceInfoModel> downLoadList = new ArrayList<DownLoadSourceInfoModel>();
        Cursor cursor = null ;
        try {
            SQLiteDatabase db = getReadableDatabase() ;
            cursor = db.query(TABLE_NAME , null ,null ,null ,null ,null ,null);
            if(cursor!=null && cursor.getCount()>0){
                while (cursor.moveToNext()){
                    downLoadList.add(convert(cursor));
                }

            }
            if(cursor!=null){
                cursor.close();
            }
            db.close();
            return  downLoadList ;

        }catch (Exception e){
            LogUtil.e(TAG ,e.toString());

        }

        return  downLoadList ;
    }

    public synchronized DownLoadSourceInfoModel getInfoByUrl(String url) {
        checkNotNull(url);
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(TABLE_NAME, new String[]{ " * "} , COLUMN_URL + "=?", new String[]{url}, null, null, null);
            return cursor == null || !cursor.moveToFirst() ? null : convert(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public synchronized DownLoadSourceInfoModel getInfoByTaskId(String taskId) {
        checkNotNull(taskId);
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(TABLE_NAME, new String[]{ " * "} , COLUMN_TASK_ID + "=?", new String[]{taskId}, null, null, null);
            return cursor == null || !cursor.moveToFirst() ? null : convert(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public synchronized DownLoadSourceInfoModel getLastTaskInfo(String url) {
        checkNotNull(url);
        Cursor cursor = null;
        DownLoadSourceInfoModel item = null;
        try {
            cursor = getReadableDatabase().query(TABLE_NAME,
                    new String[]{ " * "} ,
                    COLUMN_URL + "=?",
                    new String[]{url},
                    null,
                    null,
                    COLUMN_ID + " DESC",
                    "1");

            if (cursor.moveToLast()) {
                item = convert(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(TAG, e.toString());
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return item ;

    }


    public synchronized int getDownloadTimes(long timeStamp ,String md5 ,String url){
        int downLoadTime = 0 ;
        long dayBeginTimestamp = DateUtil.getCurDayBeginTimestamp(timeStamp) ;
        long dayEndBeginTimestamp = DateUtil.getCurDayEndTimestamp(timeStamp) ;
        checkAllNotNull(timeStamp ,md5 ,url);
        Cursor cursor = null;
        ArrayList<DownLoadSourceInfoModel> downLoadList = new ArrayList<DownLoadSourceInfoModel>();
        try {
            cursor = getReadableDatabase().query(TABLE_NAME, new String[]{ " * "} , COLUMN_URL + "=? AND " + COLUMN_MD5 + "=? AND " + COLUMN_TIME_STAMP + ">=? AND " + COLUMN_TIME_STAMP + "<?" , new String[]{url,md5 ,"" +dayBeginTimestamp , "" +dayEndBeginTimestamp}, null, null, null);

            if(cursor!=null && cursor.getCount()>0){
                while (cursor.moveToNext()){
                    downLoadList.add(convert(cursor));
                }

            }

             downLoadTime = downLoadList.size() ;
            LogUtil.d(TAG, "getDownloadTimes downLoadTime = " + downLoadTime + ",url" +url  + ",md5=" +md5);
        }catch ( Exception e){
            e.printStackTrace();
            LogUtil.s(TAG , "insert e=" ,e);
        }finally {
            if (cursor != null) {
                cursor.close();
            }

        }
        return downLoadTime ;
    }

    public synchronized  void cleanNoUseData(){
        SQLiteDatabase database =null ;
        if(DateUtil.isDateValid(System.currentTimeMillis())){

            try {
                database = getWritableDatabase();
                DownLoadSourceInfoModel lastItem = getLastItem();
                if (null != lastItem) {
                    if (lastItem.getId() > KEEP_RECORD_COUNT) {
                        int preId = lastItem.getId() - KEEP_RECORD_COUNT;
                        int row = database.delete(TABLE_NAME, COLUMN_ID + "<?", new String[]{"" + preId});

                        LogUtil.d(TAG, "clean invalid record row = " + row);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                LogUtil.s(TAG , "insert e=" ,e);

            }
        }

    }




    public synchronized DownLoadSourceInfoModel getLastItem() {
        DownLoadSourceInfoModel item = null;
        Cursor cursor = null;

        try {
            cursor = getReadableDatabase().query(TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    COLUMN_ID + " DESC",
                    "1");

            if (cursor.moveToLast()) {
                item = convert(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(TAG, e.toString());
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }


        return item;
    }

    public synchronized void putInfoByUrl(String url, DownLoadSourceInfoModel sourceInfo) {
        try {
            checkAllNotNull(url, sourceInfo);
            DownLoadSourceInfoModel sourceInfoFromDb = getInfoByUrl(url);
            boolean exist = sourceInfoFromDb != null;
            SQLiteDatabase db =getWritableDatabase();
            ContentValues contentValues = convert(sourceInfo);
            if (exist) {
                db.update(TABLE_NAME, contentValues, COLUMN_URL + "=?", new String[]{url});
            } else {
                db.insert(TABLE_NAME, null, contentValues);
            }

            db.close();
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.d(TAG, e.toString());
        }

    }

    public synchronized void putInfoByTaskId(String taskId, DownLoadSourceInfoModel sourceInfo) {
        try {
            checkAllNotNull(taskId, sourceInfo);
            DownLoadSourceInfoModel sourceInfoFromDb = getInfoByTaskId(taskId);
            boolean exist = sourceInfoFromDb != null;
            ContentValues contentValues = convert(sourceInfo);
            SQLiteDatabase db =getWritableDatabase();
            if (exist) {
                db.update(TABLE_NAME, contentValues, COLUMN_TASK_ID + "=?", new String[]{taskId});
            } else {
                db.insert(TABLE_NAME, null, contentValues);
            }

            db.close();
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.d(TAG, e.toString());
        }

    }

    public synchronized int delete(String taskId){
        int  data =0;
        try{

            data = getWritableDatabase().delete(TABLE_NAME , COLUMN_TASK_ID + "=?" ,new String[]{taskId});

        }catch (Exception e){
            e.printStackTrace();
            LogUtil.d(TAG, e.toString());
        }
        return data ;
    }

    /*关闭数据库*/
    public  void close(SQLiteDatabase database, Cursor c) {
        if (c != null) {
            c.close();
        }
        if (database != null) {
            database.close();
        }
    }

}
