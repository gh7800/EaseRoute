package com.shineiot.login;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.shineiot.libroute.EaseRouter;
import com.shineiot.login.bean.TestBean;
import com.shineiot.routerannotation.Router;

@Router(path = "/login/loginActivity")
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.tvLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TestBean bean = new TestBean("张三");
                EaseRouter.getInstance().build("/login/testActivity").withSerializable("test",bean).navigation();
            }
        });
    }
}