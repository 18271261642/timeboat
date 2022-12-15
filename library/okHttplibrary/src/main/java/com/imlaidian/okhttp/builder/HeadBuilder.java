package com.imlaidian.okhttp.builder;


import com.imlaidian.okhttp.OkHttpUtils;
import com.imlaidian.okhttp.request.OtherRequest;
import com.imlaidian.okhttp.request.RequestCall;

public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
