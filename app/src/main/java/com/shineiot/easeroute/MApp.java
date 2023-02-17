package com.shineiot.easeroute;

import android.app.Application;

import com.shineiot.libroute.Router;

public class MApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Router.getInstance().loadRouterMap(this);
    }
}
