package martinjanas.mechanica.block_entities;

import martinjanas.mechanica.api.energy.EnergyStorage;
import martinjanas.mechanica.api.network.NetworkManager;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import martinjanas.mechanica.registries.BlockEntityRegistry;
import martinjanas.mechanica.registries.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

public class BlockEntityEnergyAcceptor extends BaseMachineBlockEntity
{
    private EnergyStorage buffer = new EnergyStorage(1.0, 1.0, 1.0);
    private long joules_per_tick = 25; //25 joules per tick for real 1 kWh per irl hour

    //TODO: Desync between server & client when sending energy across network - fix
    public BlockEntityEnergyAcceptor(BlockPos pos, BlockState blockState)
    {
        super(BlockEntityRegistry.energy_acceptor.get(), pos, blockState);
    }

    public <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T e)
    {
        if (level.isClientSide())
            return;

        for (Direction dir : Direction.values())
        {
            BlockPos neighbor_pos = pos.relative(dir);
            BlockEntity neighbor = level.getBlockEntity(neighbor_pos);
            if (neighbor == null)
                continue;

            EnergyStorage source_buffer = CapabilityRegistry.ENERGY.getCapability(level, neighbor_pos, level.getBlockState(neighbor_pos), neighbor, null);
            if (source_buffer == null)
                continue;

            if (!(neighbor instanceof BlockEntityGenerator generator))
                continue;

            source_buffer.Extract(generator.JOULES_PER_TICK);
            buffer.Insert(generator.JOULES_PER_TICK);
        }

        setChanged();

        level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);

        System.out.println("EnergyAcceptor: " + buffer.toString());
    }

    @Override
    public EnergyStorage GetEnergyStorage()
    {
        return buffer;
    }

    @Override
    public void invalidateCapabilities()
    {
        super.invalidateCapabilities();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.putLong("EnergyStored", buffer.GetStored());

        setChanged();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);

        if (tag.contains("EnergyStored" /*, NumericTag.TAG_LONG*/))
            buffer.SetStored(tag.getLong("EnergyStored"));

        setChanged();
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
        saveAdditional(tag, lookupProvider);
        super.handleUpdateTag(tag, lookupProvider);
    }


    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider)
    {
        super.onDataPacket(net, pkt, lookupProvider);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket()
    {
        //return super.getUpdatePacket();
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
