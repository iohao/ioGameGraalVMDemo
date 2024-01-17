package com.iogamegraalvmdemo.gameclientdemo.pb;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
@ProtobufClass
public class BroadcastPb implements Serializable {

    private String message;

    private Date sendTime;
}
