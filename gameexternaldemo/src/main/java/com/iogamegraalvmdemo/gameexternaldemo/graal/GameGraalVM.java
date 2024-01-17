package com.iogamegraalvmdemo.gameexternaldemo.graal;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * native模式下，设置sofa的logger
 * Author: shenjk
 * date   2024-01-17
 * */
@TargetClass(className = "com.alipay.remoting.log.BoltLoggerFactory")
final class Target_com_alipay_remoting_log_BoltLoggerFactory {
    @Substitute
    public static Logger getLogger(String name) {

        return LoggerFactory.getLogger(name);
    }
}

class GameGraalVM {
}
