package com.gerefi.dfu;

import com.gerefi.dfu.BinaryImage;
import com.gerefi.dfu.DfuImage;
import com.gerefi.dfu.DfuLogic;
import com.gerefi.dfu.LogUtil;
import com.gerefi.dfu.usb4java.DfuDeviceLocator;
import com.gerefi.dfu.usb4java.USBDfuConnection;

import cz.jaybee.intelhex.IntelHexException;
import org.apache.commons.logging.Log;

import java.io.IOException;

public class Sandbox {
    private static final Log log = LogUtil.getLog(Sandbox.class);

    public static void main(String[] args) throws IOException, IntelHexException {
        log.info("Hello sandbox");

        DfuLogic.Logger logger = DfuLogic.Logger.CONSOLE;
        USBDfuConnection device = DfuDeviceLocator.findDevice(logger);
        if (device == null) {
            System.err.println("No DFU devices found");
            return;
        }

        //BinaryImage image = HexImage.loadHexToBuffer(new FileInputStream("gerefi.hex"), device.getFlashRange());

        BinaryImage image = new DfuImage().read("gerefi.dfu");
        DfuLogic.uploadImage(logger, device, image, device.getFlashRange());

        log.info("STM32 DFU " + device);
    }
}
