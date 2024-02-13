package dev.diamond.simpletrims.mixin;

import dev.diamond.simpletrims.common.SimpleTrims;
import dev.diamond.simpletrims.client.SimpleTrimsClient;
import dev.diamond.simpletrims.common.data.SimpleTrimsDataLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(ArmorTrimMaterial.class)
public class ArmorTrimMaterialMixin {
    @Shadow @Final private Text description;

    @Environment(EnvType.CLIENT) // this is a description of an item, so its client sided
    @Inject(method = "description", at = @At("HEAD"), cancellable = true)
    private void simpletrims$replaceDescriptionWithDataTranslationKeys(CallbackInfoReturnable<Text> cir) {
        if (SimpleTrims.testRegex(this.description.getString(), SimpleTrimsDataLoader.REFERABLE_KEY_REGEX_PATTERN)) {
            String[] key = this.description.getString().split("\\.");
            Identifier id = null;
            HashMap<String, String> langhash = null;
            for (var entry : SimpleTrimsClient.NETWORKED_MATERIAL_TRANSLATION_CODE_TO_STRING.entrySet()) {
                if (entry.getKey().getNamespace().matches(key[0]) && entry.getKey().getPath().matches(key[1])) {
                    id = entry.getKey();
                    langhash = entry.getValue();
                    break;
                }
            }

            if (langhash == null) {
                cir.setReturnValue(this.description);
            } else {
                cir.setReturnValue(Text.literal(SimpleTrimsClient.getMaterialTranslation(id, MinecraftClient.getInstance().options.language, this.description.getString())).fillStyle(this.description.getStyle()));
            }
        }
    }
}
