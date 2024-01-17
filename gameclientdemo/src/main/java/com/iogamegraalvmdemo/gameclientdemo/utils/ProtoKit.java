package com.iogamegraalvmdemo.gameclientdemo.utils;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class ProtoKit {
    private static final Logger log = LoggerFactory.getLogger(ProtoKit.class);
    static final byte[] emptyBytes = new byte[0];

    public static byte[] toBytes(Object data) {
        if (Objects.isNull(data)) {
            return emptyBytes;
        } else {
            Class clazz = data.getClass();
            Codec<Object> codec = ProtobufProxy.create(clazz);

            try {
                return codec.encode(data);
            } catch (Throwable var4) {
                log.error(var4.getMessage(), var4);
                return emptyBytes;
            }
        }
    }

    public static <T> T parseProtoByte(byte[] data, Class<T> clazz) {
        if (Objects.isNull(data)) {
            return null;
        } else {
            Codec<T> codec = ProtobufProxy.create(clazz);

            try {
                return codec.decode(data);
            } catch (Throwable var4) {
                log.error(var4.getMessage(), var4);
                return null;
            }
        }
    }

    private ProtoKit() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}