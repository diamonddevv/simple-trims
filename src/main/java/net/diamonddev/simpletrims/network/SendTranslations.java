package net.diamonddev.simpletrims.network;

import net.diamonddev.simpletrims.SimpleTrims;
import net.diamonddev.simpletrims.SimpleTrimsClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class SendTranslations {
    public static Identifier SEND_TRANSLATIONS = SimpleTrims.id("net_send_translations");

    public static PacketByteBuf write(HashMap<Identifier, HashMap<String, String>> hash) {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(hash.size()); // size
        for (var entry : hash.entrySet()) {
            buf.writeIdentifier(entry.getKey()); // write identifier of material
            writeSingle(buf, entry.getValue()); // write translations
        }

        return buf;
    }
    public static HashMap<Identifier, HashMap<String, String>> read(PacketByteBuf buf) {
        HashMap<Identifier, HashMap<String, String>> hash = new HashMap<>();

        int size = buf.readInt();
        while (size > 0) {
            Identifier mat = buf.readIdentifier();
            HashMap<String, String> translations = readSingle(buf);
            hash.put(mat, translations);
            size--;
        }

        return hash;
    }

    public static void writeSingle(PacketByteBuf buf, HashMap<String, String> hash) {
        buf.writeInt(hash.size());
        hash.forEach((lang, str) -> {
            buf.writeString(lang);
            buf.writeString(str);
        });
    }
    public static HashMap<String, String> readSingle(PacketByteBuf buf) {
        HashMap<String, String> hash = new HashMap<>();
        int size = buf.readInt();
        while (size > 0) {
            String lang = buf.readString();
            String str = buf.readString();
            hash.put(lang, str);
            size--;
        }
        return hash;
    }

    public static void registerReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(SEND_TRANSLATIONS, (client, handler, buf, responseSender) -> {
            SimpleTrimsClient.NETWORKED_MATERIAL_TRANSLATION_CODE_TO_STRING = read(buf);
            SimpleTrims.LOGGER.info("Received {} material translations from server", SimpleTrimsClient.NETWORKED_MATERIAL_TRANSLATION_CODE_TO_STRING.values().size());
        });
    }
}
