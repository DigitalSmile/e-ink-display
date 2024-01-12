package org.digitalsmile.eink.color;

public enum DisplayColor {
    BLACK(0x000000),
    WHITE(0xffffff),
    RED(0xff0000);

    private final int colorCode;

    DisplayColor(int colorCode) {
        this.colorCode = colorCode;
    }

    public int getColorCode() {
        return colorCode;
    }
}
