package com.gerefi.dfu.commands;

import com.gerefi.dfu.DfuCommmand;
import com.gerefi.dfu.DfuConnection;
import com.gerefi.dfu.DfuLogic;
import com.gerefi.dfu.DfuSeCommand;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DfuSeCommandSetAddress {
    public static void execute(DfuLogic.Logger logger, DfuConnection connection, int address) {
        logger.info(String.format("SetAddress %x", address));
        ByteBuffer buffer = createSpecialCommandBuffer(connection, DfuSeCommand.SE_SET_ADDRESS, address);
        connection.sendData(DfuCommmand.DNLOAD, DfuSeCommand.W_SPECIAL, buffer);
    }

    protected static ByteBuffer createSpecialCommandBuffer(DfuConnection connection, byte command, int address) {
        ByteBuffer buffer = createBuffer(connection, 5);
        buffer.put(command);
//        buffer.rewind();
//        byte[] t = new byte[4];
//        buffer.get(t);
        buffer.putInt(address);
        return buffer;
    }

    protected static ByteBuffer createBuffer(DfuConnection connection, int capacity) {
        return connection.allocateBuffer(capacity).order(ByteOrder.LITTLE_ENDIAN);
    }
}
