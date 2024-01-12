package org.digitalsmile.eink.controllers;

import org.digitalsmile.eink.DisplayType;
import org.digitalsmile.eink.DisplayBufferHolder;

import java.io.IOException;

public interface WaveShareDisplay {

    DisplayType getDisplayType();

    void initialize() throws IOException, InterruptedException;

    void hardReset() throws IOException, InterruptedException;

    void softReset() throws IOException;

    void sleep() throws IOException;

    void busyWait() throws IOException;

    void powerOff() throws IOException;

    void sendCommand(int command) throws IOException;

    void sendData(int data) throws IOException;

    void show(DisplayBufferHolder bufferHolder) throws IOException;

    void clearDisplay() throws IOException;
}
