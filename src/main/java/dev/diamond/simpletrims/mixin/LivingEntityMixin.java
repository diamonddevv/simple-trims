package dev.diamond.simpletrims.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void simpletrims$applyTrimPowers(CallbackInfo ci) {
        //if (getWorld().getGameRules().getBoolean(SimpleTrims.SHOULD_PROVIDE_TRIM_POWERS)) {
        //    TrimApoliPowerUtil.updateAllTrimPowerApplications((LivingEntity)(Object)this); //todo
        //}
    }
}
