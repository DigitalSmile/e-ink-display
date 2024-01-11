package org.digitalsmile.display;

import org.digitalsmile.display.color.DisplayColors;

public record DisplayType(String name, DisplayColors[] availableColors, int width, int height) {

}
