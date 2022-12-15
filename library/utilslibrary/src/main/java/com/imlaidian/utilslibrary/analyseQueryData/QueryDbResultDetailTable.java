package com.imlaidian.utilslibrary.analyseQueryData;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.imlaidian.utilslibrary.utils.LogUtil;
import com.imlaidian.utilslibrary.utils.StringsUtils;



import java.util.ArrayList;



/**
 * Created by jun on 2017/8/14.
 * query data per day result table
 */

public class QueryDbResultDetailTable {
    private String TAG = "";
    private SQLiteOpenHelper mSQLiteOpenHelper = null;

    /// 以下是各个字段
    private static final String COLUMN_ID = "id";
    /// 分析结果时间
    private static final String COLUMN_ANALYSE_TIMESTAMP = "analyseTimestamp";
    /// 错误代码
    private static final String COLUMN_CODE = "code";
    /// 对应详细列表的ID
    private static final String COLUMN_SOURCE_ID = "sourceId";
    /// 当前数据的时间
    private static final String COLUMN_DATA_TIMESTAMP = "dataTimestamp";
    /// 以下是预留字段
    private static final String COLUMN_RESERVE1 = "reserve1";
    private static final String COLUMN_RESERVE2 = "reserve2";
    private static final String COLUMN_RESERVE3 = "reserve3";
    private static final String COLUMN_RESERVE4 = "reserve4";
    private static final String COLUMN_RESERVE5 = "reserve5";
    private static final String COLUMN_RESERVE6 = "reserve6";
    private static final String COLUMN_RESERVE7 = "reserve7";
    private static final String COLUMN_RESERVE8 = "reserve8";
    private static final String ALL_COLUMN_ATTR =
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + COLUMN_ANALYSE_TIMESTAMP + " TEXT,"
                    + COLUMN_CODE + " INTEGER,"
                    + COLUMN_SOURCE_ID + " INTEGER,"
                    + COLUMN_DATA_TIMESTAMP + " TEXT,"
                    + COLUMN_RESERVE1 + " TEXT,"
                    + COLUMN_RESERVE2 + " TEXT,"
                    + COLUMN_RESERVE3 + " TEXT,"
                    + COLUMN_RESERVE4 + " TEXT,"
                    + COLUMN_RESERVE5 + " TEXT,"
                    + COLUMN_RESERVE6 + " TEXT,"
                    + COLUMN_RESERVE7 + " TEXT,"
                    + COLUMN_RESERVE8 + " TEXT";

    private final Object mOperationSyncObject = new Object();

    public QueryDbResultDetailTable(String tag, SQLiteOpenHelper helper) {
        TAG = tag;
        mSQLiteOpenHelper = helper;
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
        if (isTableNameValid(tableName)) {
            synchronized (mOperationSyncObject) {
                try {
                    String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + ALL_COLUMN_ATTR + ")";
                    getWritableDatabase().execSQL(sql);

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, e.toString());
                }
            }
        }

        return false;
    }

    public boolean dropTable(String tableName) {
        if (isTableNameValid(tableName)) {
            synchronized (mOperationSyncObject) {
                try {
                    String sql = "DROP TABLE " + tableName;
                    getWritableDatabase().execSQL(sql);

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, e.toString());
                }
            }
        }

        return false;
    }

    private QueryDbResultDetailItem getDataItem(Cursor cursor) {
        if (null != cursor) {
            QueryDbResultDetailItem item = new QueryDbResultDetailItem();

            int columnIndex = 0;
            columnIndex = cursor.getColumnIndex(COLUMN_ID);
            if (columnIndex >= 0) {
                item.setId(cursor.getInt(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_ANALYSE_TIMESTAMP);
            if (columnIndex >= 0) {
                item.setAnalyseTimestamp(StringsUtils.toLong(cursor.getString(columnIndex)));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_CODE);
            if (columnIndex >= 0) {
                item.setCode(cursor.getInt(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_SOURCE_ID);
            if (columnIndex >= 0) {
                item.setSourceId(cursor.getLong(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_DATA_TIMESTAMP);
            if (columnIndex >= 0) {
                item.setDataTimestamp(StringsUtils.toLong(cursor.getString(columnIndex)));
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

            columnIndex = cursor.getColumnIndex(COLUMN_RESERVE5);
            if (columnIndex >= 0) {
                item.setReserve5(cursor.getString(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_RESERVE6);
            if (columnIndex >= 0) {
                item.setReserve6(cursor.getString(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_RESERVE7);
            if (columnIndex >= 0) {
                item.setReserve7(cursor.getString(columnIndex));
            }

            columnIndex = cursor.getColumnIndex(COLUMN_RESERVE8);
            if (columnIndex >= 0) {
                item.setReserve8(cursor.getString(columnIndex));
            }

            return item;
        }

        return null;
    }

    private ContentValues getContentValues(QueryDbResultDetailItem item) {
        ContentValues contentValues = new ContentValues();

        if (null != item) {
            contentValues.put(COLUMN_ANALYSE_TIMESTAMP, "" + item.getAnalyseTimestamp());
            contentValues.put(COLUMN_CODE, item.getCode());
            contentValues.put(COLUMN_SOURCE_ID, item.getSourceId());
            contentValues.put(COLUMN_DATA_TIMESTAMP, item.getDataTimestamp());
            contentValues.put(COLUMN_RESERVE1, item.getReserve1());
            contentValues.put(COLUMN_RESERVE2, item.getReserve2());
            contentValues.put(COLUMN_RESERVE3, item.getReserve3());
            contentValues.put(COLUMN_RESERVE4, item.getReserve4());
            contentValues.put(COLUMN_RESERVE5, item.getReserve5());
            contentValues.put(COLUMN_RESERVE6, item.getReserve6());
            contentValues.put(COLUMN_RESERVE7, item.getReserve7());
            contentValues.put(COLUMN_RESERVE8, item.getReserve8());
        }

        return contentValues;
    }

    public void insertItem(String tableName, QueryDbResultDetailItem item) {
        if (isTableNameValid(tableName) && null != item) {
            ContentValues contentValues = getContentValues(item);

            synchronized (mOperationSyncObject) {
                try {
                    long row = getWritableDatabase().insert(tableName, null, contentValues);
                    item.setId((int)row);
//                    LogUtil.d(TAG, "insert(" + tableName + ") row = " + row + ", " +  ((-1 == row) ? "fail" : "success"));
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, e.toString());
                }
            }
        }
    }

    public QueryDbResultDetailItem getFirstItem(String tableName) {
        QueryDbResultDetailItem item = null;

        if (isTableNameValid(tableName)) {
            synchronized (mOperationSyncObject) {
                Cursor cursor = null;

                try {
                    cursor = getReadableDatabase().query(tableName,
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
        }

        return item;
    }

    public QueryDbResultDetailItem getLastItem(String tableName) {
        QueryDbResultDetailItem item = null;

        if (isTableNameValid(tableName)) {
            synchronized (mOperationSyncObject) {
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

    public ArrayList<QueryDbResultDetailItem> getAllList(String tableName) {
        ArrayList<QueryDbResultDetailItem> arrayList = new ArrayList<>();

        if (isTableNameValid(tableName)) {
            synchronized (mOperationSyncObject) {
                Cursor cursor = null;

                try {
                    cursor = getReadableDatabase().query(tableName,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);

                    while (cursor.moveToNext()) {
                        QueryDbResultDetailItem item = getDataItem(cursor);

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

    public void updateItem(String tableName, QueryDbResultDetailItem item) {
        if (isTableNameValid(tableName) && null != item) {
            ContentValues contentValues = getContentValues(item);

            synchronized (mOperationSyncObject) {
                try {
                    int row = getWritableDatabase().update(tableName,
                            contentValues,
                            COLUMN_ID + "=?",
                            new String[]{"" + item.getId()});

//                    LogUtil.d(TAG, "update(" + tableName + ") row = " + row + ", " +  ((row < 0) ? "fail" : "success"));
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, e.toString());
                }
            }
        }
    }

    public void deleteItem(String tableName, QueryDbResultDetailItem item) {
        if (isTableNameValid(tableName) && null != item) {
            synchronized (mOperationSyncObject) {
                try {
                    int row = getWritableDatabase().delete(tableName,
                            COLUMN_ID + "=?",
                            new String[]{"" + item.getId()});

//                    LogUtil.d(TAG, "delete(" + tableName +") row = " + row + ", " +  ((row < 0) ? "fail" : "success"));
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, e.toString());
                }
            }
        }
    }

    public ArrayList<QueryDbResultDetailItem> getList(String tableName, long beginTimestamp, long endTimestamp) {
        ArrayList<QueryDbResultDetailItem> arrayList = new ArrayList<>();

        synchronized (mOperationSyncObject) {
            Cursor cursor = null;

            try {
                cursor = getReadableDatabase().query(tableName,
                        null,
                        COLUMN_DATA_TIMESTAMP + ">=? AND " + COLUMN_DATA_TIMESTAMP + "<?",
                        new String[]{"" + beginTimestamp, "" + endTimestamp},
                        null,
                        null,
                        null);

                while (cursor.moveToNext()) {
                    QueryDbResultDetailItem item = getDataItem(cursor);

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

    public ArrayList<Integer> getDistinctSourceIdList(String tableName) {
        ArrayList<Integer> arrayList = new ArrayList<>();

        if (isTableNameValid(tableName)) {
            synchronized (mOperationSyncObject) {
                Cursor cursor = null;

                try {
                    cursor = getReadableDatabase().query(true,
                            tableName,
                            new String[] {COLUMN_SOURCE_ID},
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);

                    while (cursor.moveToNext()) {
                        arrayList.add(cursor.getInt(0));
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

    public ArrayList<QueryDbResultDetailItem> getListWithSourceId(String tableName, int sourceId) {
        ArrayList<QueryDbResultDetailItem> arrayList = new ArrayList<>();

        if (isTableNameValid(tableName)) {
            synchronized (mOperationSyncObject) {
                Cursor cursor = null;

                try {
                    cursor = getReadableDatabase().query(tableName,
                            null,
                            COLUMN_SOURCE_ID + "=?",
                            new String[]{"" + sourceId},
                            null,
                            null,
                            null);

                    while (cursor.moveToNext()) {
                        QueryDbResultDetailItem item = getDataItem(cursor);

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

    public ArrayList<QueryDbResultDetailItem> getListWithCode(String tableName, int code) {
        ArrayList<QueryDbResultDetailItem> arrayList = new ArrayList<>();

        if (isTableNameValid(tableName)) {
            synchronized (mOperationSyncObject) {
                Cursor cursor = null;

                try {
                    cursor = getReadableDatabase().query(tableName,
                            null,
                            COLUMN_CODE + "=?",
                            new String[]{"" + code},
                            null,
                            null,
                            null);

                    while (cursor.moveToNext()) {
                        QueryDbResultDetailItem item = getDataItem(cursor);

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
}
