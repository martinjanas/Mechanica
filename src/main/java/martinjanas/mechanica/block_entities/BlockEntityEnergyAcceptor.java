package martinjanas.mechanica.block_entities;

import martinjanas.mechanica.Mechanica;
import martinjanas.mechanica.api.energy.RFEnergyStorage;
import martinjanas.mechanica.api.packet.EnergyUpdatePacket;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import martinjanas.mechanica.client.screens.EnergyAcceptorScreen;
import martinjanas.mechanica.client.screens.GeneratorScreen;
import martinjanas.mechanica.registries.BlockEntityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static martinjanas.mechanica.block_entities.BlockEntityGenerator.RF_PER_TICK;

public class BlockEntityEnergyAcceptor extends BaseMachineBlockEntity
{
    private RFEnergyStorage buffer = new RFEnergyStorage(100000, 1000, 1000);
    private long joules_per_tick = 25; //25 joules per tick for real 1 kWh per irl hour
    private UUID uuid;

    private int tick_counter = 0;
    private int last_sent_energy = 0;

    public boolean should_send_packet = false;

    public BlockEntityEnergyAcceptor(BlockPos pos, BlockState blockState)
    {
        super(BlockEntityRegistry.energy_acceptor.get(), pos, blockState);
        uuid = UUID.randomUUID();
    }

    public <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T e)
    {
        if (Minecraft.getInstance().screen instanceof EnergyAcceptorScreen)
            should_send_packet = true;
        else should_send_packet = false;

        if (level.isClientSide())
            return;

        for (Direction dir : Direction.values())
        {
            BlockPos neighbor_pos = pos.relative(dir);
            BlockEntity neighbor = level.getBlockEntity(neighbor_pos);
            if (neighbor == null)
                continue;

            RFEnergyStorage source_buffer = (RFEnergyStorage)Capabilities.EnergyStorage.BLOCK.getCapability(level, neighbor_pos, level.getBlockState(neighbor_pos), neighbor, null);
            if (source_buffer == null)
                continue;

            if (!(neighbor instanceof BlockEntityGenerator generator))
                continue;

            source_buffer.extractEnergy(RF_PER_TICK, false);
            buffer.receiveEnergy(RF_PER_TICK, false);
        }

        setChanged();

        if (tick_counter >= 2 && should_send_packet)
        {
            int current_energy = buffer.getEnergyStored();

            int delta = current_energy - last_sent_energy;
            if (current_energy != last_sent_energy || delta != 0)
            {
                PacketDistributor.sendToPlayersTrackingChunk(
                        (ServerLevel)level,
                        new ChunkPos(pos),
                        new EnergyUpdatePacket(pos, current_energy)
                );
                last_sent_energy = current_energy;
            }
            tick_counter = 0;
        }

        tick_counter++;
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
    public void invalidateCapabilities()
    {
        super.invalidateCapabilities();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.put("EnergyStored", buffer.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        if (tag.contains("EnergyStored", NumericTag.TAG_INT))
            buffer.deserializeNBT(registries, tag.get("EnergyStored"));
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
