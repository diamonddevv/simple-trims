package net.diamonddev.simpletrims.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.diamonddev.simpletrims.data.SimpleTrimDataLoader;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.DataPackContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;

@Mixin(DataPackContents.class) @SuppressWarnings("unchecked")
public abstract class DataPackContentsMixin { // this mixin was heavily taken from allthetrims. the code is slightly modified though


    @ModifyExpressionValue(method = "repopulateTags", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;collect(Ljava/util/stream/Collector;)Ljava/lang/Object;"))
    private static <T> Object simpletrims$addAllArmorToTagAndAllMaterialsToTag(Object obj) {
        HashMap<TagKey<T>, List<RegistryEntry<T>>> entries = new HashMap<>((Map<TagKey<T>, List<RegistryEntry<T>>>) obj);


        if (entries.containsKey(ItemTags.TRIMMABLE_ARMOR)) {
            ArrayList<RegistryEntry<T>> list = new ArrayList<>();
            List<Item> items = Registries.ITEM.stream().filter(i -> i instanceof Equipment e && e.getSlotType().isArmorSlot()).toList();
            for (Item item : items) list.add(mapToEntry(item));
            entries.put((TagKey<T>) ItemTags.TRIMMABLE_ARMOR, list);
        }

        if (entries.containsKey(ItemTags.TRIM_MATERIALS)) {
            ArrayList<RegistryEntry<T>> list = new ArrayList<>();

            for (var material : SimpleTrimDataLoader.SIMPLE_TRIM_MATERIALS) list.add(mapToEntry(material.getIngredientAsItem()));
            list.addAll(entries.get(ItemTags.TRIM_MATERIALS));

            entries.put((TagKey<T>) ItemTags.TRIM_MATERIALS, list);
        }
        return Collections.unmodifiableMap(entries);
    }

    @Unique
    private static <T> RegistryEntry<T> mapToEntry(Item item) {
        return (RegistryEntry<T>)Registries.ITEM.getEntry(item);
    }
}
