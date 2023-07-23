package net.diamonddev.simpletrims.mixin;

import net.diamonddev.simpletrims.SimpleTrims;
import net.diamonddev.simpletrims.SimpleTrimsClient;
import net.diamonddev.simpletrims.data.PaletteEncoderDecoder;
import net.diamonddev.simpletrims.data.SimpleTrimDataLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.atlas.PalettedPermutationsAtlasSource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Environment(EnvType.CLIENT)
@Mixin(PalettedPermutationsAtlasSource.class)
public abstract class PalettedPermutationsAtlasSourceMixin {

    @Unique private static final Identifier TRIM_MATERIAL_PALETTE_KEY = new Identifier("trims/color_palettes/trim_palette");

    @Mutable
    @Shadow @Final private Map<String, Identifier> permutations;

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void simpletrims$addDynamicPermutations(List<Identifier> textures, Identifier paletteKey, Map<String, Identifier> permutations, CallbackInfo ci) {
        if (paletteKey.equals(TRIM_MATERIAL_PALETTE_KEY)) {
            HashMap<String, Identifier> mutablePermutations = new HashMap<>(permutations);

            for (var bean : SimpleTrimDataLoader.SIMPLE_TRIM_MATERIALS) {
                mutablePermutations.put(bean.getAssetName(), bean.getPathToPalette());
            }

            this.permutations = Collections.unmodifiableMap(mutablePermutations);
        }
    }

    @Inject(method = "method_48486", at = @At("HEAD"), cancellable = true)
    private static void simpletrims$overrideLoadingLocationForEncodedPaletteResources(ResourceManager resourceManager, Identifier identifier, CallbackInfoReturnable<int[]> cir) {
        if (identifier.getPath().contains(SimpleTrimDataLoader.ENCODED_PALETTE_CONTAIN_STRING)) {
            PaletteEncoderDecoder.EncodedPalette palette = SimpleTrimsClient.NETWORKED_PALETTES.get(identifier);
            try (NativeImage image = PaletteEncoderDecoder.openDecode(palette)) {
                int[] colors = image.copyPixelsRgba();
                SimpleTrims.LOGGER.info("Successfully tricked PalettedPermutations into loading an EncodedPalette! '{}'", identifier);
                cir.setReturnValue(colors);
            } catch (Exception e) {
                throw new RuntimeException("Failed to trick PalettedPermutations into loading an EncodedPalette", e);
            }
        }
    }
}
