package com.vonallin.lib.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.vonallin.lib.protocal.TestInterface;

import static javax.lang.model.element.Modifier.PUBLIC;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ProcessorTool {
    private ProcessingEnvironment processingEnv;
    private List<String> args = new ArrayList<>();

    public ProcessorTool(ProcessingEnvironment env) {
        this.processingEnv = env;
    }

    public ProcessorTool addArgs(String arg) {
        args.add(arg);
        return this;
    }

    /**
     * 用于打印log到文件
     */
    public void printLog() {
        Elements elementUtils = processingEnv.getElementUtils();
        TypeSpec.Builder builder = TypeSpec.classBuilder("Logger$$log2")
                .addModifiers(PUBLIC)
                .addSuperinterface(ClassName.get(elementUtils.getTypeElement(TestInterface.class.getCanonicalName())));

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("test")
                .addModifiers(PUBLIC);

        int len = args.size();
        for (int i = 0; i < len; i++) {
            String arg = args.get(i);
            methodBuilder.addStatement("$T arg" + i + "=$S", String.class, arg);
        }

        builder.addMethod(methodBuilder.build());

        JavaFile javaFile = JavaFile.builder("test", builder.build())
                .build();
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
