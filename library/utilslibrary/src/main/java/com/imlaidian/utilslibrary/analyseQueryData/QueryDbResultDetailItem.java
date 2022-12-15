package com.imlaidian.utilslibrary.analyseQueryData;

import java.io.Serializable;

/**
 * Created by jun on 2017/8/14.
 * query data per day result item
 */

public class QueryDbResultDetailItem implements Serializable {
    private static final long serialVersionUID = 6253124293393368170L;

    /// id
    private int mId;
    /// 分析结果的时间
    private long mAnalyseTimestamp;
    /// 错误代码
    private int mCode;
    /// 原始表里面的ID
    private long mSourceId;
    /// 数据的时间
    private long mDataTimestamp;
    /// 以下字段预留
    //用于存储播放状态 是开始 还是结束
    private String mReserve1;
    //用于存储视频源文件的 广告id adReleaseId
    private String mReserve2;
    //用于存储广告合作方
    private String mReserve3;
    //用于存储视频播放类型
    private String mReserve4;
    //用于存储视频播放时间
    private String mReserve5;
    //用于存储视频播放url
    private String mReserve6;
    private String mReserve7;
    private String mReserve8;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public long getAnalyseTimestamp() {
        return mAnalyseTimestamp;
    }

    public void setAnalyseTimestamp(long analyseTimestamp) {
        mAnalyseTimestamp = analyseTimestamp;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
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

    public String getReserve5() {
        return mReserve5;
    }

    public void setReserve5(String reserve5) {
        mReserve5 = reserve5;
    }

    public String getReserve6() {
        return mReserve6;
    }

    public void setReserve6(String reserve6) {
        mReserve6 = reserve6;
    }

    public String getReserve7() {
        return mReserve7;
    }

    public void setReserve7(String reserve7) {
        mReserve7 = reserve7;
    }

    public String getReserve8() {
        return mReserve8;
    }

    public void setReserve8(String reserve8) {
        mReserve8 = reserve8;
    }
}
