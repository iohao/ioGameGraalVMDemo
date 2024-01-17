package com.iogamegraalvmdemo.gatewaydemo.graal;

import com.iohao.game.action.skeleton.protocol.BarMessage;
import com.iohao.game.action.skeleton.protocol.HeadMetadata;
import com.iohao.game.action.skeleton.protocol.RequestMessage;
import com.iohao.game.action.skeleton.protocol.ResponseMessage;
import com.iohao.game.action.skeleton.protocol.external.RequestCollectExternalMessage;
import com.iohao.game.action.skeleton.protocol.external.ResponseCollectExternalItemMessage;
import com.iohao.game.action.skeleton.protocol.external.ResponseCollectExternalMessage;
import com.iohao.game.bolt.broker.core.client.BrokerClientType;
import com.iohao.game.bolt.broker.core.message.*;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

import java.nio.channels.spi.SelectorProvider;
import java.util.stream.Stream;

/**
 * native模式下，ioGame框架中的相关类编译注册
 * Author: shenjk
 * date   2024-01-17
 */
@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(GateWayNativeConfiguration.GateWayNativeRuntimeHintsRegistrar.class)
public class GateWayNativeConfiguration {

    static class GateWayNativeRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            Stream.of(
                            //Netty
                            SelectorProvider.class
                            , org.jctools.maps.NonBlockingHashMap.class
                            , BrokerClusterMessage.class
                            , InnerModuleMessage.class
                            , RequestBrokerClientModuleMessage.class
                            , SettingUserIdMessage.class
                            , SettingUserIdMessageResponse.class
                            , BrokerClientType.class
                            , BroadcastOrderMessage.class
                            , BroadcastMessage.class
                            , BrokerClientItemConnectMessage.class
                            , BrokerClientModuleMessage.class
                            , ResponseCollectExternalItemMessage.class
                            , ResponseCollectExternalMessage.class
                            , RequestCollectExternalMessage.class
                            , ResponseMessage.class
                            , RequestMessage.class
                            , BarMessage.class
                            , HeadMetadata.class
                            , BrokerMessage.class
                    )
                    .forEach(x -> hints.reflection().registerType(x, MemberCategory.values()));
        }
    }
}
