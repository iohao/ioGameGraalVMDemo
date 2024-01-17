package com.iogamegraalvmdemo.gameclientdemo.handler;

/**
 * 响应处理器
 * Author: shenjk
 * date   2024-01-17
 */
public abstract class Handler {
    /**
     * 主命令
     */
    public abstract int getCmd();

    /**
     * 子命令
     */
    public abstract int getSubCmd();

    /**
     * 处理收到的响应消息
     *
     * @param data
     */
    public abstract void handle(byte[] data);

}
