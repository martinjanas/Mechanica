package martinjanas.mechanica.api.packet;

import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record JoinNetworkPacket(String name, BlockPos pos) implements CustomPacketPayload
{
    public static final Type<JoinNetworkPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("mechanica", "join_network"));
    public static final StreamCodec<FriendlyByteBuf, JoinNetworkPacket> CODEC = CustomPacketPayload.codec(JoinNetworkPacket::encode, JoinNetworkPacket::decode);

    private static void encode(JoinNetworkPacket pkt, FriendlyByteBuf buf)
    {
        buf.writeUtf(pkt.name);
        buf.writeBlockPos(pkt.pos);
    }

    private static JoinNetworkPacket decode(FriendlyByteBuf buf)
    {
        String name = buf.readUtf();
        BlockPos pos = buf.readBlockPos();
        return new JoinNetworkPacket(name, pos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}

