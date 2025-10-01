package martinjanas.mechanica.blocks;

import martinjanas.mechanica.block_entities.BlockEntityGenerator;
import martinjanas.mechanica.blocks.impl.BaseMachineBlock;
import martinjanas.mechanica.registries.BlockEntityRegistry;
import martinjanas.mechanica.registries.BlockItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BlockGenerator extends BaseMachineBlock
{
    public BlockGenerator(Properties properties)
    {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return BlockEntityRegistry.generator.get().create(pos, state);
    }


    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston)
    {
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return type == BlockEntityRegistry.generator.get() ? (lvl, pos, st, be) ->
        {
            if (be instanceof BlockEntityGenerator generator)
                generator.tick(lvl, pos, st, be);
        } : null;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params)
    {
        List<ItemStack> list = new ArrayList();
        list.add(new ItemStack(BlockItemRegistry.generator.asItem()));

        return list;
    }
}
