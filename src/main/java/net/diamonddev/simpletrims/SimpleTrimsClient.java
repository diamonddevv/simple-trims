package net.diamonddev.simpletrims;

import net.diamonddev.simpletrims.data.PaletteEncoderDecoder;
import net.diamonddev.simpletrims.network.SendEncodedPalettes;
import net.diamonddev.simpletrims.network.SendQuietReload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class SimpleTrimsClient implements ClientModInitializer {

    public static HashMap<Identifier, PaletteEncoderDecoder.EncodedPalette> NETWORKED_PALETTES = new HashMap<>();
    public static boolean shouldQuietlyReload = false;

    @Override
    public void onInitializeClient() {
        SendEncodedPalettes.registerReceiver();
        SendQuietReload.registerReceiver();
    }
}
