package martinjanas.mechanica.api.packet;

import martinjanas.mechanica.api.network.NetworkData;
import martinjanas.mechanica.api.network.NetworkType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record SyncNetworksPacket(List<NetworkData> networks) implements CustomPacketPayload
{
    public static final Type<SyncNetworksPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("mechanica", "sync_networks"));
    public static final StreamCodec<FriendlyByteBuf, SyncNetworksPacket> CODEC = CustomPacketPayload.codec(SyncNetworksPacket::encode, SyncNetworksPacket::decode);

    private static void encode(SyncNetworksPacket pkt, FriendlyByteBuf buf)
    {
        buf.writeVarInt(pkt.networks().size());

        for (NetworkData network : pkt.networks())
        {
            buf.writeUtf(network.network_name());
            buf.writeEnum(network.network_type());

            List<BlockPos> device_positions = network.device_positions();
            buf.writeVarInt(device_positions.size());

            for (BlockPos pos : device_positions)
                buf.writeBlockPos(pos);
        }
    }

    private static SyncNetworksPacket decode(FriendlyByteBuf buf)
    {
        int network_count = buf.readVarInt();
        List<NetworkData> networks = new ArrayList<>();

        for (int i = 0; i < network_count; i++)
        {
            String name = buf.readUtf();
            NetworkType type = buf.readEnum(NetworkType.class);

            int device_count = buf.readVarInt();
            List<BlockPos> device_positions = new ArrayList<>();
            for (int d = 0; d < device_count; d++)
                device_positions.add(buf.readBlockPos());

            networks.add(new NetworkData(name, type, device_positions));
        }

        return new SyncNetworksPacket(networks);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
