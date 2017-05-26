package com.bigboss.heartrate.app;

import android.app.Application;

import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

public class MyApplication extends Application {
    {
        PlatformConfig.setQQZone("1106189430", "uHw8mSRKny38oe1x");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        UMShareAPI.get(this);
    }
}
