package net.diamonddev.simpletrims.data;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;

public class PaletteEncoderDecoder {

    public static class EncodedPalette {
        public Identifier loc;
        public byte[] bytes;
    }

    public static EncodedPalette encode(Identifier loc, InputStream stream) {

        try {
            EncodedPalette palette = new EncodedPalette();

            palette.loc = loc;
            palette.bytes = stream.readAllBytes();

            return palette;
        } catch (IOException e) {
            throw new RuntimeException("Could not encode InputStream to EncodedPalette", e);
        }
    }

    public static NativeImage openDecode(EncodedPalette palette) {
        try {
            return NativeImage.read(palette.bytes);
        } catch (IOException ioe) {
            throw new RuntimeException("Could not decode EncodedPalette", ioe);
        }
    }
}
