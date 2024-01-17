package com.iogamegraalvmdemo.gameclientdemo.handler;

import com.iogamegraalvmdemo.gameclientdemo.pb.BroadcastPb;
import com.iogamegraalvmdemo.gameclientdemo.utils.CmdUtils;
import com.iogamegraalvmdemo.gameclientdemo.utils.ProtoKit;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理收到的广播消息
 * Author: shenjk
 * date   2024-01-17
 * */
@Slf4j
public class BroadcastHandler extends Handler {
    @Override
    public int getCmd() {
        return CmdUtils.CMD;
    }

    @Override
    public int getSubCmd() {
        return CmdUtils.BROADCAST;
    }

    @Override
    public void handle(byte[] data) {
        BroadcastPb pb = ProtoKit.parseProtoByte(data, BroadcastPb.class);
        log.info("收到广播消息：{}", pb);
    }
}
