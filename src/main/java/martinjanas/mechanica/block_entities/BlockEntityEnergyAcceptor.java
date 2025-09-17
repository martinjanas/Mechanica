package martinjanas.mechanica.block_entities;

import martinjanas.mechanica.api.energy.EnergyBuffer;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import martinjanas.mechanica.registries.BlockEntityRegistry;
import martinjanas.mechanica.registries.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;

public class BlockEntityEnergyAcceptor extends BaseMachineBlockEntity
{
    private EnergyBuffer buffer = new EnergyBuffer(1.0, 1.0, 1.0);

    private long joules_per_tick = 25; //25 joules per tick for real 1 kWh per irl hour

    public BlockEntityEnergyAcceptor(BlockPos pos, BlockState blockState)
    {
        super(BlockEntityRegistry.energy_acceptor.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.putLong("EnergyStored", buffer.get_joules());

        setChanged();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);

        if (tag.contains("EnergyStored" /*, NumericTag.TAG_LONG*/))
            buffer.set_joules(tag.getLong("EnergyStored"));

        setChanged();
    }

    public <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T e)
    {
        if (level.isClientSide())
            return;

        long maxPerTick = 12000;

        for (Direction dir : Direction.values())
        {
            BlockPos neighborPos = pos.relative(dir);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);

            if (neighbor != null)
            {
                BlockCapability<EnergyBuffer, Void> energyCap = CapabilityRegistry.ENERGY;
                EnergyBuffer sourceBuffer = energyCap.getCapability(level, neighborPos, level.getBlockState(neighborPos), neighbor, null);

                if (sourceBuffer != null)
                {
                    sourceBuffer.extract(maxPerTick);
                    buffer.insert(maxPerTick);
                }
            }
        }

        setChanged();

        System.out.println("EnergyAcceptor: " + buffer.toString());
    }

    @Override
    public EnergyBuffer GetEnergyBuffer()
    {
        return buffer;
    }
}
