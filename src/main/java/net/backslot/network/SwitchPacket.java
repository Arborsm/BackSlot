package net.backslot.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SwitchPacket(int slotId) implements CustomPayload {

    public static final CustomPayload.Id<SwitchPacket> PACKET_ID = new CustomPayload.Id<>(new Identifier("backslot", "switch_item"));

    public static final PacketCodec<RegistryByteBuf, SwitchPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeInt(value.slotId);
    }, buf -> new SwitchPacket(buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
