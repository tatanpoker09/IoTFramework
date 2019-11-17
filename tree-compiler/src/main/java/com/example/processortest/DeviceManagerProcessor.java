package com.example.processortest;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.IncompleteAnnotationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import tatanpoker.com.tree.annotations.Device;
import tatanpoker.com.tree.annotations.DeviceManager;
import tatanpoker.com.tree.annotations.Local;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"tatanpoker.com.tree.annotations.DeviceManager", "tatanpoker.com.tree.annotations.Device", "tatanpoker.com.tree.annotations.Local"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class DeviceManagerProcessor extends AbstractProcessor {
    private Filer filer;
    private Messager messager;
    private Elements elementUtils;
    private static final String SUFFIX = "_Impl";
    private List<DeviceMethod> methodCodeBlock;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Map<String, Device> devices = new HashMap<>();
        methodCodeBlock = new ArrayList<>();
        for (Element element : roundEnvironment.getElementsAnnotatedWith(Device.class)) {
            Device device = element.getAnnotation(Device.class);
            devices.put(element.getSimpleName().toString(), device);
            createDeviceStub(element, roundEnvironment, device);
        }
        for (Element element : roundEnvironment.getElementsAnnotatedWith(DeviceManager.class)) {
            loadDeviceManager(element, devices);
        }

        return true;
    }


    private void loadDeviceManager(Element element, Map<String, Device> devices) {

        //This is a variable.
        TypeName superclass = TypeName.get(element.asType());

        TypeSpec.Builder navigatorClass = TypeSpec
                .classBuilder(String.format("%s%s", element.getSimpleName().toString(), SUFFIX))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(superclass);

        MethodSpec.Builder initMethodBuilder = MethodSpec.methodBuilder("init")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);
        initMethodBuilder.addStatement("devices = new $T<>()", ArrayList.class);
        initMethodBuilder.beginControlFlow("try");
        boolean local = false;
        MethodSpec.Builder callHandlerMethod = MethodSpec.methodBuilder("callByID")
                .addParameter(int.class, "id")
                .addParameter(Object[].class, "params")
                .varargs(true)
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class);
        Map<String, String> deviceMethodMap = new HashMap<>();
        for (Element enclosedElement : element.getEnclosedElements()) {
            if (enclosedElement instanceof ExecutableElement) {
                if (enclosedElement.getSimpleName().toString().equals("<init>")) {
                    continue;
                }
                TypeMirror returnTypeMirror = ((ExecutableElement) enclosedElement).getReturnType();
                TypeName returnTypeName = TypeName.get(returnTypeMirror);
                String fieldName = returnTypeName.toString().toLowerCase()
                        .substring(returnTypeName.toString().lastIndexOf(".") + 1);

                String methodName = enclosedElement.getSimpleName().toString();
                String deviceClassName = returnTypeName.toString().substring(returnTypeName.toString().lastIndexOf(".") + 1);
                deviceMethodMap.put(deviceClassName, methodName);
                Device device = devices.get(deviceClassName);

                if (device != null) {
                    MethodSpec method = MethodSpec.methodBuilder(enclosedElement.getSimpleName().toString())
                            .addAnnotation(Override.class)
                            .returns(returnTypeName)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement("return $L", fieldName)
                            .build();
                    if (enclosedElement.getAnnotation(Local.class) != null) {
                        if (!local) {
                            local = true;
                            initMethodBuilder.addStatement("$L = new $T($L, $L)", fieldName, returnTypeName, device.id(), device.layout());
                            initMethodBuilder.addStatement("local = $L", fieldName);
                        } else {
                            throw new IncompleteAnnotationException(Local.class, "There can only be one local component.");
                        }
                    } else {
                        initMethodBuilder.addStatement("$L = new $TStub($L, $L)", fieldName, returnTypeName, device.id(), device.layout());
                    }
                    initMethodBuilder.addStatement("devices.add($L)", fieldName);

                    FieldSpec field = FieldSpec.builder(returnTypeName, fieldName)
                            .addModifiers(Modifier.PRIVATE)
                            .build();
                    navigatorClass.addField(field);
                    navigatorClass.addMethod(method);
                }
            }
        }
        int id = 0;
        for (DeviceMethod deviceMethod : methodCodeBlock) {
            //String methodName = deviceMethod.getDevice();
            String methodName = deviceMethodMap.get(deviceMethod.getDevice());
            if (id == 0) {
                callHandlerMethod.beginControlFlow("if(id==$L)", id);
            } else {
                callHandlerMethod.nextControlFlow("else if(id==$L)", id);
            }
            StringBuilder stringBuilder = new StringBuilder();
            int index = 0;
            for (String parameter : deviceMethod.getParameters()) {
                stringBuilder.append(String.format(",(%s)(params[%d])", parameter, index));
                index += 1;
            }
            String params = stringBuilder.toString();
            params = params.length() == 0 ? params : params.substring(1); //This is just to remove a leading comma.
            callHandlerMethod.addStatement("$L().$L($L)", methodName, deviceMethod.getMethod(), params);
            id++;
        }
        callHandlerMethod.endControlFlow();

        ClassName invalidIDException = ClassName.get("tatanpoker.com.frameworklib.exceptions", "InvalidIDException");

        initMethodBuilder.nextControlFlow("catch ($T e)", invalidIDException)
                .addStatement("e.printStackTrace()").
                endControlFlow();

        MethodSpec initMethod = initMethodBuilder.build();
        navigatorClass.addMethod(callHandlerMethod.build());
        navigatorClass.addMethod(initMethod);
        try {
            JavaFile.builder("tatanpoker.com.iotframework", navigatorClass.build()).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                .addStatement("super(id, layout)")
                .build();
        navigatorClass.addMethod(constructor);
        for (ExecutableElement methodElement : getMethods(element, roundEnvironment)) {
            if (methodElement.getSimpleName().toString().equals("<init>")) {
                continue;
            }
            Name name = methodElement.getSimpleName();
            List<? extends VariableElement> parameters = methodElement.getParameters();
            List<String> parameterTypes = new ArrayList<>();
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
                parameterTypes.add(TypeName.get(parameter.asType()).toString());
            }
            //TODO change name.toString() into an id which can be recognized on the other side.
            method.addStatement("$T methodPacket = new $T($T.getNetwork().getLocal().getId(), getId(), $L,params)", callMethodPacket, callMethodPacket, framework, methodCodeBlock.size());
            method.addStatement("$T.getNetwork().getClient().sendPacket(methodPacket)", framework);
            DeviceMethod deviceMethod = new DeviceMethod(deviceName.toString(), methodElement.getSimpleName().toString(), parameterTypes);
            methodCodeBlock.add(deviceMethod);
            navigatorClass.addMethod(method.build());
        }

            /*
              3- Write generated class to a file
             */
        try {
            JavaFile.builder("tatanpoker.com.iotframework", navigatorClass.build()).build().writeTo(filer);
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

class DeviceMethod {
    private final String device;
    private final String method;
    private final List<String> parameters;

    DeviceMethod(String device, String method, List<String> parameters) {
        this.device = device;
        this.method = method;
        this.parameters = parameters;
    }

    public String getDevice() {
        return device;
    }

    public String getMethod() {
        return method;
    }

    public List<String> getParameters() {
        return parameters;
    }
}