package com.shineiot.libroute;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Router {
    private static volatile Router mInstance;
    private Application application;

    private Router() {
    }

    public static Router getInstance() {
        if (mInstance == null) {
            synchronized (Router.class) {
                if (mInstance == null) {
                    mInstance = new Router();
                }
            }
        }
        return mInstance;
    }


    public boolean isPlugin = false;

    public void loadRouterMap() {
    }

    /**
     * 初始化
     *
     * @param application
     */
    public void init(Application application) {
        this.application = application;

        /*通过DexFile和反射 获取所有实现IRouterLoad的类*/

        /*List<Class<IRouterLoad>> classList = ClassUtils.getAllClassByInterface(IRouterLoad.class, application.getPackageName());

        for (Class<IRouterLoad> routerLoadClass : classList) {
            Log.e("className", routerLoadClass.getName());
            try {
                Class<?> cls = Class.forName(routerLoadClass.getName());
                IRouterLoad routerLoad = (IRouterLoad) cls.newInstance();
                routerLoad.loadRouter(routers);
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
        }*/

    }

    /**
     * 导航
     * @param path
     */
    public void navigation(String path) {
        try {
            startActivity(path);
        }catch (Exception e){
            Log.e("导航失败",e.getMessage());
        }
    }


    /**
     * 路由表
     * 保存，path/activity
     */
    private static Map<String, Class<? extends Activity>> routers = new HashMap<>();

    public void printRouters(){
        for(String key : routers.keySet()){
            Class<?> cla = routers.get(key);
            Log.e("",key+"----------"+cla.getName());
        }
    }

    private void startActivity(String path) throws IllegalAccessException, InstantiationException {
        Class<? extends Activity> cls = routers.get(path);

        if (cls != null && Activity.class.isAssignableFrom(cls)) {
            Intent intent = new Intent(application, cls);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            application.startActivity(intent);
        }else {
            throw new RuntimeException("找不到activity");
        }
    }

    /**
     * 注册
     *
     * @param path
     * @param activity
     */
    public void register(String path, Class<? extends Activity> activity) {
        routers.put(path, activity);
    }
}
