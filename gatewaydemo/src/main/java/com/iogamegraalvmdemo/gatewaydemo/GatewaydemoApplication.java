package com.iogamegraalvmdemo.gatewaydemo;

import com.iohao.game.bolt.broker.cluster.BrokerCluster;
import com.iohao.game.bolt.broker.cluster.BrokerClusterManagerBuilder;
import com.iohao.game.bolt.broker.core.common.BrokerGlobalConfig;
import com.iohao.game.bolt.broker.server.BrokerServer;
import com.iohao.game.bolt.broker.server.BrokerServerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;

@SpringBootApplication
public class GatewaydemoApplication {

    public static void main(String[] args) {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        SpringApplication.run(GatewaydemoApplication.class, args);
        startBrokerServer();
        System.out.println("启动耗时:"
                + (System.currentTimeMillis() - runtimeMXBean.getStartTime()) +" ms");

    }

    private static void startBrokerServer() {
        BrokerGlobalConfig.externalLog = true;
        // broker （游戏网关） 构建器
        BrokerClusterManagerBuilder brokerClusterManagerBuilder = BrokerCluster.newBrokerClusterManagerBuilder()
                .gossipListenPort(30056)
                .seedAddress(Arrays.asList("127.0.0.1:30056"));
        BrokerServerBuilder brokerServerBuilder = BrokerServer.newBuilder()
                .brokerClusterManagerBuilder(brokerClusterManagerBuilder)
                .port(10200);

        BrokerServer brokerServer = brokerServerBuilder.build();
        brokerServer.startup();
    }
}
