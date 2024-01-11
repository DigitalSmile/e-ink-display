package org.digitalsmile.display;

public enum DisplayOrientation {
    /**
     *  +---------------+
     *  | ↓→            | ====
     *  |               | wires
     *  |               | ====
     *  +---------------+
     *
     */
    HORIZONTAL_NORMAL(270),

    /**
     *    ||     ||
     *    ||wires||
     *  +-----------+
     *  | ↓→        |
     *  |           |
     *  |           |
     *  |           |
     *  |           |
     *  |           |
     *  +-----------+
     *
     */
    VERTICAL_NORMAL(0);

    private final int rotation;
    DisplayOrientation(int rotation) {
        this.rotation = rotation;
    }

    public int getRotation() {
        return rotation;
    }

    public int getCanvasWidth(DisplayType displayType) {
        return switch (rotation) {
            case 0 -> displayType.width();
            case 270 -> displayType.height();
            default -> throw new IllegalStateException("Unexpected value: " + rotation);
        };
    }

    public int getCanvasHeight(DisplayType displayType) {
        return switch (rotation) {
            case 0 -> displayType.height();
            case 270 -> displayType.width();
            default -> throw new IllegalStateException("Unexpected value: " + rotation);
        };
    }
}
