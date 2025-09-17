package martinjanas.mechanica.block_entities.impl;

import martinjanas.mechanica.api.energy.EnergyBuffer;
import martinjanas.mechanica.api.energy.impl.IEnergyBuffer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseMachineBlockEntity extends BlockEntity implements IEnergyBuffer
{
    public BaseMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState)
    {
        super(type, pos, blockState);
    }

    @Override
    public abstract EnergyBuffer GetEnergyBuffer();
}
