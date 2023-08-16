package net.diamonddev.simpletrims.common.command;

import net.diamonddev.simpletrims.common.SimpleTrims;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimMaterials;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.item.trim.ArmorTrimPatterns;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;

public class CmdInit {
    @Nullable
    private static DynamicRegistryManager DRM = null;

    public static HashMap<String, Optional<RegistryEntry.Reference<ArmorTrimPattern>>> PATTERN = new HashMap<>();
    public static HashMap<String, Optional<RegistryEntry.Reference<ArmorTrimMaterial>>> MATERIAL = new HashMap<>();


    public static void register() {
        // callbacks
        DynamicRegistrySetupCallback.EVENT.register(registryView -> {
            DynamicRegistryManager drm = registryView.asDynamicRegistryManager();
            DRM = drm;

            registryView.registerEntryAdded(RegistryKeys.TRIM_PATTERN, (rawId, id, object) -> {
                PATTERN.put(id.toString(), ArmorTrimPatterns.get(drm, object.templateItem().value().getDefaultStack()));
            });

            registryView.registerEntryAdded(RegistryKeys.TRIM_MATERIAL, (rawId, id, object) -> {
                MATERIAL.put(id.toString(), ArmorTrimMaterials.get(drm, object.ingredient().value().getDefaultStack()));
            });
        });

        // arg types
        ArgumentTypeRegistry.registerArgumentType(SimpleTrims.id("pattern_arg"), ArmorTrimPatternArgument.class, ConstantArgumentSerializer.of(ArmorTrimPatternArgument::pattern));
        ArgumentTypeRegistry.registerArgumentType(SimpleTrims.id("material_arg"), ArmorTrimMaterialArgument.class, ConstantArgumentSerializer.of(ArmorTrimMaterialArgument::material));

        // command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            TrimCommand.register(dispatcher, DRM); // oh boy this has no potential to go wrong at all!
        });
    }
}
