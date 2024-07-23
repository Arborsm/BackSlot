package dev.arbor.backslotforge.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("SameParameterValue")
public abstract class BasePacketHandler {
    protected static SimpleChannel createChannel(ResourceLocation name) {
        String protocolVersion = ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString();
        return NetworkRegistry.ChannelBuilder.named(name)
                .clientAcceptedVersions(protocolVersion::equals)
                .serverAcceptedVersions(protocolVersion::equals)
                .networkProtocolVersion(() -> protocolVersion)
                .simpleChannel();
    }

    private int index = 0;

    protected abstract SimpleChannel getChannel();

    public abstract void initialize();
    protected <MSG extends IBackSlotPacket> void registerClientToServer(Class<MSG> type, Function<FriendlyByteBuf, MSG> decoder) {
        registerMessage(type, decoder, NetworkDirection.PLAY_TO_SERVER);
    }

    protected <MSG extends IBackSlotPacket> void registerServerToClient(Class<MSG> type, Function<FriendlyByteBuf, MSG> decoder) {
        registerMessage(type, decoder, NetworkDirection.PLAY_TO_CLIENT);
    }
    private <MSG extends IBackSlotPacket> void registerMessage(Class<MSG> type, Function<FriendlyByteBuf, MSG> decoder, NetworkDirection networkDirection) {
        getChannel().registerMessage(index++, type, IBackSlotPacket::encode, decoder, IBackSlotPacket::handle, Optional.of(networkDirection));
    }
}
