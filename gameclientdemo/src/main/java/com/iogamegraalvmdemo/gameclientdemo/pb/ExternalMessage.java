package com.iogamegraalvmdemo.gameclientdemo.pb;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.iogamegraalvmdemo.gameclientdemo.utils.CmdKit;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@ProtobufClass
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExternalMessage {
    /** 请求命令类型: 0 心跳，1 业务 */
    @Protobuf(fieldType = FieldType.INT32, order = 1)
    int cmdCode;
    /** 协议开关，用于一些协议级别的开关控制，比如 安全加密校验等。 : 0 不校验 */
    @Protobuf(fieldType = FieldType.INT32, order = 2)
    int protocolSwitch;
    /** 业务路由（高16为主, 低16为子） */
    @Protobuf(fieldType = FieldType.INT32, order = 3)
    int cmdMerge;

    /**
     * 响应码。
     * <pre>
     *     从字段精简的角度，我们不可能每次响应都带上完整的异常信息给客户端排查问题，
     *     因此，我们会定义一些响应码，通过编号进行网络传输，方便客户端定位问题。
     *
     *     0:成功
     *     !=0: 表示有错误
     * </pre>
     */
    @Protobuf(fieldType = FieldType.SINT32, order = 4)
    int responseStatus;
    /** 验证信息（错误消息、异常消息） */
    @Protobuf(fieldType = FieldType.STRING, order = 5)
    String validMsg;
    @Protobuf(fieldType = FieldType.BYTES, order = 6)
    byte[] data;

    /**
     * 业务数据
     *
     * @param data 业务数据
     */
    public void setData(byte[] data) {
        if (data != null) {
            this.data = data;
        }
    }

    /**
     * 业务路由
     *
     * @param cmd    主路由
     * @param subCmd 子路由
     */
    public void setCmdMerge(int cmd, int subCmd) {
        this.cmdMerge = CmdKit.merge(cmd, subCmd);
    }
}