package com.shineiot.route_complier;

import com.google.auto.service.AutoService;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * @Description router-compiler 必须以 Java Lib 的形式创建
 * @Author : GF63
 * @Date : 2023/2/17
 *
 * 自定义注解处理器
 */
@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,"TEST NativeProcessor");

        return true;
    }
}
