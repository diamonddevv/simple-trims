package net.diamonddev.simpletrims.mixin;

import net.diamonddev.simpletrims.SimpleTrims;
import net.diamonddev.simpletrims.data.SimpleTrimsDataLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorTrimMaterial.class)
public class ArmorTrimMaterialMixin {
    @Shadow @Final private Text description;

    @Environment(EnvType.CLIENT) // this is a description of an item, so its client sided
    @Inject(method = "description", at = @At("HEAD"), cancellable = true)
    private void simpletrims$replaceDescriptionWithDataTranslationKeys(CallbackInfoReturnable<Text> cir) {
        if (SimpleTrims.testRegex(this.description.getString(), SimpleTrimsDataLoader.REFERABLE_KEY_REGEX_PATTERN)) {
            String[] key = this.description.getString().split("\\.");
            SimpleTrimsDataLoader.MaterialBeanWrapper bean = null;
            for (var tb : SimpleTrimsDataLoader.SIMPLE_TRIM_MATERIALS) {
                if (tb.getNamespace().equals(key[0]) && tb.getAssetName().equals(key[1])) {
                    bean = tb;
                    break;
                }
            }

            if (bean == null) {
                cir.setReturnValue(this.description);
            } else {
                cir.setReturnValue(Text.literal(bean.getTranslation(MinecraftClient.getInstance().options.language)).fillStyle(this.description.getStyle()));
            }
        }
    }
}
