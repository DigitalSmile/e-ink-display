package org.digitalsmile.eink;

import org.digitalsmile.eink.color.DisplayLayer;

import java.util.HashMap;
import java.util.Map;

public final class DisplayBufferHolder {
    private final Map<DisplayLayer, byte[]> bufferMap = new HashMap<>();

    private DisplayBufferHolder() {
    }

    public void put(DisplayLayer layer, byte[] byteBuffer) {
        bufferMap.put(layer, byteBuffer);
    }

    public byte[] get(DisplayLayer layer) {
        return bufferMap.get(layer);
    }

    public void clear() {
        bufferMap.clear();
    }

    public static DisplayBufferHolderBuilder builder() {
        return new DisplayBufferHolderBuilder();
    }

    public static class DisplayBufferHolderBuilder {
        private final DisplayBufferHolder displayBufferHolder = new DisplayBufferHolder();

        private DisplayBufferHolderBuilder() {
        }

        public DisplayBufferHolderBuilder add(DisplayLayer layer, byte[] byteBuffer) {
            displayBufferHolder.put(layer, byteBuffer);
            return this;
        }

        public DisplayBufferHolder build() {
            return displayBufferHolder;
        }
    }

}
