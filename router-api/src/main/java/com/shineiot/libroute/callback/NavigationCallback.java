package com.shineiot.libroute.callback;

import com.shineiot.libroute.Postcard;

public interface NavigationCallback {
    /**
     * 找到跳转页面
     * @param postcard
     */
    void onFound(Postcard postcard);

    /**
     * 未找到
     * @param postcard
     */
    void onLost(Postcard postcard);

    /**
     * 成功跳转
     * @param postcard
     */
    void onArrival(Postcard postcard);

    /**
     * 中断了路由跳转
     * @author luoxiaohui
     * @createTime 2019-06-18 17:00
     */
    void onInterrupt(Throwable throwable);
}
