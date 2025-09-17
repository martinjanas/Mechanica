package martinjanas.mechanica;

import martinjanas.mechanica.registries.*;
import martinjanas.mechanica.registries.impl.ModRegistry;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.List;

@Mod(Mechanica.MOD_ID)
public class Mechanica
{
    public static final String MOD_ID = "mechanica";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Mechanica(IEventBus bus, ModContainer container)
    {
        List<ModRegistry> mod_registries = List.of(new ItemRegistry(), new BlockRegistry(), new BlockItemRegistry(),
                new BlockEntityRegistry(), new CreativeTabRegistry(), new MenuRegistry(), new RecipeRegistry(), new EnchantmentRegistry(), new CapabilityRegistry());

        bus.addListener(this::CommonSetup);

        for (ModRegistry registry : mod_registries)
             registry.register(bus);

        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void CommonSetup(FMLCommonSetupEvent event)
    {
    }
}
