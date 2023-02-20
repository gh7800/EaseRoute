package com.shineiot.login;

import android.app.Activity;

import com.shineiot.libroute.IRouterLoad;

import java.util.Map;

/**
 * @author GF63
 */
public class LoginRouter implements IRouterLoad {
    @Override
    public void loadRouter(Map<String, Class<? extends Activity>> router) {
        router.put("/login/loginActivity",LoginActivity.class);
    }
}
