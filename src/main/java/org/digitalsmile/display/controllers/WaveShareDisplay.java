package org.digitalsmile.display.controllers;

import org.digitalsmile.display.DisplayType;
import org.digitalsmile.display.DisplayBufferHolder;

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
