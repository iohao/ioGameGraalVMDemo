package com.iogamegraalvmdemo.gameserverdemo.pb;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@ProtobufClass
public class LoginReqPb implements Serializable {

    @NotNull
    private String username;

    private String password;
}
