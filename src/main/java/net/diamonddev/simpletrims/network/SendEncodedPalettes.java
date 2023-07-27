package net.diamonddev.simpletrims.network;

import net.diamonddev.simpletrims.SimpleTrims;
import net.diamonddev.simpletrims.SimpleTrimsClient;
import net.diamonddev.simpletrims.data.PaletteEncoderDecoder;
import net.diamonddev.simpletrims.data.SimpleTrimsDataLoader;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class SendEncodedPalettes {
    public static Identifier SEND_ENCODED_PALETTES = SimpleTrims.id("net_send_encoded_palette");
    public static final long MAX_PACKET_CAPACITY = (long)2e+6; // 2mb

    public static PacketByteBuf[] write(PaletteEncoderDecoder.EncodedPalette[] palettes) {
        ArrayList<PacketByteBuf> bufs = new ArrayList<>();
        ArrayList<PaletteEncoderDecoder.EncodedPalette[]> allocations = new ArrayList<>();

        long bufSize = 0;
        ArrayList<PaletteEncoderDecoder.EncodedPalette> toAllocate = new ArrayList<>();
        for (var palette : palettes) {
            if (bufSize + palette.bytes.length > MAX_PACKET_CAPACITY) {
                allocations.add(toAllocate.toArray(new PaletteEncoderDecoder.EncodedPalette[0]));
                toAllocate.clear();
                bufSize = 0;
            }
            toAllocate.add(palette);
            bufSize += palette.bytes.length;
        }
        allocations.add(toAllocate.toArray(new PaletteEncoderDecoder.EncodedPalette[0]));

        for (var alloc : allocations) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(alloc.length);
            for (var palette : alloc) {
                writeSingle(palette, buf);
            }
            bufs.add(buf);
        }
        return bufs.toArray(new PacketByteBuf[0]);
    }

    public static PaletteEncoderDecoder.EncodedPalette[] read(PacketByteBuf buf) {
        int i = buf.readInt();
        ArrayList<PaletteEncoderDecoder.EncodedPalette> palettes = new ArrayList<>();
        while (i > 0) {
            palettes.add(readSingle(buf));
            i--;
        }
        return palettes.toArray(new PaletteEncoderDecoder.EncodedPalette[0]);
    }

    private static void writeSingle(PaletteEncoderDecoder.EncodedPalette palette, PacketByteBuf buf) {
        buf.writeIdentifier(palette.loc);
        buf.writeByteArray(palette.bytes);

        buf.writeBoolean(palette.emissive);
    }
    private static PaletteEncoderDecoder.EncodedPalette readSingle(PacketByteBuf buf) {
        PaletteEncoderDecoder.EncodedPalette data = new PaletteEncoderDecoder.EncodedPalette();

        data.loc = buf.readIdentifier();
        data.bytes = buf.readByteArray();

        data.emissive = buf.readBoolean();

        return data;
    }

    public static void registerReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(SEND_ENCODED_PALETTES, (client, handler, buf, responseSender) -> {
            PaletteEncoderDecoder.EncodedPalette[] palettes = read(buf);

            SimpleTrims.LOGGER.info("Received a packet containing {} Encoded Palettes from the server", palettes.length);

            for (var palette : palettes) {
                Identifier id = SimpleTrimsDataLoader.convertEncodedPaletteLocToPalettedPermutationIdentifier(palette.loc);
                SimpleTrimsClient.NETWORKED_PALETTES.put(id, palette);
            }
        });
    }
}
