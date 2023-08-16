package net.diamonddev.simpletrims.client;

import net.diamonddev.simpletrims.common.data.PaletteEncoderDecoder;
import net.diamonddev.simpletrims.common.network.SendAssetNamesToPalettePaths;
import net.diamonddev.simpletrims.common.network.SendEncodedPalettes;
import net.diamonddev.simpletrims.common.network.SendQuietReload;
import net.diamonddev.simpletrims.common.network.SendTranslations;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class SimpleTrimsClient implements ClientModInitializer {

    public static HashMap<Identifier, HashMap<String, String>> NETWORKED_MATERIAL_TRANSLATION_CODE_TO_STRING = new HashMap<>();
    public static HashMap<String, String> NETWORKED_ASSETNAME_TO_PATH_HASH = new HashMap<>();
    public static HashMap<Identifier, PaletteEncoderDecoder.EncodedPalette> NETWORKED_PALETTES = new HashMap<>();
    public static boolean shouldQuietlyReload = false;

    @Override
    public void onInitializeClient() {
        SendEncodedPalettes.registerReceiver();
        SendQuietReload.registerReceiver();
        SendAssetNamesToPalettePaths.registerReceiver();
        SendTranslations.registerReceiver();
    }


    public static String getMaterialTranslation(Identifier identifier, String langcode, String key) {
        return NETWORKED_MATERIAL_TRANSLATION_CODE_TO_STRING.get(identifier).containsKey(langcode) ? NETWORKED_MATERIAL_TRANSLATION_CODE_TO_STRING.get(identifier).get(langcode) : NETWORKED_MATERIAL_TRANSLATION_CODE_TO_STRING.get(identifier).getOrDefault(Language.DEFAULT_LANGUAGE, key);
    }
}
