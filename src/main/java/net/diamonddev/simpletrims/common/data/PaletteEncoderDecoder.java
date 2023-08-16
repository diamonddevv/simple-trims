package net.diamonddev.simpletrims.common.data;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;

public class PaletteEncoderDecoder {

    public static class EncodedPalette {
        public Identifier loc;
        public byte[] bytes;

        // properties
        public boolean emissive;
    }

    public static EncodedPalette encode(Identifier loc, InputStream stream, boolean emissive) {

        try {
            EncodedPalette palette = new EncodedPalette();

            palette.loc = loc;
            palette.bytes = stream.readAllBytes();
            palette.emissive = emissive;

            return palette;
        } catch (IOException e) {
            throw new RuntimeException("Could not encode InputStream to EncodedPalette", e);
        }
    }

    public static NativeImage openDecode(EncodedPalette palette) {
        try {
            NativeImage image = NativeImage.read(palette.bytes);

            // Apply Properties if Needed
            if (palette.emissive) {
                // palettes are 8px wide by 1px tall
                for (int i = 0; i < 8; i++) {
                    image.setColor(i, 0, bitwiseShiftOpacity(image.getColor(i, 0), 254)); // the shader for emissive trims wants opacity of 254 to become emissive
                }
            }

            return image;
        } catch (IOException ioe) {
            throw new RuntimeException("Could not decode EncodedPalette", ioe);
        }
    }

    private static int bitwiseShiftOpacity(int color, int alpha) {
        return (color & 0xffffff) | (alpha << 24); // ty stack overflow https://stackoverflow.com/a/6715482
    }
}
