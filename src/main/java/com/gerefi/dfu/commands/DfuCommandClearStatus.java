package com.gerefi.dfu.commands;

import com.gerefi.dfu.DfuCommmand;
import com.gerefi.dfu.DfuConnection;

import java.nio.ByteBuffer;

public class DfuCommandClearStatus {
    public static void execute(DfuConnection session) {
        ByteBuffer buffer = session.allocateBuffer(0);
        session.sendData(DfuCommmand.CLRSTATUS, (short) 0, buffer);
    }
}
