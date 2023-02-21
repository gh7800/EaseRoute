package com.shineiot.router_complier;

import com.google.auto.service.AutoService;
import com.shineiot.routerannotation.RouteMeta;
import com.shineiot.routerannotation.Router;

import java.util.ArrayList;
import java.util.HashMap;
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
    private Map<String, String> fileNameGroup = new TreeMap<>();
    //分组 key:组名 value:对应组的路由信息
    private Map<String, List<RouteMeta>> routerMetaByGroup = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        logUtil = LogUtils.newLog(processingEnv.getMessager());
        logUtil.i("processor--init");

        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        filerUtils = processingEnv.getFiler();


    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        logUtil.i("processing");

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
            if(routeVerify(routeMeta)) {
                List<RouteMeta> routeMetaList = routerMetaByGroup.get(routeMeta.getGroup());
                if (routeMetaList == null) {
                    routeMetaList = new ArrayList<>();
                    routeMetaList.add(routeMeta);
                    routerMetaByGroup.put(routeMeta.getGroup(),routeMetaList);
                } else {
                    routeMetaList.add(routeMeta);
                }
            }else {
                throw new RuntimeException(router + " group or path can not be empty");
            }
        }

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
    private void generateClassByPoet(){
        for(String key : routerMetaByGroup.keySet()){
            generateRouterPathByPoet(key,routerMetaByGroup.get(key));
        }

    }

    /**
     * 生成 RouterPath 类
     */
    private void generateRouterPathByPoet(String group,List<RouteMeta> list){

    }


/*
    private void processorRoute(Set<? extends Element> rootElements) {
        logUtil.i("123321");

        //获得Activity这个类的节点信息
        TypeElement activity = elementUtils.getTypeElement(Constant.ACTIVITY);
        TypeElement service = elementUtils.getTypeElement(Constant.ISERVICE);
        for (Element element : rootElements) {
            RouteMeta routeMeta;
            //类信息
            TypeMirror typeMirror = element.asType();
            logUtil.i("1-2-3-Route class:" + typeMirror.toString());
            Router route = element.getAnnotation(Router.class);
            if (typeUtils.isSubtype(typeMirror, activity.asType())) {
                routeMeta = new RouteMeta(RouteMeta.Type.ACTIVITY, route, element);
            } else if (typeUtils.isSubtype(typeMirror, service.asType())) {
                routeMeta = new RouteMeta(RouteMeta.Type.ISERVICE, route, element);
            } else {
                throw new RuntimeException("Just support Activity or IService Route: " + element);
            }
            categories(routeMeta);
        }
        TypeElement iRouteGroup = elementUtils.getTypeElement(Constant.IROUTE_GROUP);
        TypeElement iRouteRoot = elementUtils.getTypeElement(Constant.IROUTE_ROOT);

        //生成Group记录分组表
        generatedGroup(iRouteGroup);

        //生成Root类 作用：记录<分组，对应的Group类>
        generatedRoot(iRouteRoot, iRouteGroup);

    }

    *//**
     * 生成Root类  作用：记录<分组，对应的Group类>
     *
     * @param iRouteRoot
     * @param iRouteGroup
     *//*
    private void generatedRoot(TypeElement iRouteRoot, TypeElement iRouteGroup) {
        //创建参数类型 Map<String,Class<? extends IRouteGroup>> routes>
        //Wildcard 通配符
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(iRouteGroup))
                ));
        //参数 Map<String,Class<? extends IRouteGroup>> routes> routes
        ParameterSpec parameter = ParameterSpec.builder(parameterizedTypeName, "routes").build();
        //函数 public void loadInfo(Map<String,Class<? extends IRouteGroup>> routes> routes)
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constant.METHOD_LOAD_INTO)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(parameter);
        //函数体
        for (Map.Entry<String, String> entry : rootMap.entrySet()) {
            methodBuilder.addStatement("routes.put($S, $T.class)", entry.getKey(), ClassName.get(Constant.PACKAGE_OF_GENERATE_FILE, entry.getValue()));
        }
        //生成$Root$类
        String className = Constant.NAME_OF_ROOT + moduleName;
        TypeSpec typeSpec = TypeSpec.classBuilder(className)
                .addSuperinterface(ClassName.get(iRouteRoot))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodBuilder.build())
                .build();
        try {
            JavaFile.builder(Constant.PACKAGE_OF_GENERATE_FILE, typeSpec).build().writeTo(filerUtils);
            logUtil.i("Generated RouteRoot：" + Constant.PACKAGE_OF_GENERATE_FILE + "." + className);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generatedGroup(TypeElement iRouteGroup) {
        //创建参数类型 Map<String, RouteMeta>
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouteMeta.class));
        ParameterSpec altas = ParameterSpec.builder(parameterizedTypeName, "atlas").build();

        for (Map.Entry<String, List<RouteMeta>> entry : groupMap.entrySet()) {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constant.METHOD_LOAD_INTO)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(altas);

            String groupName = entry.getKey();
            List<RouteMeta> groupData = entry.getValue();
            for (RouteMeta routeMeta : groupData) {
                //函数体的添加
                methodBuilder.addStatement("atlas.put($S,$T.build($T.$L,$T.class,$S,$S))",
                        routeMeta.getPath(),
                        ClassName.get(RouteMeta.class),
                        ClassName.get(RouteMeta.Type.class),
                        routeMeta.getType(),
                        ClassName.get(((TypeElement) routeMeta.getElement())),
                        routeMeta.getPath(),
                        routeMeta.getGroup());
            }
            String groupClassName = Constant.NAME_OF_GROUP + groupName;
            TypeSpec typeSpec = TypeSpec.classBuilder(groupClassName)
                    .addSuperinterface(ClassName.get(iRouteGroup))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(methodBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(Constant.PACKAGE_OF_GENERATE_FILE, typeSpec).build();
            try {
                javaFile.writeTo(filerUtils);
            } catch (IOException e) {
                e.printStackTrace();
            }
            rootMap.put(groupName, groupClassName);

        }
    }

    *//**
     * 检查是否配置 group 如果没有配置 则从path截取出组名
     *
     * @param routeMeta
     *//*
    private void categories(RouteMeta routeMeta) {
        if (routeVerify(routeMeta)) {
            logUtil.i("Group : " + routeMeta.getGroup() + " path=" + routeMeta.getPath());
            //分组与组中的路由信息
            List<RouteMeta> routeMetas = groupMap.get(routeMeta.getGroup());
            if (Utils.isEmpty(routeMetas)) {
                routeMetas = new ArrayList<>();
                routeMetas.add(routeMeta);
                groupMap.put(routeMeta.getGroup(), routeMetas);
            } else {
                routeMetas.add(routeMeta);
            }
        } else {
            logUtil.i("Group info error:" + routeMeta.getPath());
        }
    }

    */

    /**
     * 验证path路由地址的合法性
     *  设置 group
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
        if (null != group && group.length() > 0) {
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