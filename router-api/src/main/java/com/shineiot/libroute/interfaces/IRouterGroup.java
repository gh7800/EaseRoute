package com.shineiot.libroute.interfaces;

import java.util.LinkedHashMap;

/**
 * @Description
 * @Author : GF63
 * @Date : 2023/2/22
 */
public interface IRouterGroup {
    void cacheRouterPathByGroup(LinkedHashMap<String, Class<? extends IRouterPath>> routes);
}
