package martinjanas.mechanica.api.packet;

import martinjanas.mechanica.api.network.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RegisterNetworkPacket(String name) implements CustomPacketPayload
{
    public static final Type<RegisterNetworkPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("mechanica", "register_network"));

    public static final StreamCodec<FriendlyByteBuf, RegisterNetworkPacket> CODEC =
            CustomPacketPayload.codec(RegisterNetworkPacket::encode, RegisterNetworkPacket::decode);

    private static void encode(RegisterNetworkPacket pkt, FriendlyByteBuf buf)
    {
        buf.writeUtf(pkt.name);
    }

    private static RegisterNetworkPacket decode(FriendlyByteBuf buf)
    {
        return new RegisterNetworkPacket(buf.readUtf());
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
