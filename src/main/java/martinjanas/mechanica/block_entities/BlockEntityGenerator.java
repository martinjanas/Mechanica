package martinjanas.mechanica.block_entities;

import martinjanas.mechanica.api.energy.EnergyStorage;
import martinjanas.mechanica.api.network.EnergyNetwork;
import martinjanas.mechanica.api.network.NetworkManager;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import martinjanas.mechanica.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public class BlockEntityGenerator extends BaseMachineBlockEntity
{
    public long JOULES_PER_TICK = 6000; //25 joules per tick for real 1 kWh per irl hour

    private EnergyStorage buffer = new EnergyStorage(1.0, 1.0, 1.0);
    private boolean network_registered = false;
    private boolean network_joined = false;

    public BlockEntityGenerator(BlockPos pos, BlockState blockState)
    {
        super(BlockEntityRegistry.generator.get(), pos, blockState);
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

    public <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T e)
    {
        if (level.isClientSide())
            return;

        buffer.Insert(JOULES_PER_TICK);
        setChanged();

        System.out.println("Generator: " + buffer.toString());
    }


    @Override
    public void invalidateCapabilities()
    {
        super.invalidateCapabilities();
    }

    @Override
    public EnergyStorage GetEnergyStorage()
    {
        return buffer;
    }

    public void OnRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        if (!network_registered)
            network_registered = NetworkManager.Get().Register("Energy1", EnergyNetwork::new);

        if (!network_joined)
            network_joined = NetworkManager.Get().Join("Energy1", this);
    }

    public void OnDestroyBlock(BlockEvent.BreakEvent event)
    {
        network_joined = NetworkManager.Get().Disconnect("Energy1", this);
    }
}
