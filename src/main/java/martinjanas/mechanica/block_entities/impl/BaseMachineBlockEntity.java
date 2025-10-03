package martinjanas.mechanica.block_entities.impl;

import martinjanas.mechanica.api.energy.EnergyStorage;
import martinjanas.mechanica.api.energy.impl.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public abstract class BaseMachineBlockEntity extends BlockEntity implements IEnergyStorage
{
    public BaseMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState)
    {
        super(type, pos, blockState);
    }

    @Override
    public abstract EnergyStorage GetEnergyStorage();

    public abstract UUID GetUUID();
}
