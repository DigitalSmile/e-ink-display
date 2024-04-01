package org.digitalsmile.eink.controllers;

import org.digitalsmile.eink.DisplayType;
import org.digitalsmile.eink.DisplayBufferHolder;
import org.digitalsmile.gpio.core.exception.NativeException;

import java.io.IOException;

public interface WaveShareDisplay {

    DisplayType getDisplayType();

    void initialize() throws IOException, InterruptedException, NativeException;

    void hardReset() throws IOException, InterruptedException, NativeException;

    void softReset() throws IOException, NativeException;

    void sleep() throws IOException, NativeException;

    void busyWait() throws IOException, NativeException;

    void powerOff() throws IOException, NativeException;

    void sendCommand(int command) throws IOException, NativeException;

    void sendData(int data) throws IOException, NativeException;

    void show(DisplayBufferHolder bufferHolder) throws IOException, NativeException;

    void clearDisplay() throws IOException, NativeException;
}
