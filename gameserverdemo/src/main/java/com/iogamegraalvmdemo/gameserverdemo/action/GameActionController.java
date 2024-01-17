package com.iogamegraalvmdemo.gameserverdemo.action;

import com.iogamegraalvmdemo.gameserverdemo.BroadcastHelper;
import com.iogamegraalvmdemo.gameserverdemo.pb.BroadcastPb;
import com.iogamegraalvmdemo.gameserverdemo.pb.LoginReqPb;
import com.iogamegraalvmdemo.gameserverdemo.pb.LoginResultPb;
import com.iogamegraalvmdemo.gameserverdemo.utils.CmdUtils;
import com.iohao.game.action.skeleton.annotation.ActionController;
import com.iohao.game.action.skeleton.annotation.ActionMethod;
import com.iohao.game.action.skeleton.core.flow.FlowContext;
import com.iohao.game.bolt.broker.client.kit.UserIdSettingKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 游戏逻辑
 * Author: shenjk
 * date   2024-01-17
 * */
@Slf4j
@Component
@ActionController(CmdUtils.CMD)
public class GameActionController {

    @ActionMethod(CmdUtils.LOGIN)
    public LoginResultPb login(FlowContext flowContext, LoginReqPb loginReqPb) throws Exception {
        if (loginReqPb.getUsername().equals("test") && loginReqPb.getPassword().equals("test")) {
            LoginResultPb resultPb = new LoginResultPb();
            long userId = 100001;
            resultPb.setUserId(userId);
            resultPb.setUsername(loginReqPb.getUsername());
            UserIdSettingKit.settingUserId(flowContext, resultPb.getUserId());
            BroadcastPb broadcastPb = new BroadcastPb();
            broadcastPb.setMessage("测试广播消息");
            broadcastPb.setSendTime(new Date());
            BroadcastHelper.broadcast(userId, broadcastPb);
            return resultPb;
        }
        throw new Exception("用户信息不存在");
    }

    @ActionMethod(CmdUtils.QUIT)
    public long quit(FlowContext flowContext) {
        log.info("退出游戏");
        return 1;
    }
}
