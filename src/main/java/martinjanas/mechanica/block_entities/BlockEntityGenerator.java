package martinjanas.mechanica.block_entities;

import martinjanas.mechanica.api.energy.EnergyBuffer;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import martinjanas.mechanica.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityGenerator extends BaseMachineBlockEntity
{
    private EnergyBuffer buffer = new EnergyBuffer(1.0, 1.0, 1.0);

    private long joules_per_tick = 25; //25 joules per tick for real 1 kWh per irl hour

    public BlockEntityGenerator(BlockPos pos, BlockState blockState)
    {
        super(BlockEntityRegistry.generator.get(), pos, blockState);
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

        long joules_per_tick = 18000; //18000 joules in 1 tick - 1 kWh in 10 real seconds
        buffer.insert(joules_per_tick);
        setChanged();

        System.out.println("Generator: " + buffer.toString());
    }


    @Override
    public void invalidateCapabilities()
    {
        super.invalidateCapabilities();
    }

    @Override
    public EnergyBuffer GetEnergyBuffer()
    {
        return buffer;
    }
}
