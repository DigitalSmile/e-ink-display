package org.digitalsmile.eink.controllers;

import org.digitalsmile.eink.DisplayBufferHolder;
import org.digitalsmile.eink.DisplayType;
import org.digitalsmile.gpio.NativeMemoryException;

import java.io.IOException;

public interface WaveShareDisplay {

    DisplayType getDisplayType();

    void initialize() throws IOException, InterruptedException, NativeMemoryException;

    void hardReset() throws IOException, InterruptedException, NativeMemoryException;

    void softReset() throws IOException, NativeMemoryException;

    void sleep() throws IOException, NativeMemoryException;

    void busyWait() throws IOException, NativeMemoryException;

    void powerOff() throws IOException, NativeMemoryException;

    void sendCommand(int command) throws IOException, NativeMemoryException;

    void sendData(int data) throws IOException, NativeMemoryException;

    void show(DisplayBufferHolder bufferHolder) throws IOException, NativeMemoryException;

    void clearDisplay() throws IOException, NativeMemoryException;
}
