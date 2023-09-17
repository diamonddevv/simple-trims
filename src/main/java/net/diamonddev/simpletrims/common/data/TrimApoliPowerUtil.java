package net.diamonddev.simpletrims.common.data;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.component.PowerHolderComponentImpl;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class TrimApoliPowerUtil {

    public record SourcedPower<T extends Power>(PowerType<T> power, Identifier source) {
    }

    private static final HashMap<LivingEntity, HashMap<EquipmentSlot, ItemStack>> KNOWN_ARMOR = new HashMap<>();
    private static final HashMap<LivingEntity, HashMap<EquipmentSlot, SourcedPower<?>>> ENTITY_TO_SLOT_TO_POWER = new HashMap<>();
    private static final HashMap<Identifier, String> MEMOIZED_POWERS = new HashMap<>();

    public static void clearMemo() {
        MEMOIZED_POWERS.clear();
    }

    public static void updateAllTrimPowerApplications(LivingEntity livingEntity) {
        updateSpecificTrimPowerApplication(livingEntity, EquipmentSlot.HEAD);
        updateSpecificTrimPowerApplication(livingEntity, EquipmentSlot.CHEST);
        updateSpecificTrimPowerApplication(livingEntity, EquipmentSlot.LEGS);
        updateSpecificTrimPowerApplication(livingEntity, EquipmentSlot.FEET);
    }

    public static void updateSpecificTrimPowerApplication(LivingEntity livingEntity, EquipmentSlot slot) {
        ItemStack stack = livingEntity.getEquippedStack(slot);

        if (KNOWN_ARMOR.containsKey(livingEntity)) {
            if (!hasDifference(livingEntity, slot, stack)) {
                return;
            }
        }

        if (stack.isEmpty()) {
            removePower(livingEntity, slot);
            return;
        }

        if (!KNOWN_ARMOR.containsKey(livingEntity)) {
            KNOWN_ARMOR.put(livingEntity, new HashMap<>());
        }
        KNOWN_ARMOR.get(livingEntity).put(slot, stack); // update

        if (stack.getOrCreateNbt().contains(ArmorTrim.NBT_KEY)) {
            NbtCompound trim = stack.getSubNbt(ArmorTrim.NBT_KEY);
            if (trim != null) {
                if (trim.contains("material")) {
                    Identifier material = new Identifier(trim.getString("material"));

                    String powerId = null;
                    if (MEMOIZED_POWERS.containsKey(material)) {
                        powerId = MEMOIZED_POWERS.get(material);
                    } else {
                        var mbw = SimpleTrimsDataLoader.lookupWrapper(material);
                        if (mbw != null) {
                            powerId = mbw.getPower();
                            MEMOIZED_POWERS.put(material, powerId); // memoize
                        }

                    }

                    if (powerId != null) {
                        PowerHolderComponent holder = new PowerHolderComponentImpl(livingEntity);
                        PowerType<?> type = fetchPowerType(powerId);
                        holder.addPower(type, material);

                        if (!ENTITY_TO_SLOT_TO_POWER.containsKey(livingEntity)) {
                            ENTITY_TO_SLOT_TO_POWER.put(livingEntity, new HashMap<>());
                        }
                        ENTITY_TO_SLOT_TO_POWER.get(livingEntity).put(slot, new SourcedPower<>(type, material)); // update
                    }
                }
            }
        }
    }

    private static void removePower(LivingEntity living, EquipmentSlot equipmentSlot) {
        if (!ENTITY_TO_SLOT_TO_POWER.containsKey(living)) {
            ENTITY_TO_SLOT_TO_POWER.put(living, new HashMap<>());
        }

        var removed = ENTITY_TO_SLOT_TO_POWER.get(living).remove(equipmentSlot);
        if (removed != null) {
            PowerHolderComponent holder = new PowerHolderComponentImpl(living);
            holder.removePower(removed.power, removed.source);
        }
    }

    private static PowerType<?> fetchPowerType(String powerId) {
        return (PowerType<?>) PowerTypeRegistry.get(new Identifier(powerId));
    }

    private static boolean hasDifference(LivingEntity entity, EquipmentSlot slot, ItemStack otherStack) {
        switch (slot) {
            case MAINHAND, OFFHAND -> {
                throw new RuntimeException("Mainhand and Offhand are not Armor Slots.");
            }
            case FEET, LEGS, CHEST, HEAD -> {
                return !otherStack.equals(KNOWN_ARMOR.get(entity).get(slot));
            }
        }
        throw new RuntimeException("Equipment slot unknown!");
    }
}
