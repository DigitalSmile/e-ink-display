package org.digitalsmile.display.color;

public enum DisplayLayer {
    BLACK_AND_WHITE(DisplayColors.BLACK, DisplayColors.WHITE),
    RED_AND_WHITE(DisplayColors.RED, DisplayColors.WHITE);


    private final DisplayColors[] availableColors;
    DisplayLayer(DisplayColors... availableColors) {
        this.availableColors = availableColors;
    }

    public DisplayColors[] getAvailableColors() {
        return availableColors;
    }
}
