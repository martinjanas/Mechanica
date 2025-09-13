package martinjanas.mechanica.registries;

import martinjanas.mechanica.Mechanica;
import martinjanas.mechanica.registries.impl.ModRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RecipeRegistry implements ModRegistry
{
    public static final DeferredRegister<RecipeSerializer<?>> serializers = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Mechanica.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> recipe_types = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, Mechanica.MOD_ID);

    //public static DeferredHolder<RecipeType<?>, RecipeType<CrafterRecipe>> crafter_type;
    //public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrafterRecipe>> crafter_serializer;

    @Override
    public void register(IEventBus bus)
    {
        //crafter_type = recipe_types.register("crafter", () -> new RecipeType<CrafterRecipe>() { });
        //crafter_serializer = serializers.register("crafter", CrafterSerializer::new);

        recipe_types.register(bus);
        serializers.register(bus);
    }
}
