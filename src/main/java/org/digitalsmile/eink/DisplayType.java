package org.digitalsmile.eink;

import org.digitalsmile.eink.color.DisplayColor;
import org.digitalsmile.eink.color.DisplayColorLayout;
import org.digitalsmile.eink.color.DisplayLayer;

import java.util.Map;

public record DisplayType(String name, DisplayLayer[] displayLayers, int width, int height,
                          Map<DisplayColor, Map<DisplayColorLayout, Byte>> colorBufferConverter,
                          Map<DisplayLayer, DisplayColor> ditheringColorFilter) {
}
