package com.imlaidian.utilslibrary.analyseQueryData;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.imlaidian.utilslibrary.utils.LogUtil;
import com.imlaidian.utilslibrary.utils.StringsUtils;
import java.util.ArrayList;


/**
 * Created by jun on 2017/8/18.
 * query data result table-name list table
 */

public class QueryDbResultTLNameTable {
    private String TAG = "QueryDataResultTLNameTable";

    /// 表名
    private static final String TABLE_NAME = "TableList";

    /// 以下是各个字段
    private static final String COLUMN_ID = "id";
    /// 表名
    private static final String COLUMN_NAME = "name";
    /// 前缀标识
    private static final String COLUMN_PREFIX_IDEN = "prefixIden";
    /// 后缀标识
    private static final String COLUMN_SUFFIX_IDEN = "suffixIden";
    /// 开始时间
    private static final String COLUMN_BEGIN_TIMESTAMP = "beginTimestamp";
    /// 结束时间
    private static final String COLUMN_END_TIMESTAMP = "endTimestamp";
    /// 创建时间
    private static final String COLUMN_CREATE_TIMESTAMP = "createTimestamp";
    /// 状态
    private static final String COLUMN_STATUS = "status";
    /// 预留
    private static final String COLUMN_RESERVE1 = "reserve1";
    private static final String COLUMN_RESERVE2 = "reserve2";
    private static final String COLUMN_RESERVE3 = "reserve3";
    private static final String COLUMN_RESERVE4 = "reserve4";

    private static final String ALL_COLUMN_ATTR =
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + COLUMN_NAME + " TEXT NOT NULL,"
                    + COLUMN_PREFIX_IDEN + " TEXT NOT NULL,"
                    + COLUMN_SUFFIX_IDEN + " TEXT NOT NULL,"
                    + COLUMN_BEGIN_TIMESTAMP + " TEXT NOT NULL,"
                    + COLUMN_END_TIMESTAMP + " TEXT NOT NULL,"
                    + COLUMN_CREATE_TIMESTAMP + " TEXT NOT NULL,"
                    + COLUMN_STATUS + " INTEGER,"
                    + COLUMN_RESERVE1 + " TEXT,"
                    + COLUMN_RESERVE2 + " TEXT,"
                    + COLUMN_RESERVE3 + " TEXT,"
                    + COLUMN_RESERVE4 + " TEXT";

    private final Object mOperationSyncObject = new Object();

    private SQLiteOpenHelper mSQLiteOpenHelper = null;

    public QueryDbResultTLNameTable(String tag, SQLiteOpenHelper helper) {
        TAG = tag;
        mSQLiteOpenHelper = helper;
        createTable(TABLE_NAME);
    }

    private SQLiteDatabase getWritableDatabase() {
        return  mSQLiteOpenHelper.getWritableDatabase();
    }

    private SQLiteDatabase getReadableDatabase() {
        return mSQLiteOpenHelper.getReadableDatabase();
    }

    private boolean isTableNameValid(String string) {
        return (null != string && string.length() > 0);
    }

    public boolean createTable(String tableName) {
        LogUtil.d(TAG, "start create table:" + tableName);
        if (isTableNameValid(tableName)) {
            synchronized (mOperationSyncObject) {
                try {
                    String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + ALL_COLUMN_ATTR + ")";
                    getWritableDatabase().execSQL(sql);

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, "create table " + tableName + " error:" + e.toString());
                }
            }
        }

        return false;
    }

    private QueryDbResultTLNameItem getDataItem(Cursor cursor) {
        if (null != cursor) {
            QueryDbResultTLNameItem item = new QueryDbResultTLNameItem();

            int columnIndex;
            columnIndex = cursor.getColumnIndex(COLUMN_ID);
            if (columnIndex >= 0) {
                item.setId(cursor.getInt(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_NAME);
            if (columnIndex >= 0) {
                item.setName(cursor.getString(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_PREFIX_IDEN);
            if (columnIndex >= 0) {
                item.setPrefixIden(cursor.getString(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_SUFFIX_IDEN);
            if (columnIndex >= 0) {
                item.setSuffixIden(cursor.getString(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_BEGIN_TIMESTAMP);
            if (columnIndex >= 0) {
                item.setBeginTimestamp(StringsUtils.toLong(cursor.getString(columnIndex)));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_END_TIMESTAMP);
            if (columnIndex >= 0) {
                item.setEndTimestamp(StringsUtils.toLong(cursor.getString(columnIndex)));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_CREATE_TIMESTAMP);
            if (columnIndex >= 0) {
                item.setCreateTimestamp(StringsUtils.toLong(cursor.getString(columnIndex)));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_STATUS);
            if (columnIndex >= 0) {
                item.setStatus(cursor.getInt(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_RESERVE1);
            if (columnIndex >= 0) {
                item.setReserve1(cursor.getString(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_RESERVE2);
            if (columnIndex >= 0) {
                item.setReserve2(cursor.getString(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_RESERVE3);
            if (columnIndex >= 0) {
                item.setReserve3(cursor.getString(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_RESERVE4);
            if (columnIndex >= 0) {
                item.setReserve4(cursor.getString(columnIndex));
            }

            return item;
        }

        return null;
    }

    private ContentValues getContentValues(QueryDbResultTLNameItem item) {
        ContentValues contentValues = new ContentValues();

        if (null != item) {
            contentValues.put(COLUMN_NAME, item.getName());
            contentValues.put(COLUMN_PREFIX_IDEN, item.getPrefixIden());
            contentValues.put(COLUMN_SUFFIX_IDEN, item.getSuffixIden());
            contentValues.put(COLUMN_BEGIN_TIMESTAMP, "" + item.getBeginTimestamp());
            contentValues.put(COLUMN_END_TIMESTAMP, "" + item.getEndTimestamp());
            contentValues.put(COLUMN_CREATE_TIMESTAMP, "" + item.getCreateTimestamp());
            contentValues.put(COLUMN_STATUS, item.getStatus());
            contentValues.put(COLUMN_RESERVE1, item.getReserve1());
            contentValues.put(COLUMN_RESERVE2, item.getReserve2());
            contentValues.put(COLUMN_RESERVE3, item.getReserve3());
            contentValues.put(COLUMN_RESERVE4, item.getReserve4());
        }

        return contentValues;
    }

    public boolean insert(QueryDbResultTLNameItem item) {
        if (null != item) {
            ContentValues contentValues = getContentValues(item);

            synchronized (mOperationSyncObject) {
                try {
                    long row = getWritableDatabase().insert(TABLE_NAME, null, contentValues);
                    item.setId((int)row);
                    LogUtil.d(TAG, "insert(" + TABLE_NAME + ") row = " + row + ", " +  ((-1 == row) ? "fail" : "success"));

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, e.toString());
                }
            }
        }

        return false;
    }

    public boolean delete(QueryDbResultTLNameItem item) {
        if (null != item) {
            synchronized (mOperationSyncObject) {
                int row = getWritableDatabase().delete(TABLE_NAME,
                        COLUMN_ID + "=?",
                        new String[]{"" + item.getId()});

                LogUtil.d(TAG, "delete row = " + row);

                return row >= 0;
            }
        }

        return false;
    }

    public boolean update(QueryDbResultTLNameItem item) {
        if (null != item) {
            ContentValues contentValues = getContentValues(item);
            synchronized (mOperationSyncObject) {
                try {
                    int row = getWritableDatabase().update(TABLE_NAME,
                            contentValues,
                            COLUMN_ID + "=?",
                            new String[]{"" + item.getId()});

                    LogUtil.d(TAG, "update item row = " + row + ", " +  ((row < 0) ? "fail" : "success"));

                    return row >= 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, e.toString());
                }
            }
        }

        return false;
    }

    public QueryDbResultTLNameItem getFirst() {
        QueryDbResultTLNameItem item = null;

        synchronized (mOperationSyncObject) {
            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().query(TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        "1");

                if (cursor.moveToLast()) {
                    item = getDataItem(cursor);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d(TAG, e.toString());
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
            }
        }

        return item;
    }

    public QueryDbResultTLNameItem getLast() {
        QueryDbResultTLNameItem item = null;

        synchronized (mOperationSyncObject) {
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
                    item = getDataItem(cursor);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d(TAG, e.toString());
            } finally {
                if (null  != cursor) {
                    cursor.close();
                }
            }
        }

        return item;
    }

    public QueryDbResultTLNameItem getFirstItemWithPrefixIden(String prefixIden) {
        QueryDbResultTLNameItem item = null;

        if (null != prefixIden) {
            synchronized (mOperationSyncObject) {
                Cursor cursor = null;

                try {
                    cursor = getReadableDatabase().query(TABLE_NAME,
                            null,
                            COLUMN_PREFIX_IDEN + "=?",
                            new String[] {prefixIden},
                            null,
                            null,
                            null,
                            "1");

                    if (cursor.moveToLast()) {
                        item = getDataItem(cursor);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, e.toString());
                } finally {
                    if (null != cursor) {
                        cursor.close();
                    }
                }
            }
        }

        return item;
    }

    public QueryDbResultTLNameItem getFirstItemWithSuffixIden(String suffixIden) {
        QueryDbResultTLNameItem item = null;

        if (null != suffixIden) {
            synchronized (mOperationSyncObject) {
                Cursor cursor = null;

                try {
                    cursor = getReadableDatabase().query(TABLE_NAME,
                            null,
                            COLUMN_SUFFIX_IDEN + "=?",
                            new String[] {suffixIden},
                            null,
                            null,
                            null,
                            "1");

                    if (cursor.moveToLast()) {
                        item = getDataItem(cursor);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, e.toString());
                } finally {
                    if (null != cursor) {
                        cursor.close();
                    }
                }
            }
        }

        return item;
    }

    public QueryDbResultTLNameItem getLastItemWithPrefixIden(String prefixIden) {
        QueryDbResultTLNameItem item = null;

        if (null != prefixIden) {
            synchronized (mOperationSyncObject) {
                Cursor cursor = null;

                try {
                    cursor = getReadableDatabase().query(TABLE_NAME,
                            null,
                            COLUMN_PREFIX_IDEN + "=?",
                            new String[] {prefixIden},
                            null,
                            null,
                            COLUMN_ID + " DESC",
                            "1");

                    if (cursor.moveToLast()) {
                        item = getDataItem(cursor);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, e.toString());
                } finally {
                    if (null != cursor) {
                        cursor.close();
                    }
                }
            }
        }

        return item;
    }

    public QueryDbResultTLNameItem getLastItemWithSuffixIden(String suffixIden) {
        QueryDbResultTLNameItem item = null;

        if (null != suffixIden) {
            synchronized (mOperationSyncObject) {
                Cursor cursor = null;

                try {
                    cursor = getReadableDatabase().query(TABLE_NAME,
                            null,
                            COLUMN_SUFFIX_IDEN + "=?",
                            new String[] {suffixIden},
                            null,
                            null,
                            COLUMN_ID + " DESC",
                            "1");

                    if (cursor.moveToLast()) {
                        item = getDataItem(cursor);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, e.toString());
                } finally {
                    if (null != cursor) {
                        cursor.close();
                    }
                }
            }
        }

        return item;
    }

    public ArrayList<QueryDbResultTLNameItem> getListWithPrefixIden(String prefixIden) {
        ArrayList<QueryDbResultTLNameItem> arrayList = new ArrayList<>();

        if (null != prefixIden) {
            synchronized (mOperationSyncObject) {
                Cursor cursor = null;

                try {
                    cursor = getReadableDatabase().query(TABLE_NAME,
                            null,
                            COLUMN_PREFIX_IDEN + "=?",
                            new String[] {prefixIden},
                            null,
                            null,
                            null);

                    while (cursor.moveToNext()) {
                        QueryDbResultTLNameItem item = getDataItem(cursor);
                        arrayList.add(item);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, e.toString());
                } finally {
                    if (null != cursor) {
                        cursor.close();
                    }
                }
            }
        }

        return arrayList;
    }

    public ArrayList<QueryDbResultTLNameItem> getListWithSuffixIden(String suffixIden) {
        ArrayList<QueryDbResultTLNameItem> arrayList = new ArrayList<>();

        if (null != suffixIden) {
            synchronized (mOperationSyncObject) {
                Cursor cursor = null;

                try {
                    cursor = getReadableDatabase().query(TABLE_NAME,
                            null,
                            COLUMN_SUFFIX_IDEN + "=?",
                            new String[] {suffixIden},
                            null,
                            null,
                            null);

                    while (cursor.moveToNext()) {
                        QueryDbResultTLNameItem item = getDataItem(cursor);

                        arrayList.add(item);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, e.toString());
                } finally {
                    if (null != cursor) {
                        cursor.close();
                    }
                }
            }
        }

        return arrayList;
    }

    public QueryDbResultTLNameItem getItem(String name) {
        QueryDbResultTLNameItem item = null;

        if (null != name) {
            synchronized (mOperationSyncObject) {
                Cursor cursor = null;

                try {
                    cursor = getReadableDatabase().query(TABLE_NAME,
                            null,
                            COLUMN_NAME + "=?",
                            new String[]{name},
                            null,
                            null,
                            null,
                            "1");

                    if (cursor.moveToLast()) {
                        item = getDataItem(cursor);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, e.toString());
                } finally {
                    if (null != cursor) {
                        cursor.close();
                    }
                }
            }
        }

        return item;
    }

    public boolean isExist(String name) {
        boolean exist = false;

        if (null != name) {
            synchronized (mOperationSyncObject) {
                Cursor cursor = null;

                try {
                    cursor = getReadableDatabase().query(TABLE_NAME,
                            null,
                            COLUMN_NAME + "=?",
                            new String[]{name},
                            null,
                            null,
                            null,
                            "1");
                    if (cursor.moveToLast()) {
                        exist = (null != getDataItem(cursor));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, e.toString());
                } finally {
                    if (null != cursor) {
                        cursor.close();
                    }
                }
            }
        }

        return exist;
    }

    public boolean isExist(String name, String prefixIden) {
        boolean exist = false;

        if (null != name && null != prefixIden) {
            synchronized (mOperationSyncObject) {
                Cursor cursor = null;

                try {
                    cursor = getReadableDatabase().query(TABLE_NAME,
                            null,
                            COLUMN_NAME + "=? AND " + COLUMN_PREFIX_IDEN + "=?",
                            new String[] {name, prefixIden},
                            null,
                            null,
                            null,
                            "1");

                    if (cursor.moveToLast()) {
                        exist = (null != getDataItem(cursor));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, e.toString());
                } finally {
                    if (null != cursor) {
                        cursor.close();
                    }
                }
            }
        }

        return exist;
    }

    public boolean isExist(String name, String prefixIden, String suffixIden) {
        boolean exist = false;

        if (null != name && null != prefixIden) {
            synchronized (mOperationSyncObject) {
                Cursor cursor = null;

                try {
                    cursor = getReadableDatabase().query(TABLE_NAME,
                            null,
                            COLUMN_NAME + "=? AND " + COLUMN_PREFIX_IDEN + "=? AND " + COLUMN_SUFFIX_IDEN + "=?",
                            new String[] {name, prefixIden, suffixIden},
                            null,
                            null,
                            null,
                            "1");

                    if (cursor.moveToLast()) {
                        exist = (null != getDataItem(cursor));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, e.toString());
                } finally {
                    if (null != cursor) {
                        cursor.close();
                    }
                }
            }
        }

        return exist;
    }

    public boolean delete(String name) {
        if (null != name) {
            synchronized (mOperationSyncObject) {
                try {
                    int row = getReadableDatabase().delete(TABLE_NAME,
                            COLUMN_NAME + "=?",
                            new String[] {name});
//                    LogUtil.d(TAG, "delete name = " + name);

                    return row >= 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, e.toString());
                }
            }
        }

        return false;
    }

    public ArrayList<String> getPrefixIdenList(boolean isDistinct) {
        ArrayList<String> list = new ArrayList<>();

        synchronized (mOperationSyncObject) {
            Cursor cursor = null;

            try {
                cursor = getWritableDatabase().query(isDistinct,
                        TABLE_NAME,
                        new String[] {COLUMN_NAME},
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);

                while (cursor.moveToNext()) {
                    String temp = cursor.getString(0);

                    if (null != temp) {
                        list.add(temp);
                    }
                }
            } catch (Exception e) {
                LogUtil.d(TAG, e.toString());
                e.printStackTrace();
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
            }
        }

        return list;
    }

    public ArrayList<QueryDbResultTLNameItem> getAllList() {
        ArrayList<QueryDbResultTLNameItem> arrayList = new ArrayList<>();

        synchronized (mOperationSyncObject) {
            Cursor cursor = null;

            try {
                cursor = getReadableDatabase().query(TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);

                while (cursor.moveToNext()) {
                    QueryDbResultTLNameItem item = getDataItem(cursor);

                    arrayList.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d(TAG, e.toString());
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
            }
        }

        return arrayList;
    }

    public ArrayList<QueryDbResultTLNameItem> getListWithBeginTimestamp(long beginTimestamp, long endTimestamp) {
        ArrayList<QueryDbResultTLNameItem> arrayList = new ArrayList<>();

        synchronized (mOperationSyncObject) {
            Cursor cursor = null;

            try {
                cursor = getReadableDatabase().query(TABLE_NAME,
                        null,
                        COLUMN_BEGIN_TIMESTAMP + ">=? AND " + COLUMN_END_TIMESTAMP + "<=?",
                        new String[] {"" + beginTimestamp, "" + endTimestamp},
                        null,
                        null,
                        null);

                while (cursor.moveToNext()) {
                    QueryDbResultTLNameItem item = getDataItem(cursor);

                    arrayList.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d(TAG, e.toString());
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
            }
        }

        return arrayList;
    }
}
