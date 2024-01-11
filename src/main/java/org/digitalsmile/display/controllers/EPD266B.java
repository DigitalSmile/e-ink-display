package org.digitalsmile.display.controllers;

import org.digitalsmile.display.DisplayType;
import org.digitalsmile.display.DisplayBufferHolder;
import org.digitalsmile.display.color.DisplayColors;
import org.digitalsmile.display.color.DisplayLayer;
import org.digitalsmile.gpio.GPIOBoard;
import org.digitalsmile.gpio.core.IntegerToHex;
import org.digitalsmile.gpio.pin.Pin;
import org.digitalsmile.gpio.pin.attributes.Direction;
import org.digitalsmile.gpio.pin.attributes.State;
import org.digitalsmile.gpio.spi.SPIBus;
import org.digitalsmile.gpio.spi.SPIMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EPD266B implements WaveShareDisplay {
    private static final Logger logger = LoggerFactory.getLogger(EPD266B.class);

    private static final DisplayType DISPLAY_TYPE = new DisplayType("2.66inch Module (B)",
                                                             new DisplayColors[]{DisplayColors.BLACK, DisplayColors.WHITE, DisplayColors.RED}, 152, 296);
    private static final int WIDTH = DISPLAY_TYPE.width();
    private static final int HEIGHT = DISPLAY_TYPE.height();

    private final DisplayBufferHolder clearRectBufferHolder;

    private final Pin rst;
    private final Pin busy;
    private final Pin dc;
    private final Pin pwr;

    private final SPIBus spiBus;

    public EPD266B() throws IOException {
        this.spiBus = GPIOBoard.ofSPI(0, SPIMode.MODE_0, 20_000_000);

        this.rst = GPIOBoard.ofPin(17, Direction.OUTPUT);
        this.busy = GPIOBoard.ofPin(24, Direction.INPUT);
        this.dc = GPIOBoard.ofPin(25, Direction.OUTPUT);
        this.pwr = GPIOBoard.ofPin(18, Direction.OUTPUT);
        var blackByteBuffer = new byte[WIDTH / 8 * HEIGHT];
        Arrays.fill(blackByteBuffer, (byte) 0xff);
        var redByteBuffer = new byte[WIDTH / 8 * HEIGHT];
        Arrays.fill(redByteBuffer, (byte) 0x00);
        this.clearRectBufferHolder = DisplayBufferHolder.builder().add(DisplayLayer.BLACK_AND_WHITE, blackByteBuffer)
                .add(DisplayLayer.RED_AND_WHITE, redByteBuffer).build();
    }

    @Override
    public DisplayType getDisplayType() {
        return DISPLAY_TYPE;
    }

    @Override
    public void initialize() throws IOException, InterruptedException {
        logger.info("Initializing display - {}}...", DISPLAY_TYPE.name());
        logger.debug("Set Power On");
        var timeStart = Instant.now();
        pwr.write(State.HIGH);

        logger.debug("Hardware and software reset");
        // resetting the chip - hardware (to clean everything pictured) and software (get rid of register garbage)
        hardReset();
        softReset();

        logger.debug("Configuring display...");
        // start configuration
        sendCommand(Command.DATA_ENTRY.getCommand());
        logger.debug("Set increment mode for X and Y coordinates");
        sendData(0x03);

        logger.debug("Set display dimensions to {}x{}", WIDTH, HEIGHT);
        sendCommand(Command.RAM_X_ADDRESS_START_END_POSITION.getCommand());
        sendData(0); // Start x
        sendData(((WIDTH - 1) >> 3) & 0x1F); // End x
        sendCommand(Command.RAM_Y_ADDRESS_START_END_POSITION.getCommand());
        sendData(0); // Start y
        sendData(0);
        sendData((HEIGHT - 1) & 0xFF); // End Y
        sendData(((HEIGHT - 1) >> 8) & 0x01); // End Y alignment

        logger.debug("Set RAM content options");
        sendCommand(Command.DISPLAY_UPDATE_CONTROL.getCommand());
        sendData(0x00); // RED and B/W ram option normal
        sendData(0x80); // Source Output Mode from S8 to S167

        logger.debug("Set X and Y address counters");
        sendCommand(Command.RAM_X_ADDRESS_COUNTER.getCommand());
        sendData(0);
        sendCommand(Command.RAM_Y_ADDRESS_COUNTER.getCommand());
        sendData(0);
        sendData(0);
        busyWait();
        var timeStop = Instant.now();
        logger.info("Initialization finished, took {}", formatDuration(Duration.between(timeStart, timeStop)));
    }

    @Override
    public void hardReset() throws IOException, InterruptedException {
        var timeStart = Instant.now();
        /*
          Hard reset the chip.
          RST LOW -> WAIT 200ns -> RST HIGH -> WAIT 200ns -> WAIT BUSY
          According to specification, the delay should be at least 200ns on each RST change.
          @see <a href="https://files.waveshare.com/upload/e/ec/2.66inch-e-paper-b-specification.pdf">2.66inch e-Paper B Specification</a>
         */
        rst.write(State.LOW);
        // wait should be 200ns, so 1ms is good enough (keeping in mind bad accuracy)
        Thread.sleep(1);
        rst.write(State.HIGH);
        // wait should be 200ns, so 1ms is good enough (keeping in mind bad accuracy)
        Thread.sleep(1);
        busyWait();
        var timeStop = Instant.now();
        logger.debug("Hard reset finished, took {}", formatDuration(Duration.between(timeStart, timeStop)));
    }

    @Override
    public void softReset() throws IOException {
        var timeStart = Instant.now();
        sendCommand(Command.SOFT_RESET.getCommand());
        busyWait();
        var timeStop = Instant.now();
        logger.debug("Soft reset finished, took {}", formatDuration(Duration.between(timeStart, timeStop)));
    }

    @Override
    public void sleep() throws IOException {
        logger.info("Going to deep sleep mode...");
        sendCommand(Command.SLEEP.getCommand());
        sendData(0x01);
    }

    @Override
    public void busyWait() throws IOException {
        logger.debug("Busy...");
        var timeStart = Instant.now();
        while (busy.read().equals(State.HIGH)) {
            // better than Thread.sleep(), can save up to 10ms per call.
            Thread.onSpinWait();
        }
        var timeStop = Instant.now();
        logger.debug("Busy wait finished, took {}", formatDuration(Duration.between(timeStart, timeStop)));
    }

    @Override
    public void powerOff() throws IOException {
        pwr.write(State.LOW);
        dc.write(State.LOW);
        rst.write(State.LOW);
        spiBus.close();
    }

    @Override
    public void sendCommand(int command) throws IOException {
        logger.debug("Send command {}", Command.getCommandByCode(command));
        dc.write(State.LOW);
        spiBus.sendByteData(ByteBuffer.allocate(4).putInt(command).array(), false);
    }


    @Override
    public void sendData(int data) throws IOException {
        dc.write(State.HIGH);
        spiBus.sendByteData(ByteBuffer.allocate(1).put((byte) data).array(), false);
    }

    private void showImage(DisplayBufferHolder bufferHolder) throws IOException {
        var blackImageBuffer = bufferHolder.get(DisplayLayer.BLACK_AND_WHITE);
        if (blackImageBuffer == null) {
            throw new IOException("Black and white colored image is missing in buffer holder!");
        }
        var redImageBuffer = bufferHolder.get(DisplayLayer.RED_AND_WHITE);
        if (redImageBuffer == null) {
            throw new IOException("Red and white colored image is missing in buffer holder!");
        }
        sendCommand(Command.BLACK_AND_WHITE_RAM.getCommand());
        for (int j = 0; j < HEIGHT; j++) {
            for (int i = 0; i < WIDTH / 8; i++) {
                sendData(blackImageBuffer[i + j * WIDTH / 8]);
            }
        }
        sendCommand(Command.RED_AND_WHITE_RAM.getCommand());
        for (int j = 0; j < HEIGHT; j++) {
            for (int i = 0; i < WIDTH / 8; i++) {
                sendData(redImageBuffer[i + j * WIDTH / 8]);
            }
        }
        sendCommand(Command.DISPLAY_UPDATE_SEQUENCE.getCommand());
        busyWait();
    }

    @Override
    public void show(DisplayBufferHolder bufferHolder) throws IOException {
        var timeStart = Instant.now();
        logger.info("Showing image");
        showImage(bufferHolder);
        var timeStop = Instant.now();
        logger.info("Show finished, took {}", formatDuration(Duration.between(timeStart, timeStop)));
    }

    @Override
    public void clearDisplay() throws IOException {
        var timeStart = Instant.now();
        logger.info("Clearing display");
        showImage(clearRectBufferHolder);
        var timeStop = Instant.now();
        logger.info("Clear finished, took {}", formatDuration(Duration.between(timeStart, timeStop)));
    }

    private String formatDuration(Duration duration) {
        List<String> parts = new ArrayList<>();
        int minutes = duration.toMinutesPart();
        if (minutes > 0) {
            parts.add(minutes + "min");
        }
        int seconds = duration.toSecondsPart();
        if (seconds > 0 || !parts.isEmpty()) {
            parts.add(seconds + "s");
        }
        int millis = duration.toMillisPart();
        if (millis > 0 || !parts.isEmpty()) {
            parts.add(millis + "ms");
        }
        return String.join(", ", parts);
    }

    public enum Command {
        DISPLAY_UPDATE_SEQUENCE(0x20),
        BLACK_AND_WHITE_RAM(0x24),
        RED_AND_WHITE_RAM(0x26),
        SLEEP(0x10),
        RAM_X_ADDRESS_COUNTER(0x4E),
        RAM_Y_ADDRESS_COUNTER(0x4F),
        DISPLAY_UPDATE_CONTROL(0x21),
        RAM_X_ADDRESS_START_END_POSITION(0x44),
        RAM_Y_ADDRESS_START_END_POSITION(0x45),
        DATA_ENTRY(0x11),
        SOFT_RESET(0x12);

        private final int command;

        Command(int command) {
            this.command = command;
        }

        public int getCommand() {
            return command;
        }

        public static Command getCommandByCode(int code) {
            return Arrays.stream(Command.values()).filter((c) -> c.getCommand() == code).findFirst().orElseThrow();
        }

        @Override
        public String toString() {
            return name() + " (" + IntegerToHex.convert(command) + ")";
        }
    }


}
