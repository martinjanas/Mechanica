package martinjanas.mechanica.registries;

import martinjanas.mechanica.Mechanica;
import martinjanas.mechanica.registries.impl.ModRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MenuRegistry implements ModRegistry
{
    public static final DeferredRegister<MenuType<?>> menus = DeferredRegister.create(BuiltInRegistries.MENU, Mechanica.MOD_ID);

    //public static DeferredHolder<MenuType<?>, MenuType<CrafterMenu>> crafter_menu;
    //public static DeferredHolder<MenuType<?>, MenuType<BarrelMenu>> barrel_menu;
    //public static DeferredHolder<MenuType<?>, MenuType<GeneratorMenu>> generator_menu;

    @Override
    public void register(IEventBus bus)
    {
        //crafter_menu = menus.register("crafter_menu", () -> new MenuType<>((id, playerInventory) -> new CrafterMenu(id, playerInventory, new SimpleContainer(3)), FeatureFlags.DEFAULT_FLAGS));

        //barrel_menu = menus.register("barrel_menu", () -> IMenuTypeExtension.create(BarrelMenu::new));

        /*generator_menu = menus.register("generator_menu",
                () -> IMenuTypeExtension.create((containerId, inventory, buffer) -> new GeneratorMenu(containerId, inventory, buffer))
        );*/

        menus.register(bus);
    }
}
