package net.diamonddev.simpletrims.mixin;

import net.diamonddev.simpletrims.common.SimpleTrims;
import net.diamonddev.simpletrims.common.data.PaletteEncoderDecoder;
import net.diamonddev.simpletrims.common.data.SimpleTrimsDataLoader;
import net.diamonddev.simpletrims.common.network.SendAssetNamesToPalettePaths;
import net.diamonddev.simpletrims.common.network.SendEncodedPalettes;
import net.diamonddev.simpletrims.common.network.SendQuietReload;
import net.diamonddev.simpletrims.common.network.SendTranslations;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void simpletrims$onPlayerConnectToServer(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        String playername = player.getGameProfile().getName();

        // Asset Names to Palette Paths
        HashMap<String, String> assetsToPaths = new HashMap<>();
        for (var bean : SimpleTrimsDataLoader.SIMPLE_TRIM_MATERIALS) {
            assetsToPaths.put(bean.getAssetName(), bean.getPathToPalette().toString());
        }
        SimpleTrims.LOGGER.info("Sending user {} Material Asset-to-Path hashmap.. [Size: {}]", playername, assetsToPaths.size());
        ServerPlayNetworking.send(player, SendAssetNamesToPalettePaths.SEND_ASSETS, SendAssetNamesToPalettePaths.write(assetsToPaths));

        // Encoded Palettes
        PacketByteBuf[] bufs = SendEncodedPalettes.write(SimpleTrimsDataLoader.ENCODED_PALETTES.toArray(new PaletteEncoderDecoder.EncodedPalette[0]));
        SimpleTrims.LOGGER.info("Sending user {} {} packet(s) of EncodedPalettes.. [{} total palette(s)]", playername, bufs.length, SimpleTrimsDataLoader.ENCODED_PALETTES.size());
        for (var buf : bufs) {
            ServerPlayNetworking.send(player, SendEncodedPalettes.SEND_ENCODED_PALETTES, buf);
        }

        // Translations
        HashMap<Identifier, HashMap<String, String>> translations = new HashMap<>();
        int translationCount = 0, materialCount = 0;
        for (var bean : SimpleTrimsDataLoader.SIMPLE_TRIM_MATERIALS) {
            if (bean.usingTranslationMap()) {
                var hash = bean.getTranslationHashmap();
                var id = new Identifier(bean.getNamespace(), bean.getAssetName());
                translations.put(id, hash);
                materialCount += 1;
                translationCount += hash.size();
            }
        }
        SimpleTrims.LOGGER.info("Sending user {} {} translations for {} materials..", playername, translationCount, materialCount);
        ServerPlayNetworking.send(player, SendTranslations.SEND_TRANSLATIONS, SendTranslations.write(translations));

        // Quiet Reload Request
        SimpleTrims.LOGGER.info("Telling user {}'s client to quietly reload..", playername);
        ServerPlayNetworking.send(player, SendQuietReload.SEND_QUIET_RELOAD, PacketByteBufs.empty());


        // finish
        SimpleTrims.LOGGER.info("Sent {} all necessary packets!", playername);
    }
}
