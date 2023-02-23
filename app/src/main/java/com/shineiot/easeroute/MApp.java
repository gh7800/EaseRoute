package com.shineiot.easeroute;

import android.app.Application;

import com.shineiot.libroute.EaseRouter;

public class MApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        EaseRouter.getInstance().init(this);
    }
}
