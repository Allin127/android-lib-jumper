package com.vonallin.lib.compiler;

import com.google.auto.service.AutoService;
import com.vonallin.lib.annotation.AllinRouter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AutoService(Processor.class)
public class TestProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(AllinRouter.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

//    @Override
//    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //扫描整个工程   找出含有AAAAA注解的元素(包括类，)
//        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(AAAAA.class);
//        for (Element element : elements) {
//
//            TypeName typeName = ClassName.get(element.asType());
//
//            TypeSpec typeSpec = TypeSpec.classBuilder("GenerateTest")
//                    .addField(typeName, "test")
//                    //添加泛型信息
//                    .addTypeVariable(TypeVariableName.get(((TypeElement) element).getTypeParameters().get(0)))
//                    .build();
//
//            try {
//                JavaFile file = JavaFile.builder("com.test", typeSpec)
//                        .build();
//                String fileStr = file.toString();
//                System.out.println(fileStr);
//
//                JavaFile.builder("com.test", typeSpec)
//                        .build()
//                        .writeTo(processingEnv.getFiler());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        //扫描整个工程   找出含有AAAAA注解的元素(包括类，)
//        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(AAAAA.class);
//        for (Element element : elements) {
//            element.accept(new SimpleElementVisitor7<Void, Void>() {
//                @Override
//                public Void visitType(TypeElement typeElement, Void aVoid) {
//                    return super.visitType(typeElement, aVoid);
//                    //这是一个TypeElement
//                }
//
//                @Override
//                public Void visitExecutable(ExecutableElement executableElement, Void aVoid) {
//                    return super.visitExecutable(executableElement, aVoid);
//                    //这是一个executableElement
//                }
//
//                @Override
//                public Void visitPackage(PackageElement packageElement, Void aVoid) {
//                    return super.visitPackage(packageElement, aVoid);
//                    //这是一个PackageElement
//                }
//
//            }, null);
//
//        }
//        return true;
//    }
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //扫描整个工程   找出含有AAAAA注解的元素(包括类，)
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(AllinRouter.class);
        //由于编译器的输出无法打印到控制台，因此这里借助javapoet库把需要输出的信息写入到一个新的类
        //这个是我封装的一个简单的工具
        ProcessorTool builder =new ProcessorTool(processingEnv);
        for (Element element : elements) {
            AllinRouter aaaaa = element.getAnnotation(AllinRouter.class);
            if (element instanceof TypeElement) {
                builder.addArgs(" TypeElement: " + aaaaa.value());

                /*===============打印包信息=================*/
                builder.addArgs("=============================打印包信息================================");
                PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
                builder.addArgs("packageElement:  " + packageElement.getSimpleName().toString());
                builder.addArgs("packageElement:  " + packageElement.getQualifiedName());


                builder.addArgs("=============================打印泛型信息================================");
                List<? extends TypeParameterElement> typeParameters = ((TypeElement) element).getTypeParameters();
                for (TypeParameterElement typeParameter : typeParameters) {
                    builder.addArgs(typeParameter.getSimpleName().toString());
                }
                builder.addArgs("=============================================================");

            } else if (element instanceof ExecutableElement) {
                builder.addArgs("ExecutableElement: " + aaaaa.value());
            } else if (element instanceof VariableElement) {
                builder.addArgs(" VariableElement: " + aaaaa.value());
            }
        }
        builder.printLog();
        return true;
    }
}

