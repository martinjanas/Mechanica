package martinjanas.mechanica.registries;

import martinjanas.mechanica.Mechanica;
import martinjanas.mechanica.blocks.BlockGenerator;
import martinjanas.mechanica.registries.impl.ModRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegistry implements ModRegistry
{
    public static DeferredRegister.Blocks blocks = DeferredRegister.createBlocks(Mechanica.MOD_ID);

    /*public static DeferredBlock<Block> healer;
    public static DeferredBlock<Block> crafter;
    public static DeferredBlock<Block> barrel;*/
    public static DeferredBlock<Block> generator;

    @Override
    public void register(IEventBus bus)
    {
        /*healer = blocks.register("healer", () -> new BlockHealer(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
        crafter = blocks.register("crafter", () -> new BlockCrafter(BlockBehaviour.Properties.of().strength(50f, 1200f)));
        barrel = blocks.register("barrel", () -> new BlockBarrel(BlockBehaviour.Properties.of().strength(1f, 1200f)));*/
        generator = blocks.register("generator", () -> new BlockGenerator(BlockBehaviour.Properties.of().strength(1f, 1200f)));

        blocks.register(bus);
    }
}
