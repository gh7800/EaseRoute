package com.shineiot.libroute;

import com.shineiot.libroute.interfaces.IRouterPath;
import com.shineiot.routerannotation.RouteMeta;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description
 * @Author : GF63
 * @Date : 2023/2/22
 */
public class WareHouse {

    // root 映射表 保存分组信息
    static LinkedHashMap<String, Class<? extends IRouterPath>> groupsIndex = new LinkedHashMap<>();

    // group 映射表 保存组中的所有数据
    static LinkedHashMap<String, RouteMeta> routes = new LinkedHashMap<>();

    // group 映射表 保存组中的所有数据
    static Map<Class, IService> services = new HashMap<>();
    // TestServiceImpl.class , TestServiceImpl 没有再反射

    /**
     * 以键值对优先级的方式保存拦截器对象
     */
    //public static Map<Integer, Class<? extends IInterceptor>> interceptorsIndex = new UniqueKeyTreeMap<>();
    /**
     * 以集合的方式保存所有拦截器对象
     */
    //public static List<IInterceptor> interceptors = new ArrayList<>();
}
