package net.diamonddev.simpletrims.network;

import net.diamonddev.simpletrims.SimpleTrims;
import net.diamonddev.simpletrims.SimpleTrimsClient;
import net.diamonddev.simpletrims.data.PaletteEncoderDecoder;
import net.diamonddev.simpletrims.data.SimpleTrimDataLoader;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SendEncodedPalette {
    public static Identifier SEND_ENCODED_PALETTE = SimpleTrims.id("net_send_encoded_palette");

    public static PacketByteBuf write(PaletteEncoderDecoder.EncodedPalette palette) {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeIdentifier(palette.loc);
        buf.writeInt(palette.width);
        buf.writeIntArray(palette.colors);

        return buf;
    }

    public static PaletteEncoderDecoder.EncodedPalette read(PacketByteBuf buf) {
        PaletteEncoderDecoder.EncodedPalette data = new PaletteEncoderDecoder.EncodedPalette();

        data.loc = buf.readIdentifier();
        data.width = buf.readInt();
        data.colors = buf.readIntArray();

        return data;
    }

    public static void registerReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(SEND_ENCODED_PALETTE, (client, handler, buf, responseSender) -> {
            PaletteEncoderDecoder.EncodedPalette palette = read(buf);
            Identifier id = SimpleTrimDataLoader.convertEncodedPaletteLocToPalettedPermutationIdenfier(palette.loc);
            SimpleTrimsClient.NETWORKED_PALETTES.put(id, palette);
        });
    }
}
