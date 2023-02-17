package com.shineiot.libroute;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
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

    public boolean isPlugin = false;

    public  void loadRouterMap(Application application){
        List<Class<IRouterLoad>> classList = ClassUtils.getAllClassByInterface(IRouterLoad.class,application.getPackageName());
        for(Class<IRouterLoad> routerLoadClass : classList){
            Log.e(routerLoadClass.getPackage().getName(),routerLoadClass.getName());
        }
    }

    /**
     * 路由表
     * 保存，path/activity
     */
    private static Map<String,Class<? extends Activity>> routers = new HashMap<>();

    private void startActivity(Activity activity,String path){
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
