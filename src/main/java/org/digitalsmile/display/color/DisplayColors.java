package org.digitalsmile.display.color;

public enum DisplayColors {
    BLACK(0x000000),
    WHITE(0xffffff),
    RED(0xff0000);

    private final int colorCode;
    DisplayColors(int colorCode) {
        this.colorCode = colorCode;
    }

    public int getColorCode() {
        return colorCode;
    }
}
