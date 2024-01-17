package com.iogamegraalvmdemo.gameserverdemo;


import com.iogamegraalvmdemo.gameserverdemo.action.GameActionController;
import com.iohao.game.action.skeleton.core.BarSkeleton;
import com.iohao.game.action.skeleton.core.BarSkeletonBuilderParamConfig;
import com.iohao.game.action.skeleton.core.flow.interal.DebugInOut;
import com.iohao.game.bolt.broker.client.AbstractBrokerClientStartup;
import com.iohao.game.bolt.broker.core.client.BrokerAddress;
import com.iohao.game.bolt.broker.core.client.BrokerClient;
import com.iohao.game.bolt.broker.core.client.BrokerClientBuilder;
import com.iohao.game.bolt.broker.core.common.processor.hook.ClientProcessorHooks;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GameLogicServerStartup extends AbstractBrokerClientStartup {

    @Override
    public BarSkeleton createBarSkeleton() {
        var config = new BarSkeletonBuilderParamConfig()
                .addActionController(GameActionController.class);
        // 业务框架构建器
        var builder = config.createBuilder();
        // 添加控制台输出插件
        builder.addInOut(new DebugInOut());
        builder.getSetting().setCmdMaxLen(32767);
        builder.getSetting().setSubCmdMaxLen(65535);
        builder.getSetting().setValidator(true);
        return builder.build();
    }

    @Override
    public BrokerClientBuilder createBrokerClientBuilder() {
        BrokerClientBuilder builder = BrokerClient.newBuilder();
        ClientProcessorHooks clientProcessorHooks = new ClientProcessorHooks();
        builder.clientProcessorHooks(clientProcessorHooks);
        builder.appName("GameLogicDemo");
        builder.tag("GameLogicDemo");
        return builder;
    }

    @Override
    public BrokerAddress createBrokerAddress() {
        // 类似 127.0.0.1 ，但这里是本机的 ip
        String localIp = "127.0.0.1";
        // broker （游戏网关）默认端口
        int brokerPort = 10200;
        return new BrokerAddress(localIp, brokerPort);
    }
}
