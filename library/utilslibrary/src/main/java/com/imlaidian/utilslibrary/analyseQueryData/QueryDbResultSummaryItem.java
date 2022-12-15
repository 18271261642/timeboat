package com.imlaidian.utilslibrary.analyseQueryData;

import java.io.Serializable;

/**
 * Created by jun on 2017/8/25.
 * query data result summary item
 */

public class QueryDbResultSummaryItem implements Serializable {
    private static final long serialVersionUID = 6373055941452795210L;

    private int mId;
    private String mName;
    private String mPrefixIden;
    private String mSuffixIden;
    private int mCodeCount;
    private String mContent;
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

    public int getCodeCount() {
        return mCodeCount;
    }

    public void setCodeCount(int codeCount) {
        mCodeCount = codeCount;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
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
