package dev.diamond.simpletrims.common.network;

import dev.diamond.simpletrims.common.SimpleTrims;
import dev.diamond.simpletrims.client.SimpleTrimsClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SendQuietReload {
    public static Identifier SEND_QUIET_RELOAD = SimpleTrims.id("net_send_quiet_reload");

    public static void registerReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(SEND_QUIET_RELOAD, (client, handler, buf, responseSender) -> {
            SimpleTrims.LOGGER.info("Received request to quietly reload resources from server");
            if (SimpleTrimsClient.CONFIG.quietReloads) {
                quietlyReload(client);
            } else {
                SimpleTrims.LOGGER.info("Config says not to quiet reload; Telling player..");
                client.inGameHud.getChatHud().addMessage(Text.literal("simpletrims.noQuietReloadMessage"));
                client.inGameHud.getChatHud().addMessage(Text.literal("simpletrims.whyQuietReloadBad"));
            }
        });
    }


    /**
     * Makes a new thread, waits a few seconds, then reloads and kills the thread.
     * Should be long enough to get past the main loading section and allow other mods to behave properly, but it's a very hacky solution.
     *
     * @param client client to reload
     */
    @Environment(EnvType.CLIENT)
    public static void quietlyReload(MinecraftClient client) {

        SimpleTrimsClient.shouldQuietlyReload = true;
        client.reloadResources(true);
        SimpleTrimsClient.shouldQuietlyReload = false;
    }
}
