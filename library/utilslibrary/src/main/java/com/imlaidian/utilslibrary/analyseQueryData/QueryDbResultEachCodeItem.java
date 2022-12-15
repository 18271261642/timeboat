package com.imlaidian.utilslibrary.analyseQueryData;

/**
 * Created by jun on 2017/8/24.
 * query data result each code item
 */

public class QueryDbResultEachCodeItem {
    //异常代码 的起始时间 和结束时间
    private int mCode;
    private QueryDbResultEachDataItem mBeginItem =null;
    private QueryDbResultEachDataItem mEndItem =null;

    private int totalCount ;

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public QueryDbResultEachDataItem getBeginItem() {
        return mBeginItem;
    }

    public void setBeginItem(QueryDbResultEachDataItem beginItem) {
        this.mBeginItem = beginItem;
    }

    public QueryDbResultEachDataItem getEndItem() {
        return mEndItem;
    }

    public void setEndItem(QueryDbResultEachDataItem endItem) {
        mEndItem = endItem;
    }


    public QueryDbResultEachDataItem getmBeginItem() {
        return mBeginItem;
    }

    public void setmBeginItem(QueryDbResultEachDataItem mBeginItem) {
        this.mBeginItem = mBeginItem;
    }

    public long getTimestamp(long finishTime) {
        if (null != getBeginItem()) {
            long beginTimestamp = getBeginItem().getDataTimestamp();
            long endTimestamp = 0;
            if (null == getEndItem()) {
                endTimestamp = finishTime;
            } else {
                endTimestamp = getEndItem().getDataTimestamp();
            }

            return Math.abs(endTimestamp - beginTimestamp);
        }

        return 0;
    }
}
