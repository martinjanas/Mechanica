package martinjanas.mechanica.blocks;

import martinjanas.mechanica.block_entities.BlockEntityEnergyAcceptor;
import martinjanas.mechanica.block_entities.BlockEntityGenerator;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import martinjanas.mechanica.blocks.impl.BaseMachineBlock;
import martinjanas.mechanica.registries.BlockEntityRegistry;
import martinjanas.mechanica.registries.BlockItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlockEnergyAcceptor extends BaseMachineBlock
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

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params)
    {
        List<ItemStack> list = new ArrayList();
        list.add(new ItemStack(BlockItemRegistry.energy_acceptor.asItem()));

        return list;
    }
}
