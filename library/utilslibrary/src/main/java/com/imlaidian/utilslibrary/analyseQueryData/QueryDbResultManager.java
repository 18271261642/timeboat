package com.imlaidian.utilslibrary.analyseQueryData;


import android.database.sqlite.SQLiteOpenHelper;

import com.imlaidian.utilslibrary.utils.LogUtil;
import com.imlaidian.utilslibrary.utils.SystemTool;



import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jun on 2017/8/10.
 * query data result table manager
 */

public class QueryDbResultManager {
    private String TAG = "";

    private QueryDbResultDetailTable mResultTable = null;
    private QueryDbResultTLNameTable mTLNameTable = null;
    private QueryDbResultSummaryTable mSummaryTable = null;
    private QueryDbResultSummaryCodeTable mSummaryCodeTable = null;
    private int RECORD_DAY_MAX = 20;

    public QueryDbResultManager(String tag, SQLiteOpenHelper helper) {
        TAG = tag + "Manager";
        mResultTable = new QueryDbResultDetailTable(tag + "DetailTable", helper);
        mTLNameTable = new QueryDbResultTLNameTable(tag + "TLNameTable", helper);
        mSummaryTable = new QueryDbResultSummaryTable(tag + "SummaryTable", helper);
        mSummaryCodeTable = new QueryDbResultSummaryCodeTable(tag + "SummaryCodeTable", helper);
        LogUtil.d(TAG, "QueryDataResultManager ");
        cleanTableListInvalidTask();
    }

    public void createTable(QueryDbResultTLNameItem tlNameItem) {
        LogUtil.d(TAG, "create table ");
        if (null != tlNameItem) {
            if (mResultTable.createTable(tlNameItem.getName())) {
                if (!mTLNameTable.isExist(tlNameItem.getName())) {
                    mTLNameTable.insert(tlNameItem);
                    LogUtil.d(TAG, "create table success");
                } else {
                    LogUtil.d(TAG, "create table TLName fail");
                }
            } else {
                LogUtil.d(TAG, "create table fail");
            }
        }
    }

    public void updateTable(QueryDbResultTLNameItem tlNameItem) {
        if (null != tlNameItem) {
            mTLNameTable.update(tlNameItem);
        }
    }

    public void dropTable(String name) {
        boolean success = false;
        if (mResultTable.dropTable(name)) {
            if (mTLNameTable.delete(name)) {
                success = true;
            }
        }

        LogUtil.d(TAG, "drop table(" + name + ") " + (success ? "success" : "fail"));
    }

    private void cleanTableListInvalidTask() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                cleanInvalidRecord();
            }
        }, 500, SystemTool.hourToMillisecond(8));
    }

    private void cleanInvalidRecord() {
        ArrayList<String> nameList = mTLNameTable.getPrefixIdenList(true);
        if (null != nameList && nameList.size() > 0) {
            for (String name : nameList) {
                ArrayList<QueryDbResultTLNameItem> items = mTLNameTable.getListWithPrefixIden(name);
                if (items.size() > RECORD_DAY_MAX) {
                    for (int i=items.size() - RECORD_DAY_MAX; i<items.size(); i++) {
                        QueryDbResultTLNameItem item = items.get(i);
                        dropTable(item.getName());
                    }
                }
            }
        }
    }


    public void setRECORD_DAY_MAX(int RECORD_DAY_MAX) {
        this.RECORD_DAY_MAX = RECORD_DAY_MAX;
    }

    public boolean isExist(String name) {
        return mTLNameTable.isExist(name);
    }

    /// === TLName table
    public QueryDbResultTLNameItem getTLNameItem(String name) {
        return mTLNameTable.getItem(name);
    }

    public ArrayList<QueryDbResultTLNameItem> getAllTLNameItems() {
        return mTLNameTable.getAllList();
    }

    public QueryDbResultTLNameItem getTLNameFirstItemWithPrefixIden(String prefixIden) {
        return mTLNameTable.getFirstItemWithPrefixIden(prefixIden);
    }

    public QueryDbResultTLNameItem getTLNameLastItemWithPrefixIden(String prefixIden) {
        return mTLNameTable.getLastItemWithPrefixIden(prefixIden);
    }

    public ArrayList<QueryDbResultTLNameItem> getTLNameListWithPrefixIden(String prefixIden) {
        return  mTLNameTable.getListWithPrefixIden(prefixIden);
    }

    public QueryDbResultTLNameItem getTLNameFirstItemWithSuffixIden(String suffixIden) {
        return mTLNameTable.getFirstItemWithSuffixIden(suffixIden);
    }

    public QueryDbResultTLNameItem getTLNameLastItemWithSuffixIden(String suffixIden) {
        return mTLNameTable.getLastItemWithSuffixIden(suffixIden);
    }

    public ArrayList<QueryDbResultTLNameItem> getTLNameListWithSuffixIden(String suffix) {
        return  mTLNameTable.getListWithSuffixIden(suffix);
    }

    /// === Detail table
    public void insert(QueryDbResultTLNameItem tlNameItem, QueryDbResultDetailItem resultDetailItem) {
        if (null != tlNameItem && null != resultDetailItem) {
            String tableName = tlNameItem.getName();
            if (null != tableName) {
                mResultTable.insertItem(tableName, resultDetailItem);
            }
        }
    }

    public ArrayList<QueryDbResultDetailItem> getResultList(QueryDbResultTLNameItem tlNameItem) {
        if (null != tlNameItem) {
            return mResultTable.getAllList(tlNameItem.getName());
        }

        return null;
    }

    public QueryDbResultDetailItem getResultFirstItem(QueryDbResultTLNameItem tlNameItem) {
        if (null != tlNameItem) {
            return mResultTable.getFirstItem(tlNameItem.getName());
        }

        return null;
    }

    public QueryDbResultDetailItem getResultLastItem(QueryDbResultTLNameItem tlNameItem) {
        if (null != tlNameItem) {
            return mResultTable.getLastItem(tlNameItem.getName());
        }

        return null;
    }

    public void updateResultItem(QueryDbResultTLNameItem tlNameItem, QueryDbResultDetailItem detailItem) {
        if (null != tlNameItem && null != detailItem) {
            mResultTable.updateItem(tlNameItem.getName(), detailItem);
        }
    }

    public void deleteResultItem(QueryDbResultTLNameItem tlNameItem, QueryDbResultDetailItem detailItem) {
        if (null != tlNameItem && null != detailItem) {
            mResultTable.deleteItem(tlNameItem.getName(), detailItem);
        }
    }

    public ArrayList<QueryDbResultDetailItem> getResultListWithCode(QueryDbResultTLNameItem tlNameItem, int code) {
        if (null != tlNameItem) {
            return mResultTable.getListWithCode(tlNameItem.getName(), code);
        }

        return null;
    }

    public ArrayList<Integer> getDistinctSourceIdList(QueryDbResultTLNameItem tlNameItem) {
        if (null != tlNameItem) {
            return mResultTable.getDistinctSourceIdList(tlNameItem.getName());
        }

        return null;
    }

    public ArrayList<QueryDbResultDetailItem> getResultListWithSourceId(QueryDbResultTLNameItem tlNameItem, int sourceId) {
        if (null != tlNameItem) {
            return mResultTable.getListWithSourceId(tlNameItem.getName(), sourceId);
        }

        return null;
    }

    /// 根据sourceId获取每条记录的错误代码
    public ArrayList<QueryDbResultEachDataItem> getResultAllEachDataListWithSourceId(QueryDbResultTLNameItem tlNameItem) {
        try {
            if (null != tlNameItem) {
                ArrayList<Integer> idList = getDistinctSourceIdList(tlNameItem);
                if (null != idList) {
                    ArrayList<QueryDbResultEachDataItem> arrayList = new ArrayList<>();
                    for (Integer sourceId : idList) {
                        QueryDbResultEachDataItem dataItem = new QueryDbResultEachDataItem();
                        dataItem.setSourceId(sourceId);
                        ArrayList<QueryDbResultDetailItem> detailItems = getResultListWithSourceId(tlNameItem, sourceId);
                        if (null != detailItems) {
                            if (detailItems.size() > 0) {
                                QueryDbResultDetailItem firstItem = detailItems.get(0);
                                dataItem.setDataTimestamp(firstItem.getDataTimestamp());
                                dataItem.setAnalyseTimestamp(firstItem.getAnalyseTimestamp());
                            }
                        }

                        dataItem.setCodeList(detailItems);

                        arrayList.add(dataItem);
                    }

                    return arrayList;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    /// === summary table
    public boolean insertSummaryItem(QueryDbResultSummaryItem item) {
        return mSummaryTable.insert(item);
    }

    public boolean deleteSummaryItem(QueryDbResultSummaryItem item) {
        return mSummaryTable.delete(item);
    }

    public boolean updateSummaryItem(QueryDbResultSummaryItem item) {
        return mSummaryTable.update(item);
    }

    public QueryDbResultSummaryItem getFirstSummaryItem() {
        return mSummaryTable.getFirst();
    }

    public QueryDbResultSummaryItem getLastSummaryItem() {
        return mSummaryTable.getLast();
    }

    public QueryDbResultSummaryItem querySummaryItemWithName(String name) {
        return mSummaryTable.queryWithName(name);
    }

    /// === summary code table
    public boolean insertSummaryCodeItem(QueryDbResultSummaryCodeItem item) {
        return mSummaryCodeTable.insert(item);
    }

    public boolean deleteSummaryCodeItem(QueryDbResultSummaryCodeItem item) {
        return mSummaryCodeTable.delete(item);
    }

    public boolean deleteSummaryCodeItem(int summaryId) {
        return mSummaryCodeTable.delete(summaryId);
    }

    public boolean updateSummaryCodeItem(QueryDbResultSummaryCodeItem item) {
        return mSummaryCodeTable.update(item);
    }

    public QueryDbResultSummaryCodeItem getFirstSummaryCodeItem() {
        return mSummaryCodeTable.getFirst();
    }

    public QueryDbResultSummaryCodeItem getLastSummaryCodeItem() {
        return mSummaryCodeTable.getLast();
    }

    public ArrayList<QueryDbResultSummaryCodeItem> getSummaryCodeList(int summaryId) {
        return mSummaryCodeTable.getList(summaryId);
    }
}
