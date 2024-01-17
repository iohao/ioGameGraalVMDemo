package com.iogamegraalvmdemo.gameserverdemo.graal;

import org.graalvm.nativeimage.hosted.Feature;

/**
 * buildArgs中增加
 * &lt;buildArg>--features=com.iogamegraalvmdemo.gameserverdemo.graal.GameRuntimeHintsFeature&lt;/buildArg>
 * 确保相关的MethodAccess类被注册
 * Author: shenjk
 * date   2024-01-17
 */
public class GameRuntimeHintsFeature implements Feature {
    @Override
    public void duringSetup(DuringSetupAccess access) {

    }

    @Override
    public void afterImageWrite(AfterImageWriteAccess access) {
        Feature.super.afterImageWrite(access);
    }

    @Override
    public void cleanup() {
        ActionControllerRuntimeUtils.cleanup();
    }
}
