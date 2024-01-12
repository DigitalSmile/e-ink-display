package org.digitalsmile.eink.color;

import java.util.Set;

public enum DisplayLayer {
    BLACK_AND_WHITE(DisplayColor.BLACK, DisplayColor.WHITE),
    RED_AND_WHITE(DisplayColor.RED, DisplayColor.WHITE);

    private final DisplayColor drawColor;
    private final DisplayColor backgroundColor;

    DisplayLayer(DisplayColor drawColor, DisplayColor backgroundColor) {
        this.drawColor = drawColor;
        this.backgroundColor = backgroundColor;
    }

    public DisplayColor getBackgroundColor() {
        return backgroundColor;
    }

    public DisplayColor getDrawColor() {
        return drawColor;
    }

    public DisplayColor[] getAvailableColors() {
        var allColors = Set.of(drawColor, backgroundColor);
        return allColors.toArray(DisplayColor[]::new);
    }
}
