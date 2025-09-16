package martinjanas.mechanica.registries;

import martinjanas.mechanica.registries.impl.ModRegistry;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;

public class BlockItemRegistry implements ModRegistry
{
    /*public static DeferredItem<BlockItem> healer;
    public static DeferredItem<BlockItem> crafter;
    public static DeferredItem<BlockItem> barrel;*/
    public static DeferredItem<BlockItem> generator;
    public static DeferredItem<BlockItem> energy_acceptor;

    @Override
    public void register(IEventBus bus)
    {
        var items = ItemRegistry.items;

        /*healer = items.registerSimpleBlockItem("healer", BlockRegistry.healer);
        crafter = items.registerSimpleBlockItem("crafter", BlockRegistry.crafter);
        barrel = items.registerSimpleBlockItem("barrel", BlockRegistry.barrel);*/
        generator = items.registerSimpleBlockItem("generator", BlockRegistry.generator);
        energy_acceptor = items.registerSimpleBlockItem("energy_acceptor", BlockRegistry.energy_acceptor);
    }
}
