package com.shineiot.router_complier;

import com.google.auto.service.AutoService;
import com.shineiot.routerannotation.RouteMeta;
import com.shineiot.routerannotation.Router;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;


/**
 * 指定使用的Java版本 替代 {@link AbstractProcessor#getSupportedSourceVersion()} 函数
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
/*
 * 指定要处理的注解类
 */
@SupportedAnnotationTypes("com.shineiot.routerannotation.Router")
/*
 * 自动写入注解处理器，不需要手动在resources/META-INF/services下的文件写入
 */
@AutoService(Processor.class)

/*
 * 自定义注解处理器
 */
public class RouterProcessor extends AbstractProcessor {

    /**
     * 节点工具类 (类、函数、属性都是节点)
     */
    private Elements elementUtils;
    /**
     * type(类信息)工具类
     */
    private Types typeUtils;
    /**
     * 文件操作类 类/资源
     */
    private Filer filerUtils;

    private String moduleName;

    private LogUtils logUtil;

    private boolean isInit = false;//避免多次执行

    /**
     * key:组名 value:类名 :文件名列表的map
     */
    private final Map<String, String> fileNameGroup = new TreeMap<>();
    //分组 key:组名 value:对应组的路由信息
    private Map<String, List<RouteMeta>> routerMetaByGroup = new HashMap<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        logUtil = LogUtils.newLog(processingEnv.getMessager());
        logUtil.i("processor--init");

        //参数是模块名 为了防止多模块/组件化开发的时候 生成相同的 xx$$ROOT$$文件
        Map<String, String> options = processingEnv.getOptions();
        if ( null != options && options.size() > 0) {
            moduleName = options.get(Const.ARGUMENTS_NAME);
        }

        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        filerUtils = processingEnv.getFiler();


    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        logUtil.i("____processing");

        if (isInit) {
            return true;
        }
        isInit = true;

        //获取所有使用Router注解的节点,并遍历获取所有节点信息
        Set<? extends Element> rootElements = roundEnv.getElementsAnnotatedWith(Router.class);
        for (Element element : rootElements) {
            //获取注释
            Router router = element.getAnnotation(Router.class);

            //获取路由信息
            RouteMeta routeMeta = getRouterMeta(element, router, typeUtils);
            //验证路由合法性
            if (routeVerify(routeMeta)) {
                List<RouteMeta> routeMetaList = routerMetaByGroup.get(routeMeta.getGroup());
                if (routeMetaList == null) {
                    routeMetaList = new ArrayList<>();
                    routeMetaList.add(routeMeta);
                    routerMetaByGroup.put(routeMeta.getGroup(), routeMetaList);
                } else {
                    routeMetaList.add(routeMeta);
                }
            } else {
                throw new RuntimeException(router + " group or path can not be empty");
            }
        }

        TypeElement typeElement = elementUtils.getTypeElement(Const.I_ROUTER_PATH);
        logUtil.i("_________typeElement______" + typeElement);

        generateClassByPoet(typeElement);

        /*for(List<RouteMeta> data : routerMetaByGroup.values()){
            for(RouteMeta routeMeta : data){
                logUtil.i(routeMeta.getGroup());
                logUtil.i(routeMeta.getPath());
            }
        }*/

        /*if(!Utils.isEmpty(annotations)) {
            //被Route注解的节点集合
            Set<? extends Element> rootElements = roundEnv.getElementsAnnotatedWith(RouterAnnotation.class);
            //logUtil.i(rootElements.size() + "12344");
            if (!Utils.isEmpty(rootElements)) {
                processorRoute(rootElements);
            }
            return true;
        }*/

        return true;
    }

    /**
     * 获取RouterMeta 信息
     */
    private RouteMeta getRouterMeta(Element element, Router router, Types typeUtils) {
        RouteMeta routeMeta;
        //类信息
        TypeMirror typeMirror = element.asType();

        //获得Activity这个类的节点信息
        TypeElement activity = elementUtils.getTypeElement(Const.ACTIVITY);
        TypeElement service = elementUtils.getTypeElement(Const.ISERVICE);

        if (typeUtils.isSubtype(typeMirror, activity.asType())) {
            routeMeta = new RouteMeta(RouteMeta.Type.ACTIVITY, router, element);
        } else if (typeUtils.isSubtype(typeMirror, service.asType())) {
            routeMeta = new RouteMeta(RouteMeta.Type.ISERVICE, router, element);
        } else {
            throw new RuntimeException("Just support Activity or Fragment Route: " + element);
        }

        return routeMeta;
    }

    /**
     * 利用poet,生成java类代码
     */
    private void generateClassByPoet(TypeElement typeElement) {
        for (String key : routerMetaByGroup.keySet()) {
            generateRouterPathByPoet(key, routerMetaByGroup.get(key), typeElement);
        }

        generaRouterGroupByPoet();

    }

    /**
     * 生成 RouterPath 类
     * 存放单个群组的所有 Path-RouteMeta
     */
    private void generateRouterPathByPoet(String groupName, List<RouteMeta> list, TypeElement typeElement) {
        //创建参数类型 Map<String, RouteMeta>
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(LinkedHashMap.class),
                ClassName.get(String.class),
                ClassName.get(RouteMeta.class));
        //创建参数 atlas
        ParameterSpec altas = ParameterSpec.builder(parameterizedTypeName, "atlas").build();

        //创建函数 Const.METHOD_LOAD_INTO
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Const.METHOD_LOAD_INTO)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(altas);

        for (RouteMeta routeMeta : list) {
            methodBuilder.addStatement("atlas.put($S,$T.build($T.$L,$T.class,$S,$S))",
                    routeMeta.getPath(),//以path为key，因为group可能相同
                    ClassName.get(RouteMeta.class),
                    ClassName.get(RouteMeta.Type.class),
                    routeMeta.getType(),
                    ClassName.get((TypeElement) routeMeta.getElement()),
                    routeMeta.getPath(),
                    routeMeta.getGroup());
        }

        String groupClassName = Const.NAME_OF_GROUP + groupName;

        //TypeElement typeElement = elementUtils.getTypeElement(Const.I_ROUTER_PATH);

        TypeSpec typeSpec = TypeSpec.classBuilder(groupClassName)
                .addSuperinterface(ClassName.get(typeElement))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodBuilder.build())
                .build();
        JavaFile javaFile = JavaFile.builder(Const.PACKAGE_OF_GENERATE_FILE, typeSpec).build();
        try {
            javaFile.writeTo(filerUtils);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        fileNameGroup.put(groupName, groupClassName);
    }

    /**
     * 生成root类
     *  存放所有group的类信息
     */
    private void generaRouterGroupByPoet() {
        //创建参数类型 Map<String,Class<? extends IRouteGroup>> routes>
        //Wildcard 通配符
        TypeElement typeElement = elementUtils.getTypeElement(Const.I_ROUTER_GROUP);
        TypeElement typeElementPath = elementUtils.getTypeElement(Const.I_ROUTER_PATH);

        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(LinkedHashMap.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(typeElementPath))
                ));
        //参数 Map<String,Class<? extends IRouteGroup>> routes> routes
        ParameterSpec parameter = ParameterSpec.builder(parameterizedTypeName, "routes").build();
        //函数 public void loadInfo(Map<String,Class<? extends IRouteGroup>> routes> routes)
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Const.METHOD_LOAD_GROUP)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(parameter);
        //函数体
        for (Map.Entry<String, String> entry : fileNameGroup.entrySet()) {
            methodBuilder.addStatement("routes.put($S, $T.class)", entry.getKey(), ClassName.get(Const.PACKAGE_OF_GENERATE_FILE, entry.getValue()));
        }
        //生成$Root$类
        String className = Const.NAME_OF_ROOT + moduleName;
        TypeSpec typeSpec = TypeSpec.classBuilder(className)
                .addSuperinterface(ClassName.get(typeElement))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodBuilder.build())
                .build();
        try {
            JavaFile.builder(Const.PACKAGE_OF_GENERATE_FILE, typeSpec).build().writeTo(filerUtils);
            logUtil.i("Generated RouteRoot：" + Const.PACKAGE_OF_GENERATE_FILE + "." + className);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证path路由地址的合法性
     * 设置 group
     *
     * @param routeMeta
     * @return
     */
    private boolean routeVerify(RouteMeta routeMeta) {
        String path = routeMeta.getPath();
        String group = routeMeta.getGroup();
        // 必须以 / 开头来指定路由地址
        if (!path.startsWith("/")) {
            return false;
        }
        //如果group没有设置 我们从path中获得group
        if (null == group || group.equals("")) {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            //截取出的group还是空
            if (defaultGroup.length() == 0) {
                return false;
            }
            routeMeta.setGroup(defaultGroup);
        }
        return true;
    }
}