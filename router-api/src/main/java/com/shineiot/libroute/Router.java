package com.shineiot.libroute;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.shineiot.libroute.interfaces.IRouterGroup;
import com.shineiot.libroute.interfaces.IRouterPath;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Router {
    private static volatile Router mInstance;
    private Application application;

    private static final String TAG = "EasyRouter";
    private static final String ROUTE_ROOT_PAKCAGE = "com.shineiot.router.routes";
    private static final String SDK_NAME = "EaseRouter";
    private static final String SEPARATOR = "_";
    private static final String SUFFIX_ROOT = "Root";
    private static final String SUFFIX_INTERCEPTOR = "Interceptor";

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

        try {
            loadInfo();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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

    private void loadInfo() throws PackageManager.NameNotFoundException, InterruptedException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Set<String> routerMap  = ClassUtils.getFileNameByPackageName(this.application,ROUTE_ROOT_PAKCAGE);
        Log.e("set",routerMap.size()+"");

        for (String className : routerMap) {
            if (className.startsWith(ROUTE_ROOT_PAKCAGE + "." + SDK_NAME + SEPARATOR + SUFFIX_ROOT)) {
                Log.e("className",className);

                //root中注册的是分组信息 将分组信息加入仓库中
                ((IRouterGroup) Class.forName(className).getConstructor().newInstance()).cacheRouterPathByGroup(WareHouse.groupsIndex);

            } /*else if (className.startsWith(ROUTE_ROOT_PAKCAGE + "." + SDK_NAME + SEPARATOR + SUFFIX_INTERCEPTOR)) {

                ((IInterceptorGroup) Class.forName(className).getConstructor().newInstance()).loadInto(Warehouse.interceptorsIndex);
            }*/
        }

        for (Map.Entry<String, Class<? extends IRouterPath>> stringClassEntry : WareHouse.groupsIndex.entrySet()) {
            Log.e(TAG, "Root映射表[ " + stringClassEntry.getKey() + " : " + stringClassEntry.getValue() + "]");
        }

        int size = WareHouse.groupsIndex.size();
        Log.e("size",""+size);
    }

    /**
     * 导航
     * @param path
     */
    public void navigation(String path) {
        for(String key : WareHouse.groupsIndex.keySet()){
            Class<? extends IRouterPath> routerPath = WareHouse.groupsIndex.get(key);
            //routerPath.
        }
    }


    /**
     * 路由表
     * 保存，path/activity
     */
    private static Map<String, Class<? extends Activity>> routers = new HashMap<>();

    public void printRouters(){
        for(String key : WareHouse.routes.keySet()){
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
