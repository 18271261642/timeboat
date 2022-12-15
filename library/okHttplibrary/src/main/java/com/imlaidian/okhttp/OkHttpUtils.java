package com.imlaidian.okhttp;


import static com.imlaidian.okhttp.model.OkHttpLinkStatusModel.DNS_REQUEST_FAILED;
import static com.imlaidian.okhttp.model.OkHttpLinkStatusModel.DNS_REQUEST_SUCCESS;
import static com.imlaidian.okhttp.model.OkHttpLinkStatusModel.NETTY_DNS_ERROR_CLOSED;
import static com.imlaidian.utilslibrary.config.PublicConstant.DNS_REQUEST_TYPE;

import android.net.Uri;

import com.imlaidian.okhttp.builder.GetBuilder;
import com.imlaidian.okhttp.builder.HeadBuilder;
import com.imlaidian.okhttp.builder.OtherRequestBuilder;
import com.imlaidian.okhttp.builder.PostFileBuilder;
import com.imlaidian.okhttp.builder.PostFormBuilder;
import com.imlaidian.okhttp.builder.PostStringBuilder;
import com.imlaidian.okhttp.callback.Callback;
import com.imlaidian.okhttp.https.HttpsUtils;
import com.imlaidian.okhttp.model.OkHttpLinkStatusModel;
import com.imlaidian.okhttp.request.OkHttpRequest;
import com.imlaidian.okhttp.request.RequestCall;
import com.imlaidian.okhttp.storage.OkHttpLinkStatusStorageProvide;
import com.imlaidian.okhttp.utils.Platform;
import com.imlaidian.utilslibrary.utils.LogUtil;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;


public class OkHttpUtils
{

    public static final long DEFAULT_MILLISECONDS = 30_000L;
    private volatile static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private Platform mPlatform;

    public static final String TAG = "OkHttpUtils";


    public static final int MainThread = 1;
    public static final int IOThread = 2 ;
    public static final int threadType = IOThread;

    public OkHttpUtils(OkHttpClient okHttpClient)
    {
        if (okHttpClient == null)
        {
            mOkHttpClient = new OkHttpClient();
        }
        else
        {
            mOkHttpClient = okHttpClient;
        }

//        mPlatform = Platform.get();
    }

    public void dispatchersThread(int threadType){
        mPlatform = Platform.findPlatform(threadType);
    }


    public Platform getPlatForm(){
        if(mPlatform==null){

            mPlatform = Platform.findPlatform(threadType);

        }
        return mPlatform;
    }
    public static OkHttpUtils initClient(OkHttpClient okHttpClient)
    {
        if (mInstance == null)
        {
            synchronized (OkHttpUtils.class)
            {
                if (mInstance == null)
                {
                    mInstance = new OkHttpUtils(okHttpClient);
                }
            }
        }
        return mInstance;
    }

    public static OkHttpUtils getInstance()
    {
        if (mInstance == null)
        {
            synchronized (OkHttpUtils.class)
            {
                if (mInstance == null)
                {
                    mInstance = new OkHttpUtils(configParameters());
                }
            }
        }
        return mInstance;

    }


    /// 配置参数
    public static OkHttpClient configParameters() {
        // 配置
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
//        CookieJarImpl cookieJar = new CookieJarImpl(new MemoryCookieStore());
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20000L, TimeUnit.MILLISECONDS)
                .readTimeout(20000L, TimeUnit.MILLISECONDS)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true ;
                    }
                })
//                .cookieJar(cookieJar)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();

        return   okHttpClient ;
    }

    public Executor getDelivery()
    {
        return getPlatForm().defaultCallbackExecutor();
    }

    public OkHttpClient getOkHttpClient()
    {
        return mOkHttpClient;
    }

    public static GetBuilder get()
    {
        return new GetBuilder();
    }

    public static PostStringBuilder postString()
    {
        return new PostStringBuilder();
    }

    public static PostFileBuilder postFile()
    {
        return new PostFileBuilder();
    }

    public static PostFormBuilder post()
    {
        return new PostFormBuilder();
    }

    public static OtherRequestBuilder put()
    {
        return new OtherRequestBuilder(METHOD.PUT);
    }

    public static HeadBuilder head()
    {
        return new HeadBuilder();
    }

    public static OtherRequestBuilder delete()
    {
        return new OtherRequestBuilder(METHOD.DELETE);
    }

    public static OtherRequestBuilder patch()
    {
        return new OtherRequestBuilder(METHOD.PATCH);
    }

    public void blockWaitExecute(final RequestCall requestCall, Callback callback){

        if (callback == null){
            LogUtil.d(TAG, "execute callback");
            callback = Callback.CALLBACK_DEFAULT;
        }

        final Callback finalCallback = callback;
        final OkHttpRequest okHttpRequest = requestCall.getOkHttpRequest();
        LogUtil.d(TAG , "url =" + okHttpRequest.getUrl()) ;

        if(okHttpRequest.getParams()!=null){
            LogUtil.d(TAG ,  "Params=" + okHttpRequest.getParams().toString()) ;
        }

        if(okHttpRequest.getHeaders()!=null){
            LogUtil.d(TAG ,  "Params=" + okHttpRequest.getHeaders().toString()) ;
        }
        final int id = requestCall.getOkHttpRequest().getId();
        try{
            Call call =requestCall.getCall();
            Response response = call.execute();
            try
            {
                if (call.isCanceled())
                {
                    sendBlockFailResultCallback(call, new IOException("Canceled!"), finalCallback, id);
                    return;
                }

                if (!finalCallback.validateReponse(response, id))
                {
                    sendBlockFailResultCallback(call, new IOException("request failed , reponse's code is : " + response.code()), finalCallback, id);
                    return;
                }

                Object o = finalCallback.parseNetworkResponse(response, id);
                sendBlockSuccessResult(o, finalCallback, id);
            } catch (Exception e)
            {
                sendBlockFailResultCallback(call, e, finalCallback, id);
            } finally
            {
                if (response.body() != null)
                    response.body().close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void execute(final RequestCall requestCall, Callback callback)
    {
        if (callback == null){
            LogUtil.d(TAG, "execute callback");
            callback = Callback.CALLBACK_DEFAULT;
        }

        final Callback finalCallback = callback;
        final OkHttpRequest okHttpRequest = requestCall.getOkHttpRequest();
        LogUtil.d(TAG , "url =" + okHttpRequest.getUrl()) ;
        if(okHttpRequest.getParams()!=null){
            LogUtil.d(TAG ,  "Params=" + okHttpRequest.getParams().toString()) ;
        }

        if(okHttpRequest.getHeaders()!=null){
            LogUtil.d(TAG ,  "Params=" + okHttpRequest.getHeaders().toString()) ;
        }
        final int id = requestCall.getOkHttpRequest().getId();
        requestCall.getCall().enqueue(new okhttp3.Callback()
        {
            @Override
            public void onFailure(Call call, final IOException e)
            {
                sendFailResultCallback(call, e, finalCallback, id);
            }

            @Override
            public void onResponse(final Call call, final Response response)
            {
                try
                {
                    if (call.isCanceled())
                    {
                        LogUtil.d(TAG, "onResponse isCanceled");

                        sendFailResultCallback(call, new IOException("Canceled!"), finalCallback, id);

                        return;
                    }

                    if (!finalCallback.validateReponse(response, id))
                    {

                        sendFailResultCallback(call, new IOException("request failed , reponse's code is : " + response.code()), finalCallback, id);

                        return;
                    }

                    if(LogUtil.debug){
                        String contentLengthValue = response.header("Content-Length");
                        if(contentLengthValue==null){
                            if(response.body()!=null){
                                contentLengthValue = "" + response.body().contentLength();
                            }

                        }
                        LogUtil.d(TAG, "onResponse validateReponse contentLengthValue=" +contentLengthValue);
                    }


                    Object o = finalCallback.parseNetworkResponse(response, id);
                    sendSuccessResultCallback(o, finalCallback, id);
                } catch (Exception e)
                {
                    sendFailResultCallback(call, e, finalCallback, id);
                } finally
                {
                    if (response.body() != null)
                        response.body().close();
                }

            }
        });
    }

    public  void sendBlockFailResultCallback(final Call call, final Exception e, final Callback callback, final int id){
        if (callback == null) {
            LogUtil.d(TAG," sendFailResultCallback callback null"  );
            return;
        }
        callback.onError(call, e, id);
        callback.onAfter(id);
    }

    public void sendBlockSuccessResult(final Object object, final Callback callback, final int id){
        if (callback == null) {
            LogUtil.d(TAG," sendSuccessResultCallback callback  null"  );
            return;
        }
        LogUtil.d(TAG," sendSuccessResultCallback execute"  );
        callback.onResponse(object, id);
        callback.onAfter(id);
    }

    public void sendFailResultCallback(final Call call, final Exception e, final Callback callback, final int id) {

        if (callback == null) {
            LogUtil.d(TAG," sendFailResultCallback callback null"  );
            return;
        }

        getPlatForm().execute(new Runnable()
        {
            @Override
            public void run()
            {
                LogUtil.d(TAG," sendFailResultCallback execute"  );
                callback.onError(call, e, id);
                callback.onAfter(id);
            }
        });
    }

    public void sendSuccessResultCallback(final Object object, final Callback callback, final int id)
    {
        if (callback == null) {
            LogUtil.d(TAG," sendSuccessResultCallback callback  null"  );
            return;
        }
        getPlatForm().execute(new Runnable()
        {
            @Override
            public void run()
            {
                LogUtil.d(TAG," sendSuccessResultCallback execute"  );
                callback.onResponse(object, id);
                callback.onAfter(id);
            }
        });
    }

    public void cancelTag(Object tag)
    {
        LogUtil.d(TAG,"  cancelTag "  );
        for (Call call : mOkHttpClient.dispatcher().queuedCalls())
        {
            if (tag.equals(call.request().tag()))
            {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls())
        {
            if (tag.equals(call.request().tag()))
            {
                call.cancel();
            }
        }
    }

    public static class METHOD
    {
        public static final String HEAD = "HEAD";
        public static final String DELETE = "DELETE";
        public static final String PUT = "PUT";
        public static final String PATCH = "PATCH";
    }
}
