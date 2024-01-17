package com.iogamegraalvmdemo.gameserverdemo;

import com.iohao.game.action.skeleton.ext.spring.ActionFactoryBeanForSpring;
import com.iohao.game.bolt.broker.client.BrokerClientApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

@SpringBootApplication
public class GameserverdemoApplication {

    public static void main(String[] args) {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        SpringApplication.run(GameserverdemoApplication.class, args);
        startGame();
        System.out.println("启动耗时:"
                + (System.currentTimeMillis() - runtimeMXBean.getStartTime()) +" ms");
    }
    @Bean
    public ActionFactoryBeanForSpring actionFactoryBean() {
        // 将业务框架交给 spring 管理
        return ActionFactoryBeanForSpring.me();
    }

    private static void startGame() {
        BrokerClientApplication.start(new GameLogicServerStartup());
    }

}
