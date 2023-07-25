package net.diamonddev.simpletrims.network;

import net.diamonddev.simpletrims.SimpleTrims;
import net.diamonddev.simpletrims.SimpleTrimsClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class SendQuietReload {
    public static Identifier SEND_QUIET_RELOAD = SimpleTrims.id("net_send_quiet_reload");

    public static void registerReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(SEND_QUIET_RELOAD, (client, handler, buf, responseSender) -> {
            SimpleTrims.LOGGER.info("Received request to quietly reload resources from server");
            quietlyReload(client);
        });
    }

    @Environment(EnvType.CLIENT)
    public static void quietlyReload(MinecraftClient client) {
        SimpleTrimsClient.shouldQuietlyReload = true;
        client.reloadResources();
        SimpleTrimsClient.shouldQuietlyReload = false;
    }
}
