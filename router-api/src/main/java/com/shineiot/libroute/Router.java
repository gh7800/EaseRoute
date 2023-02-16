package com.shineiot.libroute;

import android.app.Activity;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private static volatile Router mInstance;

    private Router(){}

    public static Router getInstance(){
        if(mInstance == null){
            synchronized (Router.class){
                if(mInstance == null){
                    mInstance = new Router();
                }
            }
        }
        return mInstance;
    }

    //路由表
    private static Map<String,Class<? extends Activity>> routers = new HashMap<>();

    public void startActivity(Activity activity,String path){
        Class<? extends Activity> cls = routers.get(path);
        if(cls != null){
            Intent intent = new Intent(activity,cls);
            activity.startActivity(intent);
        }
    }

    /**
     * 注册
     * @param path
     * @param activity
     */
    public void register(String path,Class<? extends Activity> activity){
        routers.put(path,activity);
    }
}
