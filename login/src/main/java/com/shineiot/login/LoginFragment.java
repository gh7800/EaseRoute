package com.shineiot.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.shineiot.libroute.IService;
import com.shineiot.routerannotation.Router;

/**
 * @Description 要想通过路由获取fragment，需要实现 IService
 * @Author : GF63
 * @Date : 2023/3/2
 */
@Router(path = "/login/loginFragment")
public class LoginFragment extends Fragment implements IService {

}
