package com.shineiot.libroute.interfaces.test;

import com.shineiot.libroute.interfaces.IRouterGroup;
import com.shineiot.libroute.interfaces.IRouterPath;

import java.util.LinkedHashMap;

/**
 * @Description 文件映射
 * @Author : GF63
 * @Date : 2023/2/22
 */
public class IRouterGroupTest implements IRouterGroup {
    @Override
    public void cacheRouterPathByGroup(LinkedHashMap<String, Class<? extends IRouterPath>> map) {
        //map.get("group") = RouterGroup.class.java
    }
}
