package com.shineiot.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.shineiot.login.bean.TestBean;
import com.shineiot.routerannotation.Router;

@Router(path = "/login/testActivity")
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        TestBean testBean = (TestBean) getIntent().getSerializableExtra("test");
        Log.e("bean",testBean.getName());
    }
}