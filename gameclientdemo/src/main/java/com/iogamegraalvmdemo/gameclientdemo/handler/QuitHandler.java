package com.iogamegraalvmdemo.gameclientdemo.handler;

import com.iogamegraalvmdemo.gameclientdemo.utils.CmdUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理退出游戏消息
 * Author: shenjk
 * date   2024-01-17
 */
@Slf4j
public class QuitHandler extends Handler {
    @Override
    public int getCmd() {
        return CmdUtils.CMD;
    }

    @Override
    public int getSubCmd() {
        return CmdUtils.QUIT;
    }

    @Override
    public void handle(byte[] data) {
        log.info("退出成功");
    }
}
