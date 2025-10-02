package martinjanas.mechanica.block_entities;

import martinjanas.mechanica.api.energy.EnergyStorage;
import martinjanas.mechanica.api.network.EnergyNetwork;
import martinjanas.mechanica.api.network.NetworkManager;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import martinjanas.mechanica.client.screens.GeneratorScreen;
import martinjanas.mechanica.registries.BlockEntityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
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
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

public class BlockEntityGenerator extends BaseMachineBlockEntity
{
    public long JOULES_PER_TICK = 3000; //25 joules per tick for real 1 kWh per irl hour

    private EnergyStorage buffer = new EnergyStorage(1.0, 1.0, 1.0);
    private boolean network_registered = false;
    private boolean network_joined = false;
    private int ticks = 0;

    public BlockEntityGenerator(BlockPos pos, BlockState blockState)
    {
        super(BlockEntityRegistry.generator.get(), pos, blockState);
    }

    public <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T e)
    {
        if (level.isClientSide())
            return;

        buffer.Insert(JOULES_PER_TICK);
        setChanged();

        level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);

        System.out.println("Generator: " + buffer.toString());

        ticks++;
    }

    public void OnRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        if (!network_registered)
            network_registered = NetworkManager.Get().Register("Energy1", EnergyNetwork::new);

        if (!network_joined)
            network_joined = NetworkManager.Get().Join("Energy1", this);

        var level = event.getLevel();
        if (level != null && !level.isClientSide())
        {
            var pos = event.getPos();
            var state = level.getBlockState(pos);
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
        }
    }

    public void OnDestroyBlock(BlockEvent.BreakEvent event)
    {
        network_joined = NetworkManager.Get().Disconnect("Energy1", this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.putLong("EnergyStored", buffer.GetStored());

        setChanged();
    }

    @Override
    public EnergyStorage GetEnergyStorage()
    {
        return buffer;
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
