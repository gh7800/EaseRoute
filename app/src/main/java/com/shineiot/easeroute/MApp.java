package com.shineiot.easeroute;

import android.app.Application;

import com.shineiot.libroute.Router;
import com.shineiot.login.LoginActivity;

public class MApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Router.getInstance().register("login", LoginActivity.class);

    }
}
