package com.shineiot.router_complier;

public class Const {
    public static final String ACTIVITY = "android.app.Activity";
    public static final String ISERVICE = "android.app.Fragment";
    public static final String ARGUMENTS_NAME = "moduleName";

    public static final String METHOD_LOAD_INTO = "cacheRouterMetaByPath";
    public static final String METHOD_LOAD_GROUP = "cacheRouterPathByGroup";
    public static final String PACKAGE_OF_GENERATE_FILE = "com.shineiot.router.routes";

    public static final String SEPARATOR = "_";
    public static final String PROJECT = "EaseRouter";
    public static final String NAME_OF_GROUP = PROJECT + SEPARATOR + "Group" + SEPARATOR;
    public static final String NAME_OF_ROOT = PROJECT + SEPARATOR + "Root" + SEPARATOR;

    public static final String I_ROUTER_PATH = "com.shineiot.libroute.interfaces.IRouterPath";
    public static final String I_ROUTER_GROUP = "com.shineiot.libroute.interfaces.IRouterGroup";
}
