package com.iogamegraalvmdemo.gameclientdemo;

import com.iogamegraalvmdemo.gameclientdemo.handler.Handler;
import com.iogamegraalvmdemo.gameclientdemo.pb.ExternalMessage;
import com.iogamegraalvmdemo.gameclientdemo.utils.CmdKit;
import com.iogamegraalvmdemo.gameclientdemo.utils.ProtoKit;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.*;

/**
 * 实现websocket连接
 * Author: shenjk
 * date   2024-01-17
 */
@Slf4j
public class GameWebSocketClient extends WebSocketClient {
    private final Map<Integer, List<Handler>> handlers = new HashMap<>();

    ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    public GameWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    public void addHandler(Handler handler) {
        int mergeCmd = CmdKit.merge(handler.getCmd(), handler.getSubCmd());
        if (handlers.containsKey(mergeCmd)) {
            handlers.get(mergeCmd).add(handler);
        } else {
            handlers.put(mergeCmd, Arrays.asList(handler));
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("连接成功");
        executorService.scheduleAtFixedRate(() -> {
            sendHeartbeat();
        }, 5, 5, TimeUnit.SECONDS);
    }

    private static byte[] heartbeatData = ProtoKit.toBytes(new ExternalMessage());

    private void sendHeartbeat() {
        send(heartbeatData);
    }

    @Override
    public void onMessage(String s) {

    }

    @Override
    public void onMessage(ByteBuffer byteBuffer) {
        // 接收服务器返回的消息
        byte[] dataContent = byteBuffer.array();
        ExternalMessage message = ProtoKit.parseProtoByte(dataContent, ExternalMessage.class);
        if (message.getCmdCode() == 0) {
            //心跳信息
            return;
        }
        int cmd = CmdKit.getCmd(message.getCmdMerge());
        int subCmd = CmdKit.getSubCmd(message.getCmdMerge());
        log.info("收到消息{},{} ExternalMessage ========== ", cmd, subCmd);
        if (message.getResponseStatus() != 0) {
            log.error("cmd:{}-subCmd:{},异常:{}", cmd, subCmd, message.getValidMsg());
            return;
        }
        byte[] data = message.getData();
        List<Handler> list = handlers.getOrDefault(message.getCmdMerge(), null);
        if (!CollectionUtils.isEmpty(list)) {
            list.stream().parallel().forEach(handler -> handler.handle(data));
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log.warn("连接关闭");
        executorService.shutdown();
    }

    @Override
    public void onError(Exception e) {
        log.error("连接异常", e);
    }
}
