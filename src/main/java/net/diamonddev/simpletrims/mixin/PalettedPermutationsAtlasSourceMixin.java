package net.diamonddev.simpletrims.mixin;

import net.diamonddev.simpletrims.SimpleTrimDefinitionLoader;
import net.minecraft.client.texture.atlas.PalettedPermutationsAtlasSource;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

            SimpleTrimDefinitionLoader.loopSimpleMaterials(bean -> {
                mutablePermutations.put(bean.getAssetName(), bean.getPathToPalette());
            });

            this.permutations = Collections.unmodifiableMap(mutablePermutations);
        }
    }
}
