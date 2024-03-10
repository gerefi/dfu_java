package com.gerefi.dfu;

import java.io.IOException;

import com.gerefi.dfu.DfuImage;

public class DfuReaderSandbox {
    public static void main(String[] args) throws IOException {
        DfuImage dfuImage = new DfuImage();
        dfuImage.read("gerefi.dfu");
    }
}
