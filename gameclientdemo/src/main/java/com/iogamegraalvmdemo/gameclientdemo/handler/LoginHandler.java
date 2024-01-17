package com.iogamegraalvmdemo.gameclientdemo.handler;

import com.iogamegraalvmdemo.gameclientdemo.pb.LoginResultPb;
import com.iogamegraalvmdemo.gameclientdemo.utils.CmdUtils;
import com.iogamegraalvmdemo.gameclientdemo.utils.ProtoKit;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理收到的登录信息
 * Author: shenjk
 * date   2024-01-17
 * */
@Slf4j
public class LoginHandler extends Handler {
    @Override
    public int getCmd() {
        return CmdUtils.CMD;
    }

    @Override
    public int getSubCmd() {
        return CmdUtils.LOGIN;
    }

    @Override
    public void handle(byte[] data) {
        LoginResultPb loginResultPb = ProtoKit.parseProtoByte(data, LoginResultPb.class);
        log.info("登录结果信息:{}", loginResultPb);
    }

}
