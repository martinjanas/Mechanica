package martinjanas.mechanica.block_entities;

import martinjanas.mechanica.api.energy.RFEnergyStorage;
import martinjanas.mechanica.api.packet.EnergyUpdatePacket;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import martinjanas.mechanica.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import java.util.UUID;

public class BlockEntityGenerator extends BaseMachineBlockEntity
{
    public int RF_PER_TICK = 20;
    private RFEnergyStorage buffer = new RFEnergyStorage(100000, 200, 200);
    private UUID uuid;

    public BlockEntityGenerator(BlockPos pos, BlockState blockState)
    {
        super(BlockEntityRegistry.generator.get(), pos, blockState);
        uuid = UUID.randomUUID();
    }

    public <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T e)
    {
        if (level.isClientSide())
            return;

        buffer.receiveEnergy(RF_PER_TICK, false);
        setChanged();

        int energy = buffer.getEnergyStored();
        ServerLevel sl = (ServerLevel)level;
        PacketDistributor.sendToPlayersTrackingChunk(sl, new ChunkPos(pos), new EnergyUpdatePacket(pos, energy));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.put("EnergyStored", buffer.serializeNBT(registries));
    }

    @Override
    public RFEnergyStorage GetEnergyStorage()
    {
        return buffer;
    }

    @Override
    public UUID GetUUID()
    {
        return uuid;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        if (tag.contains("EnergyStored", NumericTag.TAG_INT))
            buffer.deserializeNBT(registries, tag.get("EnergyStored"));
    }

    @Override
    public void invalidateCapabilities()
    {
        super.invalidateCapabilities();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries)
    {
        var tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider)
    {
        loadAdditional(tag, lookupProvider);
        super.handleUpdateTag(tag, lookupProvider);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider)
    {
        handleUpdateTag(pkt.getTag(), lookupProvider);
        //super.onDataPacket(net, pkt, lookupProvider);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
