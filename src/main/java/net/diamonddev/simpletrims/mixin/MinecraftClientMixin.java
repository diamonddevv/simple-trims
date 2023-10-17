package net.diamonddev.simpletrims.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.diamonddev.simpletrims.client.SimpleTrimsClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @WrapWithCondition(
            method = "reloadResources(ZLnet/minecraft/client/MinecraftClient$LoadingContext;)Ljava/util/concurrent/CompletableFuture;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setOverlay(Lnet/minecraft/client/gui/screen/Overlay;)V")
    )
    private boolean simpletrims$dontDisplayReloadOverlayForQuietReload(MinecraftClient instance, Overlay overlay) {
        return !SimpleTrimsClient.shouldQuietlyReload;
    }
}
