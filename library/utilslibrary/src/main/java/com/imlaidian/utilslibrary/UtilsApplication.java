package com.imlaidian.utilslibrary;

import com.alibaba.android.arouter.launcher.ARouter;
import androidx.multidex.MultiDexApplication;

public class UtilsApplication extends MultiDexApplication {
    private static UtilsApplication instance;
    public static UtilsApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        if (BuildConfig.DEBUG){
            ARouter.openDebug();
            ARouter.openLog();
        }
        ARouter.init(this);

    }

}
