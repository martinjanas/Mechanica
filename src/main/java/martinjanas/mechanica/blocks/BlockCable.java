package martinjanas.mechanica.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BlockCable extends Block
{
    private static final VoxelShape SHAPE_Z = Block.box(4, 0, 0, 12, 8, 16); // north-south (|)
    private static final VoxelShape SHAPE_X = Block.box(0, 0, 4, 16, 8, 12); // east-west  (-)

    public static DirectionProperty facing = HorizontalDirectionalBlock.FACING;
    public BlockCable(BlockBehaviour.Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(facing, Direction.NORTH));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        Direction dir = state.getValue(facing);
        if (dir.getAxis() == Direction.Axis.X)
        {
            return SHAPE_X;
        }
        return SHAPE_Z;
    }

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(facing);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction player_facing = context.getHorizontalDirection(); // always horizontal
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState neighbor_state = level.getBlockState(pos);

        // Default: face same way player is facing
        Direction cable_facing = player_facing;

        // If neighbor is a cable, adjust to match or complement it
        if (neighbor_state.getBlock() instanceof BlockCable)
        {
            Direction neighbor_facing = neighbor_state.getValue(facing);

            // Match neighbor's axis if player is roughly aligned
            if (player_facing.getAxis() == neighbor_facing.getAxis())
            {
                cable_facing = neighbor_facing;
            }
            else
            {
                // Otherwise, pick perpendicular axis
                cable_facing = (neighbor_facing == Direction.NORTH || neighbor_facing == Direction.SOUTH)
                        ? Direction.EAST
                        : Direction.NORTH;
            }
        }

        return this.defaultBlockState().setValue(facing, cable_facing);
    }

}
