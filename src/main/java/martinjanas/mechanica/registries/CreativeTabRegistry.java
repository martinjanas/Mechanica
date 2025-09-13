package martinjanas.mechanica.registries;

import martinjanas.mechanica.Mechanica;
import martinjanas.mechanica.registries.impl.ModRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreativeTabRegistry implements ModRegistry
{
    public static final DeferredRegister<CreativeModeTab> tabs = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Mechanica.MOD_ID);

    public static DeferredHolder<CreativeModeTab, CreativeModeTab> creative_tab;

    @Override
    public void register(IEventBus bus)
    {
        var builder = CreativeModeTab.builder();
        builder.title(Component.translatable("itemGroup.mechanica_creative_tab"));
        builder.withTabsBefore(CreativeModeTabs.COMBAT);
        builder.icon(() -> new ItemStack(Items.DIAMOND));
        builder.displayItems((params, output) -> {
            for (DeferredHolder<Item, ? extends Item> item_stack : ItemRegistry.items.getEntries())
            {
                output.accept(item_stack.get());
            }
        });
        CreativeModeTab tab = builder.build();

        creative_tab = tabs.register(Mechanica.MOD_ID.concat("_creative_tab"), () -> tab);

        tabs.register(bus);
    }
}
