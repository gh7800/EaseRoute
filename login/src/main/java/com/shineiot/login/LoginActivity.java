package com.shineiot.login;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.shineiot.routerannotation.Router;

@Router(path = "/login/loginActivity")
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }
}