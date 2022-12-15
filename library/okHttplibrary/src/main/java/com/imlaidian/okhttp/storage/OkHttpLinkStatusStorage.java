package com.imlaidian.okhttp.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.imlaidian.okhttp.model.OkHttpLinkStatusModel;
import com.imlaidian.utilslibrary.analyseQueryData.QueryDbResultDetailItem;
import com.imlaidian.utilslibrary.analyseQueryData.QueryDbResultEachDataItem;
import com.imlaidian.utilslibrary.analyseQueryData.QueryDbResultManager;
import com.imlaidian.utilslibrary.analyseQueryData.QueryDbResultSummaryCodeItem;
import com.imlaidian.utilslibrary.analyseQueryData.QueryDbResultSummaryItem;
import com.imlaidian.utilslibrary.analyseQueryData.QueryDbResultTLNameItem;
import com.imlaidian.utilslibrary.utils.LogUtil;

import java.util.ArrayList;


/**
 * Created by zbo on 17/2/15.
 */

public class OkHttpLinkStatusStorage extends SQLiteOpenHelper {

    public final  String TAG = OkHttpLinkStatusStorage.class.getSimpleName();

    public static final String TABLE_NAME = "link_status";
    public static final String COLUMN_LINK_STATUS = "link_status" ;
    private static  final  String  DB_NAME ="httpLinkStatus.db" ;

    public static final String COLUMN_ID ="_Id";

    public static final String COLUMN_LINK_DATE = "link_date";

    public static final String COLUMN_RESERVE_ONE= "reserve_one";

    public static final String COLUMN_RESERVE_TWO ="reserve_two";

    public static final String COLUMN_RESERVE_THREE = "reserve_three";
    public static final String COLUMN_RESERVE_FOUR= "reserve_four";
    private static final int linkDayCount = 7 ;
    private long recordWeekTimer = linkDayCount*24*60*60*1000 ;

    private QueryDbResultManager mResultTable = null;
    private static final String CREATE_SQL =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    COLUMN_LINK_STATUS + " INTEGER NOT NULL," +
                    COLUMN_LINK_DATE + " TEXT," +
                    COLUMN_RESERVE_ONE + " TEXT," +
                    COLUMN_RESERVE_TWO + " TEXT," +
                    COLUMN_RESERVE_THREE + " TEXT," +
                    COLUMN_RESERVE_FOUR + " TEXT" +
                    ");";

    public OkHttpLinkStatusStorage(Context context) {
        super(context, DB_NAME, null, 1);
        //  后面增加的 表 放onCreate 不会生成 ，db 创建后 onCreate 不会执行
        LogUtil.d(TAG, "start OkHttpLinkStatusStorage");
        mResultTable = new QueryDbResultManager("NetLinkDataResultTable", this);
        LogUtil.d(TAG, "end OkHttpLinkStatusStorage");
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtil.d(TAG , "onCreate OkHttpLinkStatusStorage table");
        db.execSQL(CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtil.d(TAG , "there is no  update");
    }

    public synchronized void insert(OkHttpLinkStatusModel item){
        SQLiteDatabase database =null ;
        try {
            database = getWritableDatabase();
            if(database!=null){
                long id = database.insert(TABLE_NAME , null , getContentValues(item));
                if (id <= 0) {
                    LogUtil.d(TAG, "insert id:" + id);
                }
            }
        }catch (Exception e){
             e.printStackTrace();
             LogUtil.s(TAG , "insert e=" ,e);
        }
    }

    public OkHttpLinkStatusModel getLastItem(String tableName) {
        OkHttpLinkStatusModel item = null;
        Cursor cursor = null;

        try {
            cursor = getReadableDatabase().query(tableName,
                    null,
                    null,
                    null,
                    null,
                    null,
                    COLUMN_ID + " DESC",
                    "1");

            if (cursor.moveToLast()) {
                item = getLinkModel(cursor);
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

    public synchronized OkHttpLinkStatusModel getFirstLinkData(){
        OkHttpLinkStatusModel  linkStatus=  null;
        SQLiteDatabase database =null;
        Cursor cursor =null ;
        try {

            database =getReadableDatabase();
            cursor = database.query(TABLE_NAME ,new String[]{ " * "} ,null , null , null ,null ,null , "1");
            if(cursor!=null && cursor.getCount()>0){
                if(cursor.moveToFirst()){
                    linkStatus=  getLinkModel(cursor);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            LogUtil.s(TAG , "getFirstLinkData e=" ,e);
        }finally {
            close(cursor);
        }

        return  linkStatus ;
    }

    public synchronized ArrayList<OkHttpLinkStatusModel> getAllLinkData(){
        ArrayList<OkHttpLinkStatusModel> linkStatusList= new ArrayList<OkHttpLinkStatusModel>();
        SQLiteDatabase database =null;
        Cursor cursor = null ;
        try {
            database =getReadableDatabase();
            cursor = database.query(TABLE_NAME ,new String[]{ " * "} ,COLUMN_LINK_DATE + " >? " , new String[]{Long.toString(System.currentTimeMillis() -recordWeekTimer)} , null ,null ,null);
            if(cursor!=null && cursor.getCount()>0){
                while (cursor.moveToNext()){
                    linkStatusList.add(getLinkModel(cursor));
                }

            }

        }catch (Exception e){
            e.printStackTrace();
            LogUtil.s(TAG , "insert e=" ,e);
        }finally {
            close(cursor);
        }

        return  linkStatusList ;
    }

    public synchronized  ArrayList<OkHttpLinkStatusModel> getDetailLinkData(long startTimeStamp , long endTimeStamp){

        ArrayList<OkHttpLinkStatusModel> linkStatusList= new ArrayList<OkHttpLinkStatusModel>();
        SQLiteDatabase database =null;
        Cursor cursor =null ;
        try {
            database =getReadableDatabase();
            cursor = database.query(TABLE_NAME ,new String[]{ " * "} ,COLUMN_LINK_DATE + " >?  and "  + COLUMN_LINK_DATE + " < ? ", new String[]{ "" + startTimeStamp , "" +endTimeStamp} , null ,null ,null);
            if(cursor!=null && cursor.getCount()>0){
                while (cursor.moveToNext()){
                    linkStatusList.add(getLinkModel(cursor));
                }

            }

        }catch (Exception e){
            e.printStackTrace();
            LogUtil.s(TAG , "getDetailLinkData e=" ,e);
        }finally {
            close(cursor);
        }

        return  linkStatusList ;

    }


    private OkHttpLinkStatusModel getLinkModel(Cursor cursor){
        OkHttpLinkStatusModel item = new OkHttpLinkStatusModel();
        if(cursor !=null){
            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            item.setLinkDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LINK_DATE)));
            item.setLinkStatus(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LINK_STATUS)));
            item.setReserveOne(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESERVE_ONE)));
            item.setReserveTwo(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESERVE_TWO)));
            item.setReserveThree(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESERVE_THREE)));
            item.setReserveFour(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESERVE_FOUR)));
        }
        return  item ;

    }


    private ContentValues getContentValues(OkHttpLinkStatusModel model){
        ContentValues  contentValues = new ContentValues();
        contentValues.put(COLUMN_LINK_DATE, model.getLinkDate());
        contentValues.put(COLUMN_LINK_STATUS,model.getLinkStatus());
        contentValues.put(COLUMN_RESERVE_ONE,model.getReserveOne());
        contentValues.put(COLUMN_RESERVE_TWO,model.getReserveTwo());
        contentValues.put(COLUMN_RESERVE_THREE,model.getReserveThree());
        contentValues.put(COLUMN_RESERVE_FOUR,model.getReserveFour());
        return contentValues;
    }

    public synchronized  int cleanNoUseData(){
        SQLiteDatabase database =null ;
        int count = 0 ;
        try {
            database = getWritableDatabase();
            if(database!=null){
                LogUtil.d(TAG , "cleanNoUseData");
                count= database.delete(TABLE_NAME , COLUMN_LINK_DATE + " <? " ,new String[]{ Long.toString(System.currentTimeMillis() - recordWeekTimer)});
            }

        }catch (Exception e){
            e.printStackTrace();
            LogUtil.s(TAG , "insert e=" ,e);

        }
        return count;
    }

    /*关闭数据库*/
    private  void close( Cursor c) {
        if (c != null) {
            c.close();
        }
    }


    /// =========== result table =============
    public void createTable(QueryDbResultTLNameItem tlNameItem) {
        mResultTable.createTable(tlNameItem);
    }

    public boolean isTLNameTableExist(String name) {
        return mResultTable.isExist(name);
    }

    public void updateTLNameTable(QueryDbResultTLNameItem tlNameItem) {
        mResultTable.updateTable(tlNameItem);
    }

    /// =========== result table =============
    public void createTLNameTable(QueryDbResultTLNameItem tlNameItem) {
        mResultTable.createTable(tlNameItem);
    }

    public QueryDbResultTLNameItem createTLNameItem(String name, String prefixIden, String suffixIden,
                                                      long beginTimestamp, long endTimestamp) {
        return new QueryDbResultTLNameItem(name, prefixIden, suffixIden, beginTimestamp, endTimestamp);
    }




    public QueryDbResultTLNameItem getTLNameItem(String name) {
        return mResultTable.getTLNameItem(name);
    }

    public ArrayList<QueryDbResultTLNameItem> getAllTLNameItems() {
        return mResultTable.getAllTLNameItems();
    }

    public QueryDbResultTLNameItem getTLNameFirstItemWithPrefixIden(String prefixIden) {
        return mResultTable.getTLNameFirstItemWithPrefixIden(prefixIden);
    }

    public QueryDbResultTLNameItem getTLNameLastItemWithPrefixIden(String prefixIden) {
        return mResultTable.getTLNameLastItemWithPrefixIden(prefixIden);
    }

    public ArrayList<QueryDbResultTLNameItem> getTLNameAllItemWithPrefixIden(String prefixIden) {
        return mResultTable.getTLNameListWithPrefixIden(prefixIden);
    }

    public QueryDbResultTLNameItem getTLNameFirstItemWithSuffixIden(String suffixIden) {
        return mResultTable.getTLNameFirstItemWithSuffixIden(suffixIden);
    }

    public QueryDbResultTLNameItem getTLNameLastItemWithSuffixIden(String suffixIden) {
        return mResultTable.getTLNameLastItemWithSuffixIden(suffixIden);
    }

    public ArrayList<QueryDbResultTLNameItem> getTLNameAllItemWithSuffixIden(String suffixIden) {
        return mResultTable.getTLNameListWithSuffixIden(suffixIden);
    }



    /// =================== result table item
    public void insertResultItem(QueryDbResultTLNameItem tlNameItem, QueryDbResultDetailItem resultDetailItem) {
        mResultTable.insert(tlNameItem, resultDetailItem);
    }

    public QueryDbResultDetailItem getResultFirstItem(QueryDbResultTLNameItem tlNameItem) {
        return mResultTable.getResultFirstItem(tlNameItem);
    }

    public QueryDbResultDetailItem getResultLastItem(QueryDbResultTLNameItem tlNameItem) {
        return mResultTable.getResultLastItem(tlNameItem);
    }

    public ArrayList<QueryDbResultDetailItem> getResultAllList(QueryDbResultTLNameItem tlNameItem) {
        return mResultTable.getResultList(tlNameItem);
    }

    public void updateResultItem(QueryDbResultTLNameItem tlNameItem, QueryDbResultDetailItem detailItem) {
        mResultTable.updateResultItem(tlNameItem, detailItem);
    }

    public void deleteResultItem(QueryDbResultTLNameItem tlNameItem, QueryDbResultDetailItem detailItem) {
        mResultTable.deleteResultItem(tlNameItem, detailItem);
    }

    public ArrayList<QueryDbResultDetailItem> getResultListWithCode(QueryDbResultTLNameItem tlNameItem, int code) {
        return mResultTable.getResultListWithCode(tlNameItem, code);
    }

    public ArrayList<Integer> getDistinctSourceIdList(QueryDbResultTLNameItem tlNameItem) {
        return mResultTable.getDistinctSourceIdList(tlNameItem);
    }

    public ArrayList<QueryDbResultDetailItem> getResultListWithSourceId(QueryDbResultTLNameItem tlNameItem, int sourceId) {
        return mResultTable.getResultListWithSourceId(tlNameItem, sourceId);
    }

    /// 根据sourceId获取每条记录的错误代码
    public ArrayList<QueryDbResultEachDataItem> getResultAllEachDataListWithSourceId(QueryDbResultTLNameItem tlNameItem) {
        return mResultTable.getResultAllEachDataListWithSourceId(tlNameItem);
    }

    /// === summary table
    public boolean insertSummaryItem(QueryDbResultSummaryItem item) {
        return mResultTable.insertSummaryItem(item);
    }

    public boolean deleteSummaryItem(QueryDbResultSummaryItem item) {
        return mResultTable.deleteSummaryItem(item);
    }

    public boolean updateSummaryItem(QueryDbResultSummaryItem item) {
        return mResultTable.updateSummaryItem(item);
    }

    public QueryDbResultSummaryItem getFirstSummaryItem() {
        return mResultTable.getFirstSummaryItem();
    }

    public QueryDbResultSummaryItem getLastSummaryItem() {
        return mResultTable.getLastSummaryItem();
    }

    public QueryDbResultSummaryItem querySummaryWithName(String name) {
        return mResultTable.querySummaryItemWithName(name);
    }

    /// === summary code table
    public boolean insertSummaryCodeItem(QueryDbResultSummaryCodeItem item) {
        return mResultTable.insertSummaryCodeItem(item);
    }

    public boolean deleteSummaryCodeItem(QueryDbResultSummaryCodeItem item) {
        return mResultTable.deleteSummaryCodeItem(item);
    }

    public boolean deleteSummaryCodeItem(int summaryId) {
        return mResultTable.deleteSummaryCodeItem(summaryId);
    }

    public boolean updateSummaryCodeItem(QueryDbResultSummaryCodeItem item) {
        return mResultTable.updateSummaryCodeItem(item);
    }

    public QueryDbResultSummaryCodeItem getFirstSummaryCodeItem() {
        return mResultTable.getFirstSummaryCodeItem();
    }

    public QueryDbResultSummaryCodeItem getLastSummaryCodeItem() {
        return mResultTable.getLastSummaryCodeItem();
    }

    public ArrayList<QueryDbResultSummaryCodeItem> getSummaryCodeList(int summaryId) {
        return mResultTable.getSummaryCodeList(summaryId);
    }
}
