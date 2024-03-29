package dev.diamond.simpletrims.mixin;

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.diamond.simpletrims.common.SimpleTrims;
import dev.diamond.simpletrims.common.data.SimpleTrimsDataLoader;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

@Mixin(RegistryLoader.class)
public class RegistryLoaderMixin {
    @ModifyExpressionValue(
            method = "load(Lnet/minecraft/registry/RegistryOps$RegistryInfoGetter;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/MutableRegistry;Lcom/mojang/serialization/Decoder;Ljava/util/Map;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resource/ResourceFinder;findResources(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;"
            )
    )
    private static Map<Identifier, Resource> simpletrims$ctrlCctrlVAllTheTrimsAndCopyAllSimpleTrimMatsToResourceLoader(Map<Identifier, Resource> og, @Local ResourceManager resourceManager) {

        Iterator<Map.Entry<Identifier, Resource>> iterator = og.entrySet().iterator();
        if (!iterator.hasNext()) return og;

        Map.Entry<Identifier, Resource> first = iterator.next();
        if (!first.getKey().getPath().contains("trim_material")) return og;

        // ^^ All the trims has these lines. they're important.

        ResourcePack pack = first.getValue().getPack();

        if (SimpleTrimsDataLoader.SIMPLE_TRIM_MATERIALS.isEmpty()) {
            SimpleTrims.TRIM_DATA.reload(resourceManager);
        }

        for (var bean : SimpleTrimsDataLoader.SIMPLE_TRIM_MATERIALS) {
            JsonObject resource = new JsonObject();
            JsonObject desc = new JsonObject();

            try {
                resource.addProperty("asset_name", bean.getAssetName());
                desc.addProperty("color", bean.getDescColorCodeAsHexString());
                desc.addProperty("translate", bean.getDescTranslationKey().equals(SimpleTrimsDataLoader.NOT_A_TRANSLATION_KEY_LOL) ? bean.getReferrableTranslationKey() : bean.getDescTranslationKey());
                resource.add("description", desc);
                resource.addProperty("ingredient", bean.getIngredientAsId());
                resource.addProperty("item_model_index", 0f);

                Resource res = new Resource(pack, () -> IOUtils.toInputStream(resource.toString(), StandardCharsets.UTF_8));
                Identifier id = new Identifier(bean.getNamespace(), "trim_material/" + bean.getAssetName() + ".json");
                og.put(id, res);
                SimpleTrims.LOGGER.info("Dynamically generated Trim Material Definition for assetname '{}'", bean.getAssetName());
            } catch (RuntimeException runtime) {
                SimpleTrims.LOGGER.error("Failed to dynamically generate Trim Material Definition for assetname '" +  bean.getAssetName() + "'", runtime);
            }
        }

        return og;
    }
}
