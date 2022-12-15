package com.imlaidian.utilslibrary.analyseQueryData;

/**
 * Created by jun on 2017/8/18.
 * query data result table-name table item
 */

public class QueryDbResultTLNameItem {
    private int mId;
    private String mName;
    private String mPrefixIden;
    private String mSuffixIden;
    private long mBeginTimestamp;
    private long mEndTimestamp;
    private long mCreateTimestamp;

    /// 分析中
    public static final int STATUS_ANALYSE_START = 0;
    /// 分析完成
    public static final int STATUS_ANALYSE_FINISH = 1;
    /// 上传中
    public static final int STATUS_UPLOAD_START = 2;
    /// 上传完成
    public static final int STATUS_UPLOAD_FINISH = 3;
    /// 状态
    private int mStatus;

    /// 以下字段预留
    private String mReserve1;
    private String mReserve2;
    private String mReserve3;
    private String mReserve4;

    public QueryDbResultTLNameItem() {

    }

    public QueryDbResultTLNameItem(String name, String prefixIden, String suffixIden,
                                   long beginTimestamp, long endTimestamp) {
        setName(name);
        setPrefixIden(prefixIden);
        setSuffixIden(suffixIden);
        setBeginTimestamp(beginTimestamp);
        setEndTimestamp(endTimestamp);
        setCreateTimestamp(System.currentTimeMillis());
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPrefixIden() {
        return mPrefixIden;
    }

    public void setPrefixIden(String prefixIden) {
        mPrefixIden = prefixIden;
    }

    public String getSuffixIden() {
        return mSuffixIden;
    }

    public void setSuffixIden(String suffixIden) {
        mSuffixIden = suffixIden;
    }

    public long getBeginTimestamp() {
        return mBeginTimestamp;
    }

    public void setBeginTimestamp(long beginTimestamp) {
        mBeginTimestamp = beginTimestamp;
    }

    public long getEndTimestamp() {
        return mEndTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        mEndTimestamp = endTimestamp;
    }

    public long getCreateTimestamp() {
        return mCreateTimestamp;
    }

    public void setCreateTimestamp(long createTimestamp) {
        mCreateTimestamp = createTimestamp;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public void setStatusAnalyseStart() {
        mStatus = STATUS_ANALYSE_START;
    }

    public void setStatusAnalyseFinish() {
        mStatus = STATUS_ANALYSE_FINISH;
    }

    public void setStatusUploadStart() {
        mStatus = STATUS_UPLOAD_START;
    }

    public void setStatusUploadFinish() {
        mStatus = STATUS_UPLOAD_FINISH;
    }

    public boolean isAnalyseFinish() {
        return STATUS_ANALYSE_FINISH == mStatus;
    }

    public boolean isUploadFinish() {
        return STATUS_UPLOAD_FINISH == mStatus;
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
