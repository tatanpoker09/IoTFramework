package com.example.processortest;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

import tatanpoker.com.tree.annotations.Device;

@AutoService(Processor.class)
@SupportedAnnotationTypes("tatanpoker.com.tree.annotations.Device")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
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
        for (Element element : roundEnvironment.getElementsAnnotatedWith(Device.class)) {
            Device device = element.getAnnotation(Device.class);
            if (device.stub()) {
                createDeviceStub(element, roundEnvironment, device);
            }

        }
        return true;
    }

    private void createDeviceStub(Element element, RoundEnvironment roundEnvironment, Device device) {
        Name deviceName = element.getSimpleName();
        TypeName superclass = TypeName.get(element.asType());
        TypeSpec.Builder navigatorClass = TypeSpec
                .classBuilder(String.format("%sStub", deviceName.toString()))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(superclass);
        ClassName exception = ClassName.get("tatanpoker.com.frameworklib.exceptions", "InvalidIDException");
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(int.class, "id")
                .addParameter(int.class, "layout")
                .addException(exception)
                .addStatement("super(id, layout, null)")
                .build();
        navigatorClass.addMethod(constructor);
        for (ExecutableElement methodElement : getMethods(element, roundEnvironment)) {
            if (methodElement.getSimpleName().toString().equals("<init>")) {
                continue;
            }
            Name name = methodElement.getSimpleName();
            List<? extends VariableElement> parameters = methodElement.getParameters();
            MethodSpec.Builder method = MethodSpec.methodBuilder(name.toString())
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class);

            ClassName callMethodPacket = ClassName.get(
                    "tatanpoker.com.frameworklib.framework.network.packets",
                    "CallMethodPacket");
            ClassName framework = ClassName.get(
                    "tatanpoker.com.frameworklib.framework",
                    "Framework");

            method.addStatement("$T<$T> params = new $T<>()", List.class, Object.class, ArrayList.class);
            for (VariableElement parameter : parameters) {
                method.addParameter(TypeName.get(parameter.asType()),
                        parameter.getSimpleName().toString());
                method.addStatement("params.add($L)", parameter.getSimpleName().toString());
            }
            method.addStatement("$T methodPacket = new $T($T.getNetwork().getId(), getId(), \"$L\",params)", callMethodPacket, callMethodPacket, framework, name.toString());
            method.addStatement("$T.getNetwork().getClient().sendPacket(methodPacket)", framework);

            navigatorClass.addMethod(method.build());
        }
            /*
              3- Write generated class to a file
             */
        try {
            JavaFile.builder("tatanpoker.com.iotframework.annotation", navigatorClass.build()).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static @Nonnull
    List<ExecutableElement> getMethods(@Nonnull Element annotationElem, @Nonnull RoundEnvironment roundEnvironment) {
        List<ExecutableElement> outList = new ArrayList<>();

        String simpleName = annotationElem.getSimpleName().toString();
        for (Element elem : roundEnvironment.getRootElements())
            if (elem.getSimpleName().toString().equals(simpleName))
                for (Element methodDeclaration : elem.getEnclosedElements())
                    if (methodDeclaration instanceof ExecutableElement) {
                        ExecutableElement method = (ExecutableElement) methodDeclaration;
                        if (method.getSimpleName() != elem.getSimpleName()) {
                            outList.add(method);
                        }
                    }

        return outList;
    }
}
