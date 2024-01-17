package com.iogamegraalvmdemo.gameserverdemo.pb;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Data;

import java.io.Serializable;

@ProtobufClass
@Data
public class LoginResultPb implements Serializable {

    private long userId;

    private String username;
}
