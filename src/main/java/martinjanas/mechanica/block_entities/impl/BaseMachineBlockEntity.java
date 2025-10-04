package martinjanas.mechanica.block_entities.impl;

import martinjanas.mechanica.api.energy.RFEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public abstract class BaseMachineBlockEntity extends BlockEntity
{
    public BaseMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState)
    {
        super(type, pos, blockState);
    }

    public abstract RFEnergyStorage GetEnergyStorage();
    public abstract UUID GetUUID();
}
