package com.shineiot.libroute;

import android.app.Activity;

import java.util.Map;

/**
 * @author GF63
 */
public interface IRouterLoad {
    void loadRouter(Map<String,Class<? extends Activity>> router);
}
