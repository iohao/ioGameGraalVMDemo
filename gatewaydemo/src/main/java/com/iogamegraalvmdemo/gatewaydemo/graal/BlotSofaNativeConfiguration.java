package com.iogamegraalvmdemo.gatewaydemo.graal;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

import java.util.stream.Stream;

/**
 * native模式下，sofa相关的类编译
 * Author: shenjk
 * date   2024-01-17
 */
@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(BlotSofaNativeConfiguration.BlotSofaNativeRuntimeHintsRegistrar.class)
public class BlotSofaNativeConfiguration {

    static class BlotSofaNativeRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            Stream.of(com.alipay.remoting.rpc.protocol.RpcRequestCommand.class
                            , com.alipay.remoting.rpc.protocol.RpcProtocolDecoder.class
                            , com.alipay.remoting.ServerIdleHandler.class
                            , com.alipay.remoting.rpc.RpcConnectionEventHandler.class
                            , com.alipay.remoting.rpc.RpcHandler.class
                            , com.caucho.hessian.io.UnsafeDeserializer.class
                            , java.sql.Date.class
                            , java.sql.Time.class
                            , java.sql.Timestamp.class
                    )
                    .forEach(x -> hints.reflection().registerType(x, MemberCategory.values()));

        }
    }
}
