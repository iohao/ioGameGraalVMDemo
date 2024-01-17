package com.iogamegraalvmdemo.gameserverdemo.graal;

import cn.hutool.core.lang.ClassScanner;
import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.iohao.game.action.skeleton.annotation.ActionController;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;

/**
 * 注册ActionController及其对应的MethodAccess和ConstructorAccess类
 * Author: shenjk
 * date   2024-01-17
 */
public class ActionControllerRuntimeUtils {
    public static void registerProtobufType(RuntimeHints hints) {

        ClassScanner.scanAllPackage("com.iogamegraalvmdemo", clz -> {
            if (clz.getAnnotation(ActionController.class) != null) {

                ConstructorAccess constructorAccess = ConstructorAccess.get(clz);
                MethodAccess methodAccess = MethodAccess.get(clz);
                MethodAccessUtils.generate(clz);
                ConstructorAccessUtils.generate(clz);

                hints.reflection().registerType(clz, MemberCategory.values());
                hints.reflection().registerType(constructorAccess.getClass(), MemberCategory.values());
                hints.reflection().registerType(methodAccess.getClass(), MemberCategory.values());

            }
            return false;
        });
    }

    public static void cleanup() {
        ClassScanner.scanAllPackage("com.iogamegraalvmdemo", clz -> {
            if (clz.getAnnotation(ActionController.class) != null) {
                MethodAccessUtils.cleanup(clz);
                ConstructorAccessUtils.cleanup(clz);
            }
            return false;
        });
    }

    static class AccessClassFileInfo {
        private String packageName;
        private String accessClassName;

        private File file;

        private AccessClassFileInfo(Class<?> clazz, String accessName) {
            this.packageName = clazz.getPackageName();
            this.accessClassName = clazz.getSimpleName() + accessName;

            this.file = Paths.get(System.getProperty("user.dir"), "src/main/java",
                    this.packageName.replace(".", "/"),
                    accessClassName + ".java"
            ).toFile();

        }

        private void cleanup() {
            if (file != null && file.exists()) {
                file.delete();
            }
        }

        private void saveFile(StringBuffer stringBuffer) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] bytes = stringBuffer.toString().getBytes();
                fos.write(bytes);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class MethodAccessUtils {
        private static final String accessName = "MethodAccess";

        public static void generate(Class<?> clazz) {
            AccessClassFileInfo accessClassFileInfo = new AccessClassFileInfo(clazz, accessName);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("package " + accessClassFileInfo.packageName + ";\n");
            stringBuffer.append("import com.esotericsoftware.reflectasm.MethodAccess;\n");
            stringBuffer.append("import java.lang.reflect.Method;\n");

            stringBuffer.append("public class " + accessClassFileInfo.accessClassName + " extends MethodAccess {\n");
            stringBuffer.append("\t@Override\n");
            stringBuffer.append("\tpublic Object invoke(Object object, int methodIndex, Object... args) {\n");
            stringBuffer.append("\t\tString methodName = getMethodNames()[methodIndex];\n");
            stringBuffer.append("\t\ttry {\n");
            stringBuffer.append("\t\t\tClass<?>[] parameterTypes = getParameterTypes()[methodIndex];\n");
            stringBuffer.append("\t\t\tMethod method = object.getClass().getDeclaredMethod(methodName, parameterTypes);\n");
            stringBuffer.append("\t\t\tmethod.setAccessible(true);\n");
            stringBuffer.append("\t\t\treturn method.invoke(object, args);\n");
            stringBuffer.append("\t\t} catch (Exception e) {\n");
            stringBuffer.append("\t\t\tthrow new RuntimeException(e);\n");
            stringBuffer.append("\t\t}\n");
            stringBuffer.append("\t}\n");
            stringBuffer.append("}");

            accessClassFileInfo.saveFile(stringBuffer);
        }

        public static void cleanup(Class<?> clazz) {
            new AccessClassFileInfo(clazz, accessName).cleanup();
        }
    }

    static class ConstructorAccessUtils {
        private static final String accessName = "ConstructorAccess";

        public static void cleanup(Class<?> clazz) {
            new AccessClassFileInfo(clazz, accessName).cleanup();
        }

        public static void generate(Class<?> clazz) {
            AccessClassFileInfo accessClassFileInfo = new AccessClassFileInfo(clazz, accessName);

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("package " + accessClassFileInfo.packageName + ";\n");
            stringBuffer.append("import com.esotericsoftware.reflectasm.ConstructorAccess;\n");

            stringBuffer.append("public class " + accessClassFileInfo.accessClassName + " extends ConstructorAccess {\n");
            stringBuffer.append("\t@Override\n");
            stringBuffer.append("\tpublic Object newInstance() {\n");
            stringBuffer.append("\t\treturn new " + clazz.getSimpleName() + "();\n");
            stringBuffer.append("\t}\n");
            stringBuffer.append("\t@Override\n");
            stringBuffer.append("\tpublic Object newInstance(Object enclosingInstance) {\n");
            stringBuffer.append("\t\treturn new " + clazz.getSimpleName() + "();\n");
            stringBuffer.append("\t}\n");
            stringBuffer.append("}\n");

            accessClassFileInfo.saveFile(stringBuffer);
        }
    }

}