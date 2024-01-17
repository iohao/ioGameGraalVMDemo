package com.iogamegraalvmdemo.gameclientdemo;

import com.iogamegraalvmdemo.gameclientdemo.handler.BroadcastHandler;
import com.iogamegraalvmdemo.gameclientdemo.handler.LoginHandler;
import com.iogamegraalvmdemo.gameclientdemo.handler.QuitHandler;
import com.iogamegraalvmdemo.gameclientdemo.pb.ExternalMessage;
import com.iogamegraalvmdemo.gameclientdemo.utils.ProtoKit;
import lombok.extern.slf4j.Slf4j;

/**
 * 游戏逻辑服务
 * Author: shenjk
 * date   2024-01-17
 * */
@Slf4j
public class GameLogicService {
    private final GameWebSocketClient webSocketClient;

    public GameLogicService(GameWebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
        webSocketClient.addHandler(new LoginHandler());
        webSocketClient.addHandler(new QuitHandler());
        webSocketClient.addHandler(new BroadcastHandler());
    }

    /**
     * 发送请求
     *
     * @param cmd
     * @param subCmd
     * @param requestObj
     */
    public <T> void sendRequest(int cmd, int subCmd, T requestObj) {
        ExternalMessage externalMessage = new ExternalMessage();
        // 请求命令类型: 0 心跳，1 业务
        externalMessage.setCmdCode(1);
        // 路由
        externalMessage.setCmdMerge(cmd, subCmd);
        if (requestObj != null) {
            // 业务数据
            byte[] data = ProtoKit.toBytes(requestObj);
            externalMessage.setData(data);
        }
        log.info("发送消息:{}", externalMessage);
        byte[] bytes = ProtoKit.toBytes(externalMessage);
        webSocketClient.send(bytes);
    }

    public void sendRequest(int cmd, int subCmd) {
        sendRequest(cmd, subCmd, null);
    }
}
