package net.diamonddev.simpletrims.data;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

public class PaletteEncoderDecoder {

    public static class EncodedPalette {
        public Identifier loc;
        public int width;
        public int[] colors;
    }

    public static EncodedPalette encode(Identifier loc, NativeImage nativeImage, int wPx) {
        if (nativeImage.getFormat() == NativeImage.Format.RGBA) {
            if (nativeImage.getHeight() == 1) {
                if (nativeImage.getWidth() == wPx) {
                    EncodedPalette palette = new EncodedPalette();
                    int[] colors = new int[nativeImage.getWidth()];


                    for (int y = 0; y < nativeImage.getHeight(); y++) {
                        for (int x = 0; x < nativeImage.getWidth(); x++) {
                            int color = nativeImage.getColor(x, y);
                            colors[x] = color;
                        }
                    }

                    palette.loc = loc;
                    palette.width = wPx;
                    palette.colors = colors;
                    return palette;
                } else {
                    throw new IllegalArgumentException("Native image width did not match the provided bind (Must be " + wPx + " px wide)");
                }
            } else {
                throw new IllegalArgumentException("Encodable Palettes must be 1 pixel high");
            }
        } else {
            throw new IllegalArgumentException("Encodable Palettes must be in RGBA format; Got " + nativeImage.getFormat());
        }
    }

    public static NativeImage openDecode(EncodedPalette palette) {
        NativeImage image = new NativeImage(NativeImage.Format.RGBA, palette.width, 1, true);

        int x = 0;
        for (int color : palette.colors) {
            image.setColor(x, 0, color);
            x++;
        }

        return image;
    }
}
