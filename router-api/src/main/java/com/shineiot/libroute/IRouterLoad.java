package com.shineiot.libroute;

import android.app.Activity;

import java.util.Map;

public interface IRouterLoad {
    void loadRoute(Map<String,Class<? extends Activity>> router);
}
