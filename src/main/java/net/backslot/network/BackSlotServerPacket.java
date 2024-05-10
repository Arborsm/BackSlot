package net.backslot.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class BackSlotServerPacket {

    public static void init() {
        PayloadTypeRegistry.playS2C().register(VisibilityPacket.PACKET_ID, VisibilityPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SwitchPacket.PACKET_ID, SwitchPacket.PACKET_CODEC);
        SwitchPacketReceiver receiver = new SwitchPacketReceiver();
        ServerPlayNetworking.registerGlobalReceiver(SwitchPacket.PACKET_ID, receiver);
    }

}
