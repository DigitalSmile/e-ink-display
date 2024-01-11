package org.digitalsmile.display.color;

import java.util.Arrays;

public enum DisplayLayer {
    BLACK_AND_WHITE(DisplayColor.BLACK, DisplayColor.WHITE),
    RED_AND_WHITE(DisplayColor.RED, DisplayColor.WHITE);

    private final DisplayColor drawColor;
    private final DisplayColor[] availableColors;
    DisplayLayer(DisplayColor drawColor, DisplayColor... availableColors) {
        this.drawColor = drawColor;
        this.availableColors = availableColors;
    }

    public DisplayColor getDrawColor() {
        return drawColor;
    }

    public DisplayColor[] getAvailableColors() {
        var allColors = Arrays.asList(availableColors);
        allColors.add(drawColor);
        return allColors.toArray(DisplayColor[]::new);
    }
}
