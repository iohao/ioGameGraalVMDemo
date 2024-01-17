package com.iogamegraalvmdemo.gameexternaldemo.graal;

import cn.hutool.core.lang.ClassScanner;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.iohao.game.action.skeleton.protocol.BarMessage;
import com.iohao.game.action.skeleton.protocol.HeadMetadata;
import com.iohao.game.action.skeleton.protocol.RequestMessage;
import com.iohao.game.action.skeleton.protocol.ResponseMessage;
import com.iohao.game.action.skeleton.protocol.external.RequestCollectExternalMessage;
import com.iohao.game.action.skeleton.protocol.external.ResponseCollectExternalItemMessage;
import com.iohao.game.action.skeleton.protocol.external.ResponseCollectExternalMessage;
import com.iohao.game.bolt.broker.client.external.bootstrap.handler.ExternalBizHandler;
import com.iohao.game.bolt.broker.client.external.bootstrap.handler.IdleHandler;
import com.iohao.game.bolt.broker.core.client.BrokerClientType;
import com.iohao.game.bolt.broker.core.message.*;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.util.CollectionUtils;

import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * native模式下，ioGame框架中的相关类编译注册
 * Author: shenjk
 * date   2024-01-17
 */
@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(GameNativeConfiguration.GameNativeRuntimeHintsRegistrar.class)
public class GameNativeConfiguration {

    static class GameNativeRuntimeHintsRegistrar implements RuntimeHintsRegistrar {


        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            Stream.of(
                            //Netty
                            SelectorProvider.class
                            //Game
                            , cn.hutool.system.OsInfo.class
                            , org.jctools.maps.NonBlockingHashMap.class
                            , org.jctools.maps.NonBlockingHashMapLong.class
                            , org.jctools.maps.NonBlockingSetInt.class
                            , com.alipay.remoting.rpc.HeartbeatHandler.class
                            , RequestBrokerClientModuleMessage.class
                            , HeadMetadata.class
                            , BarMessage.class
                            , RequestMessage.class
                            , ResponseMessage.class
                            , BrokerClientItemConnectMessage.class
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

                            , IdleHandler.class
                            , ExternalBizHandler.class

                    )
                    .forEach(x -> hints.reflection().registerType(x, MemberCategory.values()));

            Stream.of(
                    "com.iohao.game.bolt.broker.client.external.session.UserSessions$Holder"
                    , "org.jctools.maps.NonBlockingSetInt$NBSI"
            ).forEach(className -> {
                try {
                    Class<?> clz = Class.forName(className);
                    hints.reflection().registerType(clz, MemberCategory.values());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
            //注册protobuf
            ProtobufClassRuntimeUtils.registerProtobufType(hints);
        }
    }

    /**
     * native模式时，将proto类编译注册
     */
    static class ProtobufClassRuntimeUtils {
        private static void registerProtobufType(RuntimeHints hints) {

            List<Class<?>> list = new ArrayList<>();
            Stream.of(
                    "com.iohao.game"
            ).forEach(packageName -> {
                Set<Class<?>> protoClass = ClassScanner.scanAllPackage(packageName, clz -> {
                    if (clz.getAnnotation(ProtobufClass.class) != null) {
                        try {
                            if (clz.getName().startsWith("com.iohao.game")) {
                                if (!clz.getName().startsWith("com.iohao.game.bolt.broker.client.external.bootstrap.message")
                                        && !clz.getName().startsWith("com.iohao.game.action.skeleton.protocol.wrapper")
                                ) {
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
}
