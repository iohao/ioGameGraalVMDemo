package com.iogamegraalvmdemo.gameserverdemo.graal;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.iohao.game.action.skeleton.core.doc.JavaClassDocInfo;
import com.iohao.game.common.validation.Validator;
import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * native模式下，设置sofa的logger
 * Author: shenjk
 * date   2024-01-17
 */
@TargetClass(className = "com.alipay.remoting.log.BoltLoggerFactory")
final class Target_com_alipay_remoting_log_BoltLoggerFactory {
    @Substitute
    public static Logger getLogger(String name) {

        return LoggerFactory.getLogger(name);
    }
}

/**
 * 解决native模式下ClassScanner无效的问题
 * Author: shenjk
 * date   2024-01-17
 */
@TargetClass(className = "com.iohao.game.action.skeleton.core.doc.ActionCommandDocKit")
final class Target_com_iohao_game_action_skeleton_core_doc_ActionCommandDocKit {
    @Substitute
    public static Map<String, JavaClassDocInfo> getJavaClassDocInfoMap(List<Class<?>> controllerList) {
        JavaProjectBuilder javaProjectBuilder = new JavaProjectBuilder();
        final Map<String, JavaClassDocInfo> javaClassDocInfoMap = new HashMap<>(controllerList.size());
        for (Class<?> actionClazz : controllerList) {
            String packagePath = actionClazz.getPackageName();
            for (Class<?> clazz : GameActionDocWrapper.classSet) {
                if (clazz.getPackageName().equals(packagePath)) {
                    JavaClass javaClass = javaProjectBuilder.getClassByName(clazz.getName());
                    JavaClassDocInfo javaClassDocInfo = new JavaClassDocInfo(javaClass);
                    javaClassDocInfoMap.put(javaClass.toString(), javaClassDocInfo);
                }
            }
        }
        return javaClassDocInfoMap;
    }
}

/**
 * 解决native模式下ClassScanner无效的问题
 */
@TargetClass(className = "com.iohao.game.action.skeleton.core.BarSkeletonBuilderParamConfig")
final class Target_com_iohao_game_action_skeleton_core_BarSkeletonBuilderParamConfig {
    @Substitute
    private void scanClass(final List<Class<?>> actionList
            , final Predicate<Class<?>> predicateFilter
            , final Consumer<Class<?>> actionConsumer) {
        for (Class<?> actionClazz : actionList) {
            // 扫描
            String packagePath = actionClazz.getPackageName();
            List<Class<?>> classList = GameActionDocWrapper.classSet
                    .stream().filter(new Predicate<Class<?>>() {
                        @Override
                        public boolean test(Class<?> aClass) {
                            return aClass.getPackageName().equals(packagePath) && predicateFilter.test(aClass);
                        }
                    })
                    .collect(Collectors.toList());
            // 将扫描好的 class 添加到业务框架中
            classList.forEach(actionConsumer);
        }
    }
}

/**
 * 解决native模式不支持下ConstructorAccess.get的问题
 * Author: shenjk
 * date   2024-01-17
 */
@TargetClass(className = "com.esotericsoftware.reflectasm.ConstructorAccess")
final class Target_com_esotericsoftware_reflectasm_ConstructorAccess<T> {
    @Substitute
    static public <T> ConstructorAccess<T> get(Class<T> type) {
        String className = type.getName();
        String accessClassName = className + "ConstructorAccess";
        if (accessClassName.startsWith("java.")) {
            accessClassName = "reflectasm." + accessClassName;
        }
        try {
            Class enclosingType = type.getEnclosingClass();
            boolean isNonStaticMemberClass = enclosingType != null && type.isMemberClass() && !Modifier.isStatic(type.getModifiers());
            Class<?> clazz = Class.forName(accessClassName);
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            ConstructorAccess<T> constructorAccess = (ConstructorAccess<T>) constructor.newInstance();
            Field isNonStaticMemberClassField = clazz.getSuperclass().getDeclaredField("isNonStaticMemberClass");
            isNonStaticMemberClassField.setAccessible(true);
            isNonStaticMemberClassField.set(constructorAccess, isNonStaticMemberClass);
            return constructorAccess;
        } catch (Throwable t) {
            throw new RuntimeException("Exception constructing constructor access class: " + accessClassName, t);
        }
    }
}

/**
 * 解决native模式不支持下MethodAccess.get的问题
 * Author: shenjk
 * date   2024-01-17
 */
@TargetClass(className = "com.esotericsoftware.reflectasm.MethodAccess")
final class Target_com_esotericsoftware_reflectasm_MethodAccess {
    @Substitute
    static private void addDeclaredMethodsToList(Class type, ArrayList<Method> methods) {
        Method[] declaredMethods = type.getDeclaredMethods();
        for (int i = 0, n = declaredMethods.length; i < n; i++) {
            Method method = declaredMethods[i];
            int modifiers = method.getModifiers();
            if (Modifier.isPrivate(modifiers)) {
                continue;
            }
            methods.add(method);
        }
    }

    @Substitute
    static private void recursiveAddInterfaceMethodsToList(Class interfaceType, ArrayList<Method> methods) {
        addDeclaredMethodsToList(interfaceType, methods);
        for (Class nextInterface : interfaceType.getInterfaces()) {
            recursiveAddInterfaceMethodsToList(nextInterface, methods);
        }
    }

    @Substitute
    static public MethodAccess get(Class type) {
        String className = type.getName();
        String accessClassName = className + "MethodAccess";
        if (accessClassName.startsWith("java.")) {
            accessClassName = "reflectasm." + accessClassName;
        }
        try {
            boolean isInterface = type.isInterface();
            if (!isInterface && type.getSuperclass() == null && type != Object.class) {
                throw new IllegalArgumentException("The type must not be an interface, a primitive type, or void.");
            }

            ArrayList<Method> methods = new ArrayList<Method>();
            if (!isInterface) {
                Class nextClass = type;
                while (nextClass != Object.class) {
                    addDeclaredMethodsToList(nextClass, methods);
                    nextClass = nextClass.getSuperclass();
                }
            } else {
                recursiveAddInterfaceMethodsToList(type, methods);
            }
            int n = methods.size();
            String[] methodNames = new String[n];
            Class[][] parameterTypes = new Class[n][];
            Class[] returnTypes = new Class[n];
            for (int i = 0; i < n; i++) {
                Method method = methods.get(i);
                methodNames[i] = method.getName();
                parameterTypes[i] = method.getParameterTypes();
                returnTypes[i] = method.getReturnType();
            }

            Class<?> clazz = Class.forName(accessClassName);
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            MethodAccess access = (MethodAccess) constructor.newInstance();

            Field[] fields = clazz.getSuperclass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals("methodNames")) {
                    field.setAccessible(true);
                    field.set(access, methodNames);
                } else if (field.getName().equals("parameterTypes")) {
                    field.setAccessible(true);
                    field.set(access, parameterTypes);
                } else if (field.getName().equals("returnTypes")) {
                    field.setAccessible(true);
                    field.set(access, returnTypes);
                }
            }

            return access;
        } catch (Throwable t) {
            throw new RuntimeException("Exception constructing constructor access class: " + accessClassName, t);
        }
    }
}

/**
 * 替换Validation中的getValidatorf方法
 * Author: shenjk
 * date   2024-01-17
 */
@TargetClass(className = "com.iohao.game.common.validation.Validation")
final class Target_com_iohao_game_common_validation_Validation {
    @Alias
    private static volatile Validator validator;

    @Substitute
    public static Validator getValidator() throws Exception {
        if (validator != null) {
            return validator;
        }
        validator = new com.iohao.game.common.validation.support.JakartaValidator();
        return validator;
    }
}

class GameGraalVM {

}
