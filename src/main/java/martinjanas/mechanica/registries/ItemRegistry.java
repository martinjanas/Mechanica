package martinjanas.mechanica.registries;

import martinjanas.mechanica.Mechanica;
import martinjanas.mechanica.registries.impl.ModRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry implements ModRegistry
{
    public static final DeferredRegister.Items items = DeferredRegister.createItems(Mechanica.MOD_ID);

    //public static DeferredItem<Item> solidified_xp;

    @Override
    public void register(IEventBus bus)
    {
        //solidified_xp = items.register("solidified_xp", ItemSolidifiedXP::new);

        items.register(bus);
    }
}
