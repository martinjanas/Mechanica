package martinjanas.mechanica.blocks;

import martinjanas.mechanica.block_entities.BlockEntityEnergyAcceptor;
import martinjanas.mechanica.block_entities.BlockEntityGenerator;
import martinjanas.mechanica.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockEnergyAcceptor extends Block implements EntityBlock
{
    public BlockEnergyAcceptor(Properties properties)
    {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return BlockEntityRegistry.energy_acceptor.get().create(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return type == BlockEntityRegistry.energy_acceptor.get() ? (lvl, pos, st, be) ->
        {
            if (be instanceof BlockEntityEnergyAcceptor energy_acceptor)
                energy_acceptor.tick(lvl, pos, st, be);
        } : null;
    }
}
