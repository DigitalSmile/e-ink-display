package org.digitalsmile.display;

import org.digitalsmile.display.color.DisplayLayer;

public record DisplayType(String name, DisplayLayer[] displayLayers, int width, int height) {

}
