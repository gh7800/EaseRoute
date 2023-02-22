package com.shineiot.libroute.interfaces;

import com.shineiot.routerannotation.RouteMeta;

import java.util.LinkedHashMap;

/**
 * @Description
 * @Author : GF63
 * @Date : 2023/2/22
 */
public interface IRouterPath {
    void cacheRouterMetaByPath(LinkedHashMap<String, RouteMeta> atlas);
}
