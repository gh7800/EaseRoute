package com.shineiot.libroute;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.shineiot.libroute.callback.NavigationCallback;
import com.shineiot.libroute.interfaces.IRouterGroup;
import com.shineiot.libroute.interfaces.IRouterPath;
import com.shineiot.routerannotation.RouteMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

public class EaseRouter {
    private static volatile EaseRouter mInstance;
    private Application application;
    private Handler mHandler;

    private static final String TAG = "EasyRouter";
    private static final String ROUTE_ROOT_PAKCAGE = "com.shineiot.router.routes";
    private static final String SDK_NAME = "EaseRouter";
    private static final String SEPARATOR = "_";
    private static final String SUFFIX_ROOT = "Root";
    private static final String SUFFIX_INTERCEPTOR = "Interceptor";

    private EaseRouter() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static EaseRouter getInstance() {
        if (mInstance == null) {
            synchronized (EaseRouter.class) {
                if (mInstance == null) {
                    mInstance = new EaseRouter();
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

    }

    //反射获取APT/POET生成的类
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

    protected Object navigation(final Context context, final Postcard postcard, final int requestCode, final NavigationCallback callback) {

        /*if (callback != null) {
            InterceptorImpl.onInterceptions(postcard, new InterceptorCallback() {
                @Override
                public void onNext(Postcard postcard) {
                    _navigation(context, postcard, requestCode, callback);
                }

                @Override
                public void onInterrupt(String interruptMsg) {

                    callback.onInterrupt(new Throwable(interruptMsg));
                }
            });
        }else{*/

            return _navigation(context, postcard, requestCode, callback);
        //}

        //return null;
    }

    protected Object _navigation(final Context context, final Postcard postcard, final int requestCode, final NavigationCallback callback) {
        try {
            prepareCard(postcard);
        } catch (Exception e) {
            e.printStackTrace();
            //没找到
            if (null != callback) {
                callback.onLost(postcard);
            }
            return null;
        }
        if (null != callback) {
            callback.onFound(postcard);
        }

        switch (postcard.getType()) {
            case ACTIVITY:
                final Context currentContext = null == context ? application : context;
                final Intent intent = new Intent(currentContext, postcard.getDestination());
                intent.putExtras(postcard.getExtras());
                int flags = postcard.getFlags();
                if (-1 != flags) {
                    intent.setFlags(flags);
                } else if (!(currentContext instanceof Activity)) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //可能需要返回码
                        if (requestCode > 0) {
                            ActivityCompat.startActivityForResult((Activity) currentContext, intent,
                                    requestCode, postcard.getOptionsBundle());
                        } else {
                            ActivityCompat.startActivity(currentContext, intent, postcard
                                    .getOptionsBundle());
                        }

                        if ((0 != postcard.getEnterAnim() || 0 != postcard.getExitAnim()) &&
                                currentContext instanceof Activity) {
                            //老版本
                            ((Activity) currentContext).overridePendingTransition(postcard
                                            .getEnterAnim()
                                    , postcard.getExitAnim());
                        }
                        //跳转完成
                        if (null != callback) {
                            callback.onArrival(postcard);
                        }
                    }
                });
                break;
            case ISERVICE:
                return postcard.getService();
            default:
                break;
        }
        return null;
    }

    public Postcard build(String path) {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("路由地址无效!");
        } else {
            return build(path, extractGroup(path));
        }
    }

    /**
     * 获得组别
     *
     * @param path
     * @return
     */
    private String extractGroup(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new RuntimeException(path + " : 不能提取group.");
        }
        try {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            if (TextUtils.isEmpty(defaultGroup)) {
                throw new RuntimeException(path + " : 不能提取group.");
            } else {
                return defaultGroup;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Postcard build(String path, String group) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(group)) {
            throw new RuntimeException("路由地址无效!");
        } else {
            return new Postcard(path, group);
        }
    }

    /**
     * 准备卡片
     *
     * @param card
     */
    private void prepareCard(Postcard card) {
        RouteMeta routeMeta = WareHouse.routes.get(card.getPath());
        //Log.e("----------",card.getPath()+"----------"+routeMeta);

        if (null == routeMeta) {
            Class<? extends IRouterPath> groupMeta = WareHouse.groupsIndex.get(card.getGroup());
            if (null == groupMeta) {
                //throw new NoRouteFoundException("没找到对应路由：分组=" + card.getGroup() + "   路径=" + card.getPath());
                throw new RuntimeException("没找到对应路由：分组=" + card.getGroup() + "   路径=" + card.getPath());
            }
            IRouterPath iGroupInstance;
            try {
                iGroupInstance = groupMeta.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("路由分组映射表记录失败.", e);
            }
            iGroupInstance.cacheRouterMetaByPath(WareHouse.routes);

            //已经准备过了就可以移除了 (不会一直存在内存中)
            WareHouse.groupsIndex.remove(card.getGroup());
            //再次进入 else
            prepareCard(card);
        } else {
            //类 要跳转的activity 或IService实现类
            card.setDestination(routeMeta.getDestination());
            card.setType(routeMeta.getType());
            switch (routeMeta.getType()) {
                case ISERVICE:
                    Class<?> destination = routeMeta.getDestination();
                    IService service = WareHouse.services.get(destination);
                    if (null == service) {
                        try {
                            service = (IService) destination.getConstructor().newInstance();
                            WareHouse.services.put(destination, service);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    card.setService(service);
                    break;
                default:
                    break;
            }
        }
    }

}
