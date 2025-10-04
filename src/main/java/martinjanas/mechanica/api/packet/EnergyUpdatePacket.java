package martinjanas.mechanica.api.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record EnergyUpdatePacket(BlockPos pos, int energy) implements CustomPacketPayload
{
    public static final Type<EnergyUpdatePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("mechanica", "energy_update"));
    public static final StreamCodec<FriendlyByteBuf, EnergyUpdatePacket> CODEC = CustomPacketPayload.codec(EnergyUpdatePacket::encode, EnergyUpdatePacket::decode);

    private static void encode(EnergyUpdatePacket pkt, FriendlyByteBuf buf)
    {
        buf.writeBlockPos(pkt.pos());
        buf.writeInt(pkt.energy());
    }

    private static EnergyUpdatePacket decode(FriendlyByteBuf buf)
    {
        BlockPos pos = buf.readBlockPos();
        int energy = buf.readInt();

        return new EnergyUpdatePacket(pos, energy);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}


