package com.gerefi.dfu;

import com.gerefi.dfu.commands.*;

import java.nio.ByteBuffer;
import java.util.List;

public class DfuLogic {
    public static final short ST_VENDOR = 0x0483;
    public static final int ST_DFU_PRODUCT = 0xdf11;
    public static final int USB_CLASS_APP_SPECIFIC = 0xfe;
    public static final byte DFU_SUBCLASS = 0x01;
    public static final byte USB_DT_DFU = 0x21;
    public static final String FLASH_TAG = "Flash";

    public static void uploadImage(Logger logger, DfuConnection device, BinaryImage image, FlashRange range) {
        DfuLogic.startup(logger, device);
        actuallyUploadImage(logger, device, image, range);
        DfuLogic.leaveDFU(logger, device);
    }

    public static void actuallyUploadImage(Logger logger, DfuConnection device, BinaryImage image, FlashRange range) {
        long enter = System.currentTimeMillis();
        List<Integer> erasePages = range.pagesForSize(image.getImageSize());
        // todo: smarted start address logic
        int eraseAddress = 0x08000000;
        for (Integer erasePage : erasePages) {
            DfuSeCommandErasePage.execute(logger, device, eraseAddress);
            eraseAddress += erasePage;
        }
        logger.info(String.format("Erased up to %x", eraseAddress));

        for (int offset = 0; offset < image.getImageSize(); offset += device.getTransferSize()) {
            DfuSeCommandSetAddress.execute(logger, device, device.getFlashRange().getBaseAddress() + offset);
            DfuConnectionUtil.waitStatus(logger, device);

            ByteBuffer buffer = device.allocateBuffer(device.getTransferSize());
            // last transfer would usually be smaller than transfer size
            int size = Math.min(device.getTransferSize(), image.getImage().length - offset);
            buffer.put(image.getImage(), offset, size);
            device.sendData(DfuCommmand.DNLOAD, DfuSeCommand.W_DNLOAD, buffer);
            // AN3156 USB DFU protocol used in the STM32 bootloader
            // "The Write memory operation is effectively executed only when a DFU_GETSTATUS request is issued by the host. "
            DfuConnectionUtil.waitStatus(logger, device);
        }
        logger.info("Uploaded " + image.getImage().length + " bytes in " + (System.currentTimeMillis() - enter) + " ms");
    }

    public static void leaveDFU(Logger logger, DfuConnection device) {
        logger.info("Leaving DFU");
        device.sendData(DfuCommmand.DNLOAD, DfuSeCommand.W_DNLOAD, device.allocateBuffer(0));
        // The DFU Leave operation is effectively executed only when a DFU_GETSTATUS request is
        // issued by the host.
        DfuConnectionUtil.waitStatus(logger, device);
        logger.info("DONE");
    }

    public static void startup(Logger logger, DfuConnection device) {
        DfuCommandGetStatus.DeviceStatus state = DfuCommandGetStatus.read(logger, device);
        logger.info("DFU state: " + state);
        switch (state.getState()) {
            case DFU_IDLE:
                // best status
                logger.info("startup status " + state.getStatus());
                break;
            case DFU_ERROR:
                DfuCommandClearStatus.execute(device);
                break;
            case DFU_DOWNLOAD_SYNC:
            case DFU_DOWNLOAD_IDLE:
            case DFU_UPLOAD_IDLE:
            case DFU_MANIFEST_SYNC:
            case DFU_DOWNLOAD_BUSY:
            case DFU_MANIFEST:
                DfuCommandAbort.execute(device);
                break;
            default:
                throw new IllegalStateException("Unexpected state " + state);
        }
        state = DfuCommandGetStatus.read(logger, device);
        if (state.getState() != DfuCommandGetStatus.State.DFU_IDLE)
            throw new IllegalStateException("Not idle on start-up: " + state.getState());
    }

    public interface Logger {
        Logger VOID = message -> {
        };

        Logger CONSOLE = System.out::println;

        void info(String message);
    }
}
