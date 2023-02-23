package com.shineiot.login.bean;

import java.io.Serializable;

/**
 * @Description
 * @Author : GF63
 * @Date : 2023/2/23
 */
public class TestBean implements Serializable {
    private String name;

    public TestBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
