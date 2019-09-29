package com.example.processortest;

import com.example.annotationtest.CustomPrinter;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.example.annotationtest.CustomPrinter")
public class PrinterProcessor extends AbstractProcessor {
    private Filer filer;
    private Messager messager;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(CustomPrinter.class)) {
            TypeSpec.Builder navigatorClass = TypeSpec
                    .classBuilder("PrinterImpl")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            String text = element.getAnnotation(CustomPrinter.class).text();
            MethodSpec intentMethod = MethodSpec
                    .methodBuilder("printText")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(TypeName.VOID)
                    .addStatement("$T.out.println($S)", System.class, text)
                    .build();
            navigatorClass.addMethod(intentMethod);
            /*
              3- Write generated class to a file
             */
            try {
                JavaFile.builder("tatanpoker.com.iotframework.annotation", navigatorClass.build()).build().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return true;
    }
}
