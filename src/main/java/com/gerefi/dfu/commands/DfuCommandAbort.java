package com.gerefi.dfu.commands;

import com.gerefi.dfu.DfuCommmand;
import com.gerefi.dfu.DfuConnection;

import java.nio.ByteBuffer;

public class DfuCommandAbort {
    public static void execute(DfuConnection connection) {
        ByteBuffer buffer = connection.allocateBuffer(0);
        connection.sendData(DfuCommmand.ABORT, (short) 0, buffer);
    }
}
