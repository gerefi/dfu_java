package com.gerefi.dfu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogUtil {
    // todo: one day I would love to learn how to enable trace level with commons logging + java logging

    public static Log getLog(Class<?> clazz) {
        return LogFactory.getLog(clazz);
    }
}
