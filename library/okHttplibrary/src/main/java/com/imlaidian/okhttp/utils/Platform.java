
package com.imlaidian.okhttp.utils;

import static com.imlaidian.okhttp.OkHttpUtils.IOThread;
import static com.imlaidian.okhttp.OkHttpUtils.MainThread;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Platform
{

//    private static final Platform PLATFORM = findPlatform();
//
//    public static Platform get()
//    {
//        L.e(PLATFORM.getClass().toString());
//        return PLATFORM;
//    }

    public static Platform findPlatform(int threadType )
    {
        Platform PLATFORM =null;
        try
        {
            Class.forName("android.os.Build");
            if (Build.VERSION.SDK_INT != 0)
            {
                if(threadType ==MainThread){
                    PLATFORM =new Android();
                }else if(threadType ==IOThread){
                    PLATFORM =new Platform();
                }else{
                    PLATFORM =new Android();
                }
                return PLATFORM;
            }
        } catch (ClassNotFoundException ignored)
        {
            ignored.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return new Platform();
    }

    public Executor defaultCallbackExecutor()
    {
        return Executors.newCachedThreadPool();
    }

    public void execute(Runnable runnable)
    {
        defaultCallbackExecutor().execute(runnable);
    }


    static class Android extends Platform
    {
        @Override
        public Executor defaultCallbackExecutor()
        {
            return new MainThreadExecutor();
        }

        static class MainThreadExecutor implements Executor
        {
            private final Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void execute(Runnable r)
            {
                handler.post(r);
            }
        }
    }


}
