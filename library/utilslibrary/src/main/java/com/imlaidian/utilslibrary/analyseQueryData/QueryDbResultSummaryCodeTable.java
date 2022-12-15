package com.imlaidian.utilslibrary.analyseQueryData;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.imlaidian.utilslibrary.utils.LogUtil;
import com.imlaidian.utilslibrary.utils.StringsUtils;


import java.util.ArrayList;


/**
 * Created by jun on 2017/8/25.
 * code table
 */

public class QueryDbResultSummaryCodeTable {
    private String TAG = "";

    /// 表名
    private static final String TABLE_NAME = "SummaryCodeList";

    /// 以下是各个字段
    private static final String COLUMN_ID = "id";
    /// 对应SummaryTable id
    private static final String COLUMN_SUMMARY_ID = "summaryId";
    /// 对应code
    private static final String COLUMN_CODE = "code";
    /// 开始ID
    private static final String COLUMN_BEGIN_ID = "beginId";
    /// 开始数据时间
    private static final String COLUMN_BEGIN_DATA_TIMESTAMP = "beginDataTimestamp";
    /// 结束ID
    private static final String COLUMN_END_ID = "endId";
    /// 结束数据时间
    private static final String COLUMN_END_DATA_TIMESTAMP = "endDataTimestamp";
    /// 预留
    private static final String COLUMN_RESERVE1 = "reserve1";
    private static final String COLUMN_RESERVE2 = "reserve2";
    private static final String COLUMN_RESERVE3 = "reserve3";
    private static final String COLUMN_RESERVE4 = "reserve4";

    private static final String ALL_COLUMN_ATTR =
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + COLUMN_SUMMARY_ID + " TEXT,"
                    + COLUMN_CODE + " INTEGER,"
                    + COLUMN_BEGIN_ID + " INTEGER,"
                    + COLUMN_BEGIN_DATA_TIMESTAMP + " TEXT,"
                    + COLUMN_END_ID + " INTEGER,"
                    + COLUMN_END_DATA_TIMESTAMP + " TEXT,"
                    + COLUMN_RESERVE1 + " TEXT,"
                    + COLUMN_RESERVE2 + " TEXT,"
                    + COLUMN_RESERVE3 + " TEXT,"
                    + COLUMN_RESERVE4 + " TEXT";

    private final Object mOperationSyncObject = new Object();

    private SQLiteOpenHelper mSQLiteOpenHelper = null;

    public QueryDbResultSummaryCodeTable(String tag, SQLiteOpenHelper helper) {
        TAG = tag;
        mSQLiteOpenHelper = helper;

        createTable();
    }

    private SQLiteDatabase getWritableDatabase() {
        return  mSQLiteOpenHelper.getWritableDatabase();
    }

    private SQLiteDatabase getReadableDatabase() {
        return mSQLiteOpenHelper.getReadableDatabase();
    }

    public boolean createTable() {
        synchronized (mOperationSyncObject) {
            try {
                String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + ALL_COLUMN_ATTR + ")";
                getWritableDatabase().execSQL(sql);

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d(TAG, e.toString());
            }
        }

        return false;
    }

    private QueryDbResultSummaryCodeItem getDataItem(Cursor cursor) {
        if (null != cursor) {
            QueryDbResultSummaryCodeItem item = new QueryDbResultSummaryCodeItem();

            int columnIndex;
            columnIndex = cursor.getColumnIndex(COLUMN_ID);
            if (columnIndex >= 0) {
                item.setId(cursor.getInt(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_SUMMARY_ID);
            if (columnIndex >= 0) {
                item.setSummaryId(cursor.getInt(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_CODE);
            if (columnIndex >= 0) {
                item.setCode(cursor.getInt(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_BEGIN_ID);
            if (columnIndex >= 0) {
                item.setBeginId(cursor.getInt(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_BEGIN_DATA_TIMESTAMP);
            if (columnIndex >= 0) {
                item.setBeginDataTimestamp(StringsUtils.toLong(cursor.getString(columnIndex)));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_END_ID);
            if (columnIndex >= 0) {
                item.setEndId(cursor.getInt(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_END_DATA_TIMESTAMP);
            if (columnIndex >= 0) {
                item.setEndDataTimestamp(StringsUtils.toLong(cursor.getString(columnIndex)));
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

    private ContentValues getContentValues(QueryDbResultSummaryCodeItem item) {
        ContentValues contentValues = new ContentValues();

        if (null != item) {
            contentValues.put(COLUMN_SUMMARY_ID, item.getSummaryId());
            contentValues.put(COLUMN_CODE, item.getCode());
            contentValues.put(COLUMN_BEGIN_ID, item.getBeginId());
            contentValues.put(COLUMN_BEGIN_DATA_TIMESTAMP, item.getBeginDataTimestamp());
            contentValues.put(COLUMN_END_ID, item.getEndId());
            contentValues.put(COLUMN_END_DATA_TIMESTAMP, item.getEndDataTimestamp());
            contentValues.put(COLUMN_RESERVE1, item.getReserve1());
            contentValues.put(COLUMN_RESERVE2, item.getReserve2());
            contentValues.put(COLUMN_RESERVE3, item.getReserve3());
            contentValues.put(COLUMN_RESERVE4, item.getReserve4());
        }

        return contentValues;
    }

    public boolean insert(QueryDbResultSummaryCodeItem item) {
        if (null != item) {
            ContentValues contentValues = getContentValues(item);

            synchronized (mOperationSyncObject) {
                try {
                    long row = getWritableDatabase().insert(TABLE_NAME,
                            null,
                            contentValues);
                    item.setId((int)row);
//                    LogUtil.d(TAG, "insert(" + TABLE_NAME + ") row = " + row + ", " +  ((-1 == row) ? "fail" : "success"));

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, e.toString());
                }
            }
        }

        return false;
    }

    public boolean delete(QueryDbResultSummaryCodeItem item) {
        if (null != item) {
            synchronized (mOperationSyncObject) {
                int row = getWritableDatabase().delete(TABLE_NAME,
                        COLUMN_ID + "=?",
                        new String[]{"" + item.getId()});

//                LogUtil.d(TAG, "delete id(" + item.getId() + ") row = " + row);

                return row >= 0;
            }
        }

        return false;
    }

    public boolean delete(int summaryId) {
        synchronized (mOperationSyncObject) {
            int row = getWritableDatabase().delete(TABLE_NAME,
                    COLUMN_SUMMARY_ID + "=?",
                    new String[]{ "" + summaryId });

//            LogUtil.d(TAG, "delete summaryId(" + summaryId + ") row = " + row);

            return row >= 0;
        }
    }

    public boolean update(QueryDbResultSummaryCodeItem item) {
        if (null != item) {
            ContentValues contentValues = getContentValues(item);
            synchronized (mOperationSyncObject) {
                try {
                    int row = getWritableDatabase().update(TABLE_NAME, contentValues,
                            COLUMN_ID + " =? ", new String[]{"" + item.getId()});
//                    LogUtil.d(TAG, "update item row = " + row + ", " +  ((row < 0) ? "fail" : "success"));

                    return row >= 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, e.toString());
                }
            }
        }

        return false;
    }

    public QueryDbResultSummaryCodeItem getFirst() {
        QueryDbResultSummaryCodeItem item = null;

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

    public QueryDbResultSummaryCodeItem getLast() {
        QueryDbResultSummaryCodeItem item = null;

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

    public ArrayList<QueryDbResultSummaryCodeItem> getList(int summaryId) {
        ArrayList<QueryDbResultSummaryCodeItem> arrayList = new ArrayList<>();

        synchronized (mOperationSyncObject) {
            Cursor cursor = null;

            try {
                cursor = getReadableDatabase().query(TABLE_NAME,
                        null,
                        COLUMN_SUMMARY_ID + " =? ",
                        new String[] { "" + summaryId },
                        null,
                        null,
                        null);

                while (cursor.moveToNext()) {
                    QueryDbResultSummaryCodeItem item = getDataItem(cursor);
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
