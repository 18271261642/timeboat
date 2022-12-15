package com.imlaidian.utilslibrary.analyseQueryData;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.imlaidian.utilslibrary.utils.LogUtil;

/**
 * Created by jun on 2017/8/25.
 * query data result summary table
 */

public class QueryDbResultSummaryTable {
    private String TAG = "";

    /// 表名
    private static final String TABLE_NAME = "SummaryList";

    /// 以下是各个字段
    private static final String COLUMN_ID = "id";
    /// 名称
    private static final String COLUMN_NAME = "name";
    /// 前缀
    private static final String COLUMN_PREFIXIDEN = "prefixIden";
    /// 后缀
    private static final String COLUMN_SUFFIXIDEN = "suffixIden";
    /// code数量
    private static final String COLUMN_CODE_COUNT = "codeCount";
    /// 内容
    private static final String COLUMN_CONTENT = "content";
    /// 预留
    private static final String COLUMN_RESERVE1 = "reserve1";
    private static final String COLUMN_RESERVE2 = "reserve2";
    private static final String COLUMN_RESERVE3 = "reserve3";
    private static final String COLUMN_RESERVE4 = "reserve4";

    private static final String ALL_COLUMN_ATTR =
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_PREFIXIDEN + " TEXT,"
                    + COLUMN_SUFFIXIDEN + " TEXT,"
                    + COLUMN_CODE_COUNT + " TEXT,"
                    + COLUMN_CONTENT + " TEXT,"
                    + COLUMN_RESERVE1 + " TEXT,"
                    + COLUMN_RESERVE2 + " TEXT,"
                    + COLUMN_RESERVE3 + " TEXT,"
                    + COLUMN_RESERVE4 + " TEXT";

    private final Object mOperationSyncObject = new Object();

    private SQLiteOpenHelper mSQLiteOpenHelper = null;

    public QueryDbResultSummaryTable(String tag, SQLiteOpenHelper helper) {
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

    private QueryDbResultSummaryItem getDataItem(Cursor cursor) {
        if (null != cursor) {
            QueryDbResultSummaryItem item = new QueryDbResultSummaryItem();

            int columnIndex;
            columnIndex = cursor.getColumnIndex(COLUMN_ID);
            if (columnIndex >= 0) {
                item.setId(cursor.getInt(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_NAME);
            if (columnIndex >= 0) {
                item.setName(cursor.getString(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_PREFIXIDEN);
            if (columnIndex >= 0) {
                item.setPrefixIden(cursor.getString(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_SUFFIXIDEN);
            if (columnIndex >= 0) {
                item.setSuffixIden(cursor.getString(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_CODE_COUNT);
            if (columnIndex >= 0) {
                item.setCodeCount(cursor.getInt(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_CONTENT);
            if (columnIndex >= 0) {
                item.setContent(cursor.getString(columnIndex));
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

    private ContentValues getContentValues(QueryDbResultSummaryItem item) {
        ContentValues contentValues = new ContentValues();

        if (null != item) {
            contentValues.put(COLUMN_NAME, item.getName());
            contentValues.put(COLUMN_PREFIXIDEN, item.getPrefixIden());
            contentValues.put(COLUMN_SUFFIXIDEN, item.getSuffixIden());
            contentValues.put(COLUMN_CODE_COUNT, item.getCodeCount());
            contentValues.put(COLUMN_CONTENT, item.getContent());
            contentValues.put(COLUMN_RESERVE1, item.getReserve1());
            contentValues.put(COLUMN_RESERVE2, item.getReserve2());
            contentValues.put(COLUMN_RESERVE3, item.getReserve3());
            contentValues.put(COLUMN_RESERVE4, item.getReserve4());
        }

        return contentValues;
    }

    public boolean insert(QueryDbResultSummaryItem item) {
        if (null != item) {
            ContentValues contentValues = getContentValues(item);

            synchronized (mOperationSyncObject) {
                try {
                    long row = getWritableDatabase().insert(TABLE_NAME, null, contentValues);
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

    public boolean delete(QueryDbResultSummaryItem item) {
        if (null != item) {
            synchronized (mOperationSyncObject) {
                int row = getWritableDatabase().delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{"" + item.getId()});

//                LogUtil.d(TAG, "delete row = " + row);

                return row >= 0;
            }
        }

        return false;
    }

    public boolean update(QueryDbResultSummaryItem item) {
        if (null != item) {
            ContentValues contentValues = getContentValues(item);
            synchronized (mOperationSyncObject) {
                try {
                    int row = getWritableDatabase().update(TABLE_NAME, contentValues,
                            COLUMN_ID + "=?", new String[]{"" + item.getId()});
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

    public QueryDbResultSummaryItem getFirst() {
        QueryDbResultSummaryItem item = null;

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

    public QueryDbResultSummaryItem getLast() {
        QueryDbResultSummaryItem item = null;

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

    public QueryDbResultSummaryItem queryWithName(String name) {
        QueryDbResultSummaryItem item = null;

        synchronized (mOperationSyncObject) {
            Cursor cursor = null;

            try {
                cursor = getReadableDatabase().query(TABLE_NAME,
                        null,
                        COLUMN_NAME + "=?",
                        new String[] { name },
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
                if (null  != cursor) {
                    cursor.close();
                }
            }
        }

        return item;
    }
}
