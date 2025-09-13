package martinjanas.mechanica.registries;

import martinjanas.mechanica.Mechanica;
import martinjanas.mechanica.registries.impl.ModRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EnchantmentRegistry implements ModRegistry
{
    public static final DeferredRegister<Enchantment> enchantments = DeferredRegister.create(Registries.ENCHANTMENT, Mechanica.MOD_ID);

    //public static final DeferredHolder<Enchantment, AU_EfficiencyBoots> BOOT_MAGIC = enchantments.register("boot_magic", AU_EfficiencyBoots::new);

    @Override
    public void register(IEventBus bus)
    {
        enchantments.register(bus);
    }
}
