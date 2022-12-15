package com.imlaidian.utilslibrary.analyseQueryData;

import java.util.ArrayList;

/**
 * Created by jun on 2017/8/31.
 * summary each code item
 */

public class QueryDbResultSummaryEachCodeItem {
    private int mCode;
    // 广告的Release id
    private String mTitle;
    private ArrayList<QueryDbResultSummaryCodeItem> mList = new ArrayList<>();

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

    public ArrayList<QueryDbResultSummaryCodeItem> getList() {
        return mList;
    }

    public void setList(ArrayList<QueryDbResultSummaryCodeItem> list) {
        mList = list;
    }
}
