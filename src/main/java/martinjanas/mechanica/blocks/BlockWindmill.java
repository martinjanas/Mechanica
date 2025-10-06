package martinjanas.mechanica.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockWindmill extends Block
{
    public BlockWindmill(Properties properties)
    {
        super(properties);
    }

    public static VoxelShape getShape()
    {
        VoxelShape base = Block.box(0, 0, 0, 16, 2, 16);
        VoxelShape bottomBase = Block.box(1, 2, 1, 15, 3, 15);
        VoxelShape head = Block.box(2, 3, 2, 14, 27, 14);
        VoxelShape roof = Block.box(1, 27, 0, 15, 28, 18);

        // Combine all
        VoxelShape full = Shapes.or(base, bottomBase, head, roof);

        return full;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return getShape();
    }

    @Override
    protected RenderShape getRenderShape(BlockState state)
    {
        return super.getRenderShape(state);
    }
}
