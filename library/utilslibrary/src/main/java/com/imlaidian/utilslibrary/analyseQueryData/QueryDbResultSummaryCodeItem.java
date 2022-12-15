package com.imlaidian.utilslibrary.analyseQueryData;

import java.io.Serializable;

/**
 * Created by jun on 2017/8/25.
 * code item
 */

public class QueryDbResultSummaryCodeItem implements Serializable{

    private static final long serialVersionUID = 2971743060850407391L;

    private int mId;
    /// 指向summary table id
    private int mSummaryId;
    /// code value
    private int mCode;
    /// 指向detail table id
    private long mBeginId;
    /// 指向detail table id timestamp
    private long mBeginDataTimestamp;
    /// 指向detail table id
    private long mEndId;
    /// 指向detail table id timestamp
    private long mEndDataTimestamp ;
    /// 以下预留
    //广告的 AdReleaseId
    private String mReserve1;
    private String mReserve2;
    private String mReserve3;
    private String mReserve4;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getSummaryId() {
        return mSummaryId;
    }

    public void setSummaryId(int summaryId) {
        mSummaryId = summaryId;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public long getBeginId() {
        return mBeginId;
    }

    public void setBeginId(long beginId) {
        mBeginId = beginId;
    }

    public long getBeginDataTimestamp() {
        return mBeginDataTimestamp;
    }

    public void setBeginDataTimestamp(long beginDataTimestamp) {
        mBeginDataTimestamp = beginDataTimestamp;
    }

    public long getEndId() {
        return mEndId;
    }

    public void setEndId(long endId) {
        mEndId = endId;
    }

    public long getEndDataTimestamp() {
        return mEndDataTimestamp;
    }

    public void setEndDataTimestamp(long endDataTimestamp) {
        mEndDataTimestamp = endDataTimestamp;
    }

    public String getReserve1() {
        return mReserve1;
    }

    public void setReserve1(String reserve1) {
        mReserve1 = reserve1;
    }

    public String getReserve2() {
        return mReserve2;
    }

    public void setReserve2(String reserve2) {
        mReserve2 = reserve2;
    }

    public String getReserve3() {
        return mReserve3;
    }

    public void setReserve3(String reserve3) {
        mReserve3 = reserve3;
    }

    public String getReserve4() {
        return mReserve4;
    }

    public void setReserve4(String reserve4) {
        mReserve4 = reserve4;
    }
}
