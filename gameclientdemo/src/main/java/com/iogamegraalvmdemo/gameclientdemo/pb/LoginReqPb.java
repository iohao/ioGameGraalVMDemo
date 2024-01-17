package com.iogamegraalvmdemo.gameclientdemo.pb;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Data;

import java.io.Serializable;

@Data
@ProtobufClass
public class LoginReqPb implements Serializable {

    private String username;

    private String password;
}
