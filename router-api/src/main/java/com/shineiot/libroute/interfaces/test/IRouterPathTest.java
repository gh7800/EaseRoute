package com.shineiot.libroute.interfaces.test;

import com.shineiot.libroute.interfaces.IRouterPath;
import com.shineiot.routerannotation.RouteMeta;

import java.util.LinkedHashMap;

/**
 * @Description 文件映射
 * @Author : GF63
 * @Date : 2023/2/22
 */
public class IRouterPathTest implements IRouterPath {

    @Override
    public void cacheRouterMetaByPath(LinkedHashMap<String, RouteMeta> map) {
        /*map.get("path1") = new RouteMeta(
                RouteMeta.Type.ACTIVITY,
                null,
                null,
                "",
                ""
        );*/
    }
}
