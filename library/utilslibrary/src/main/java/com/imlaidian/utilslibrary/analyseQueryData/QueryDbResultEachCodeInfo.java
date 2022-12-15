package com.imlaidian.utilslibrary.analyseQueryData;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jun on 2017/8/24.
 * query data result each code info
 */

public class QueryDbResultEachCodeInfo implements Serializable{
    // 一个动作包含 多个异常代码
    private static final long serialVersionUID = 8998880257506312054L;

    private int mCode;
    // 广告 为AdReleaseId
    private String mTitle;
    private ArrayList<QueryDbResultEachCodeItem> mList = new ArrayList<>();

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public ArrayList<QueryDbResultEachCodeItem> getList() {
        return mList;
    }

    public void setList(ArrayList<QueryDbResultEachCodeItem> list) {
        mList = list;
    }

    public long getTotalTimestamp(long finishTimestamp) {
        long timestamp = 0;

        for (QueryDbResultEachCodeItem item : getList()) {
            timestamp += item.getTimestamp(finishTimestamp);
        }

        return timestamp;
    }
}
