package com.imlaidian.utilslibrary.analyseQueryData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jun on 2017/8/23.
 * each record item
 */

public class QueryDbResultEachDataItem implements Serializable{
    private static final long serialVersionUID = 3317615290197559757L;

    /// 分析结果的时间
    private long mAnalyseTimestamp;
    /// 原始表里面的ID
    private long mSourceId;
    /// 数据的时间
    private long mDataTimestamp;
    /// 错误列表
    private ArrayList<QueryDbResultDetailItem> mCodeList = new ArrayList<>();

    public long getAnalyseTimestamp() {
        return mAnalyseTimestamp;
    }

    public void setAnalyseTimestamp(long analyseTimestamp) {
        mAnalyseTimestamp = analyseTimestamp;
    }

    public long getSourceId() {
        return mSourceId;
    }

    public void setSourceId(long sourceId) {
        mSourceId = sourceId;
    }

    public long getDataTimestamp() {
        return mDataTimestamp;
    }

    public void setDataTimestamp(long dataTimestamp) {
        mDataTimestamp = dataTimestamp;
    }

    public ArrayList<QueryDbResultDetailItem> getCodeList() {
        return mCodeList;
    }

    public void setCodeList(ArrayList<QueryDbResultDetailItem> codeList) {
        mCodeList = codeList;
    }

    public Map<Integer, QueryDbResultDetailItem> getCodeMap() {
        Map<Integer, QueryDbResultDetailItem> map = new HashMap<>();

        for (QueryDbResultDetailItem item : getCodeList()) {
            map.put(item.getCode(), item);
        }

        return map;
    }
}
