package dev.diamond.simpletrims.client;

import com.google.gson.annotations.SerializedName;
import dev.diamond.simpletrims.common.SimpleTrims;
import dev.diamond.simpletrims.common.data.PaletteEncoderDecoder;
import dev.diamond.simpletrims.common.network.SendAssetNamesToPalettePaths;
import dev.diamond.simpletrims.common.network.SendEncodedPalettes;
import dev.diamond.simpletrims.common.network.SendQuietReload;
import dev.diamond.simpletrims.common.network.SendTranslations;
import net.diamonddev.libgenetics.common.api.v1.config.chromosome.ChromosomeConfigFile;
import net.diamonddev.libgenetics.common.api.v1.config.chromosome.ChromosomeConfigFileRegistry;
import net.diamonddev.libgenetics.common.api.v1.config.chromosome.serializer.ConfigSerializer;
import net.diamonddev.libgenetics.common.api.v1.config.chromosome.serializer.JsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class SimpleTrimsClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Simple Trims Client");

    public static HashMap<Identifier, HashMap<String, String>> NETWORKED_MATERIAL_TRANSLATION_CODE_TO_STRING = new HashMap<>();
    public static HashMap<String, String> NETWORKED_ASSETNAME_TO_PATH_HASH = new HashMap<>();
    public static HashMap<Identifier, PaletteEncoderDecoder.EncodedPalette> NETWORKED_PALETTES = new HashMap<>();
    public static boolean shouldQuietlyReload = false;

    public static SimpleTrimsClientConfig CONFIG = null;

    @Override
    public void onInitializeClient() {
        SendEncodedPalettes.registerReceiver();
        SendQuietReload.registerReceiver();
        SendAssetNamesToPalettePaths.registerReceiver();
        SendTranslations.registerReceiver();

        CONFIG = ChromosomeConfigFileRegistry.registerAndReadAsSelf(SimpleTrims.id("client_config"), CONFIG, SimpleTrimsClientConfig.class);
    }

    public static class SimpleTrimsClientConfig implements ChromosomeConfigFile {

        @Override
        public String getFilePathFromConfigDirectory() {
            return "simple_trims_client.json";
        }

        @Override
        public ConfigSerializer getSerializer() {
            return new JsonConfigSerializer();
        }

        @SerializedName("enableQuietReload_WILL_CAUSE_MOD_CONFLICTS")
        public boolean quietReloads = false;
    }


    public static String getMaterialTranslation(Identifier identifier, String langcode, String key) {
        return NETWORKED_MATERIAL_TRANSLATION_CODE_TO_STRING.get(identifier).containsKey(langcode) ? NETWORKED_MATERIAL_TRANSLATION_CODE_TO_STRING.get(identifier).get(langcode) : NETWORKED_MATERIAL_TRANSLATION_CODE_TO_STRING.get(identifier).getOrDefault(Language.DEFAULT_LANGUAGE, key);
    }
}
