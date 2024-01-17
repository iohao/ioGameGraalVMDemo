package com.iogamegraalvmdemo.gameclientdemo;

import com.iogamegraalvmdemo.gameclientdemo.pb.LoginReqPb;
import com.iogamegraalvmdemo.gameclientdemo.utils.CmdUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
public class GameclientdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameclientdemoApplication.class, args);

        GameWebSocketClient gameWebSocketClient = new GameWebSocketClient(URI.create("ws://127.0.0.1:10100/websocket"));
        gameWebSocketClient.connect();

        GameLogicService gameLogicService = new GameLogicService(gameWebSocketClient);
        try {
            LoginReqPb loginReqPb = new LoginReqPb();
            //测试@NotNull是否生效
            gameLogicService.sendRequest(CmdUtils.CMD, CmdUtils.LOGIN, loginReqPb);

            TimeUnit.SECONDS.sleep(10);
            //测试是否能登录成功
            loginReqPb.setUsername("test");
            loginReqPb.setPassword("test");
            gameLogicService.sendRequest(CmdUtils.CMD, CmdUtils.LOGIN, loginReqPb);

            TimeUnit.SECONDS.sleep(10);
            gameLogicService.sendRequest(CmdUtils.CMD, CmdUtils.QUIT);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
