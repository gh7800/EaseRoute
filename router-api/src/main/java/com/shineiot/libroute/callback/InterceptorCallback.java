package com.shineiot.libroute.callback;

import com.shineiot.libroute.Postcard;

public interface InterceptorCallback {
    /**
     * 未拦截，走正常流程
     * @author luoxiaohui
     * @createTime 2019-05-23 20:50
     */
    void onNext(Postcard postcard);

    /**
     * 拦截器拦截成功，中断流程
     * @author luoxiaohui
     * @createTime 2019-05-23 20:42
     */
    void onInterrupt(String interruptMsg);
}
