package com.shineiot.easeroute;

import android.app.Application;
import android.content.pm.PackageManager;

import com.shineiot.libroute.Router;

import java.lang.reflect.InvocationTargetException;

public class MApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Router.getInstance().init(this);
    }
}
