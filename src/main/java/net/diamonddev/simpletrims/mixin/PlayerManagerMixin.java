package net.diamonddev.simpletrims.mixin;

import net.diamonddev.simpletrims.data.PaletteEncoderDecoder;
import net.diamonddev.simpletrims.data.SimpleTrimsDataLoader;
import net.diamonddev.simpletrims.network.SendEncodedPalettes;
import net.diamonddev.simpletrims.network.SendQuietReload;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Shadow @Final private MinecraftServer server;

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void simpletrims$onPlayerConnectToServer(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        PacketByteBuf[] bufs = SendEncodedPalettes.write(SimpleTrimsDataLoader.ENCODED_PALETTES.toArray(new PaletteEncoderDecoder.EncodedPalette[0]));
        for (var buf : bufs) {
            ServerPlayNetworking.send(player, SendEncodedPalettes.SEND_ENCODED_PALETTES, buf);
        }

        ServerPlayNetworking.send(player, SendQuietReload.SEND_QUIET_RELOAD, PacketByteBufs.create());
    }
}
