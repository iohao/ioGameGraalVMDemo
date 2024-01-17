package com.iogamegraalvmdemo.gameserverdemo.graal;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.lang.ClassScanner;
import cn.hutool.system.OsInfo;
import com.alipay.remoting.rpc.HeartbeatHandler;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.iohao.game.action.skeleton.annotation.ActionController;
import com.iohao.game.action.skeleton.annotation.DocActionSends;
import com.iohao.game.action.skeleton.protocol.BarMessage;
import com.iohao.game.action.skeleton.protocol.HeadMetadata;
import com.iohao.game.action.skeleton.protocol.RequestMessage;
import com.iohao.game.action.skeleton.protocol.ResponseMessage;
import com.iohao.game.action.skeleton.protocol.external.RequestCollectExternalMessage;
import com.iohao.game.action.skeleton.protocol.external.ResponseCollectExternalItemMessage;
import com.iohao.game.action.skeleton.protocol.external.ResponseCollectExternalMessage;
import com.iohao.game.bolt.broker.core.client.BrokerClientType;
import com.iohao.game.bolt.broker.core.message.*;
import org.jctools.maps.NonBlockingHashMap;
import org.jctools.maps.NonBlockingHashMapLong;
import org.jctools.maps.NonBlockingSetInt;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.spi.SelectorProvider;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * 游戏逻辑服务在native模式编译注册相关类
 * Author: shenjk
 * date   2024-01-17
 * */
@Configuration
@ImportRuntimeHints(GameNativeConfiguration.GameNativeRuntimeHintsRegistrar.class)
public class GameNativeConfiguration {

    static class GameNativeRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            Stream.of(
                            //Netty
                            SelectorProvider.class
                            //Game
                            , OsInfo.class
                            , NonBlockingHashMap.class
                            , NonBlockingHashMapLong.class
                            , NonBlockingSetInt.class
                            , ConcurrentHashSet.class
                            , HeartbeatHandler.class

                            , RequestBrokerClientModuleMessage.class
                            , BrokerClientItemConnectMessage.class
                            , HeadMetadata.class
                            , BarMessage.class
                            , RequestMessage.class
                            , ResponseMessage.class

                            , BrokerClientModuleMessage.class
                            , BrokerClientType.class
                            , BrokerClusterMessage.class
                            , BrokerMessage.class

                            , RequestCollectExternalMessage.class
                            , ResponseCollectExternalMessage.class
                            , ResponseCollectExternalItemMessage.class
                            , BroadcastMessage.class
                            , BroadcastOrderMessage.class
                            , SettingUserIdMessage.class
                            , SettingUserIdMessageResponse.class

                            , MethodAccess.class
                            , ConstructorAccess.class
                    )
                    .forEach(x -> hints.reflection().registerType(x, MemberCategory.values()));
            ProtobufClassRuntimeUtils.registerProtobufType(hints);

            Stream.of("com.iohao.game.action.skeleton.core.ActionCommand$Builder")
                    .forEach(x -> {
                        try {
                            Class<?> clz = Class.forName(x);
                            hints.reflection().registerType(clz, MemberCategory.values());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

            ActionControllerRuntimeUtils.registerProtobufType(hints);
            GameActionDocUtils.generate();
        }
    }


    /**
     * 注册Protobuf对象
     */
    static class ProtobufClassRuntimeUtils {
        private static void registerProtobufType(RuntimeHints hints) {
            List<Class<?>> list = new ArrayList<>();
            Stream.of(
                    "com.iohao.game",
                    "com.iogamegraalvmdemo"
            ).forEach(packageName -> {
                Set<Class<?>> protoClass = ClassScanner.scanAllPackage(packageName, clz -> {
                    if (clz.getAnnotation(ProtobufClass.class) != null) {
                        try {
                            if (clz.getName().startsWith("com.iohao.game")) {
                                if (!clz.getName().startsWith("com.iohao.game.bolt.broker.client.external.bootstrap.message")
                                        && !clz.getName().startsWith("com.iohao.game.action.skeleton.protocol.wrapper")
                                ) {
                                    //排除框架中的测试类
                                    return false;
                                }
                            }
                            Class<?> clazz = Class.forName(clz.getName() + "$$JProtoBufClass");
                            list.add(clazz);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                    return false;
                });
                if (!CollectionUtils.isEmpty(protoClass)) {
                    list.addAll(protoClass);
                }
            });

            list.forEach(x -> hints.reflection().registerType(x, MemberCategory.values()));
        }

    }

    static class GameActionDocUtils {

        static void generate() {
            Set<Class<?>> classSet = ClassScanner.scanAllPackage("com.iogamegraalvmdemo", clazz -> {
                return clazz.getAnnotation(ActionController.class) != null || clazz.getAnnotation(DocActionSends.class) != null;
            });
            String packageName = "com.iogamegraalvmdemo.gameserverdemo.graal";
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("package " + packageName + ";\n");
            stringBuffer.append("import java.util.HashSet;\n");
            stringBuffer.append("import java.util.Set;\n");
            stringBuffer.append("public class GameActionDocWrapper {\n");
            stringBuffer.append("\tpublic static final Set<Class<?>> classSet = new HashSet<>(){{\n");
            if (!CollectionUtils.isEmpty(classSet)) {
                for (Class<?> clazz : classSet) {
                    stringBuffer.append("\t\tadd(" + clazz.getName() + ".class);\n");
                }
            }
            stringBuffer.append("\t}};\n");
            stringBuffer.append("}\n");

            File file = Paths.get(System.getProperty("user.dir"), "src/main/java",
                    packageName.replace(".", "/"),
                    "GameActionDocWrapper.java"
            ).toFile();

            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] bytes = stringBuffer.toString().getBytes();
                fos.write(bytes);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
