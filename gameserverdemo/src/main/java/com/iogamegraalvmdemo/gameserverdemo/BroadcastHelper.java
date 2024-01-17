package com.iogamegraalvmdemo.gameserverdemo;

import com.iogamegraalvmdemo.gameserverdemo.pb.BroadcastPb;
import com.iogamegraalvmdemo.gameserverdemo.utils.CmdUtils;
import com.iohao.game.action.skeleton.annotation.DocActionSend;
import com.iohao.game.action.skeleton.annotation.DocActionSends;
import com.iohao.game.action.skeleton.core.CmdInfo;
import com.iohao.game.action.skeleton.core.commumication.BroadcastContext;
import com.iohao.game.bolt.broker.core.client.BrokerClientHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 发送广播消息
 * Author: shenjk
 * date   2024-01-17
 */
@DocActionSends(
        {
                @DocActionSend(cmd = CmdUtils.CMD, subCmd = CmdUtils.BROADCAST, dataClass = BroadcastPb.class)
        }
)
public class BroadcastHelper {

    static final CmdInfo broadcastCmd = CmdInfo.getCmdInfo(CmdUtils.CMD, CmdUtils.BROADCAST);
    static final ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1);

    public static void broadcast(long userId, BroadcastPb pb) {
        //延迟发生,仅仅为了测试才延迟的
        executorService.schedule(() -> {
            BroadcastContext broadcastContext = BrokerClientHelper.me().getBroadcastContext();
            broadcastContext.broadcast(broadcastCmd, pb, userId);
        }, 1, TimeUnit.SECONDS);
    }
}
