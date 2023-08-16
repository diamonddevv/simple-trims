package net.diamonddev.simpletrims.common.network;

import net.diamonddev.simpletrims.common.SimpleTrims;
import net.diamonddev.simpletrims.client.SimpleTrimsClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class SendAssetNamesToPalettePaths {
    public static Identifier SEND_ASSETS = SimpleTrims.id("net_send_assets");

    public static PacketByteBuf write(HashMap<String, String> hash) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(hash.size());
        hash.forEach((asset, path) -> {
            buf.writeString(asset);
            buf.writeString(path);
        });
        return buf;
    }

    public static HashMap<String, String> read(PacketByteBuf buf) {
        HashMap<String, String> hash = new HashMap<>();
        int size = buf.readInt();
        while (size > 0) {
            String asset = buf.readString();
            String path = buf.readString();
            hash.put(asset, path);
            size--;
        }
        return hash;
    }

    public static void registerReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(SEND_ASSETS, (client, handler, buf, responseSender) -> {
            SimpleTrimsClient.NETWORKED_ASSETNAME_TO_PATH_HASH = read(buf);
            SimpleTrims.LOGGER.info("Received a packet containing {} Assets-to-Paths from the server", SimpleTrimsClient.NETWORKED_ASSETNAME_TO_PATH_HASH.size());
        });
    }
}
