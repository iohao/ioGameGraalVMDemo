package com.iogamegraalvmdemo.gameexternaldemo;

import com.iohao.game.bolt.broker.client.external.ExternalServer;
import com.iohao.game.bolt.broker.client.external.bootstrap.ExternalJoinEnum;
import com.iohao.game.bolt.broker.client.external.bootstrap.heart.IdleProcessSetting;
import com.iohao.game.bolt.broker.client.external.config.ExternalGlobalConfig;
import com.iohao.game.bolt.broker.core.client.BrokerAddress;
import com.iohao.game.bolt.broker.core.common.BrokerGlobalConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

@SpringBootApplication
public class GameexternaldemoApplication {

    public static void main(String[] args) {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        SpringApplication.run(GameexternaldemoApplication.class, args);
        startExternalServer();
        System.out.println("启动耗时:"
                + (System.currentTimeMillis() - runtimeMXBean.getStartTime()) + " ms");
    }

    /**
     * 启动对外服务
     */
    private static void startExternalServer() {
        ExternalGlobalConfig.accessAuthenticationHook.setVerifyIdentity(true);
        ExternalGlobalConfig.accessAuthenticationHook.addIgnoreAuthenticationCmd(1, 1000);
        ExternalGlobalConfig.accessAuthenticationHook.addIgnoreAuthenticationCmd(1, 1001);

        BrokerGlobalConfig.externalLog = true;
        int idleTime = 20;
        IdleProcessSetting idleProcessSetting = new IdleProcessSetting()
                // 设置 自定义心跳钩子事件回调
                .idleTime(idleTime)
                .allIdleTime(idleTime)
                .readerIdleTime(idleTime)
                .writerIdleTime(idleTime)
                .idleHook(new DemoIdleHook());
//        // 游戏对外服 - 构建器，设置并构建

        ExternalServer externalServer = ExternalServer.newBuilder(10100)
                .externalJoinEnum(ExternalJoinEnum.WEBSOCKET)
                // 开启心跳机制
                .enableIdle(idleProcessSetting)
                .brokerAddress(new BrokerAddress("127.0.0.1", 10200))
                // 构建对外服
                .build();
        externalServer.startup();
    }
}
