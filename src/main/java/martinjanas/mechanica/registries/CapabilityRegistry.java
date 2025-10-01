package martinjanas.mechanica.registries;

import martinjanas.mechanica.api.energy.EnergyStorage;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import martinjanas.mechanica.registries.impl.ModRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.function.Function;

public class CapabilityRegistry implements ModRegistry
{
    public static final BlockCapability<EnergyStorage, Void> ENERGY = BlockCapability.createVoid(ResourceLocation.fromNamespaceAndPath("mechanica", "energy"), EnergyStorage.class);

    @Override
    public void register(IEventBus bus)
    {
        bus.addListener(this::OnRegisterCapabilities);
    }

    private void OnRegisterCapabilities(RegisterCapabilitiesEvent event)
    {
        for (var entry : BlockEntityRegistry.block_entities.getEntries())
        {
            @SuppressWarnings("unchecked")
            BlockEntityType<BaseMachineBlockEntity> machine = (BlockEntityType<BaseMachineBlockEntity>) entry.get();

            RegisterBlockEntityCapability(event, machine, ENERGY, BaseMachineBlockEntity::GetEnergyStorage);
        }
    }

    private <BE extends BaseMachineBlockEntity, T, C> void RegisterBlockEntityCapability(RegisterCapabilitiesEvent event, BlockEntityType<BE> type, BlockCapability<T, C> cap, Function<BE, T> getter)
    {
        var cap_provider = new ICapabilityProvider<BE, C, T>()
        {
            @Override
            public T getCapability(BE be, C context)
            {
                return getter.apply(be);
            }
        };

        event.registerBlockEntity(cap, type, cap_provider);
    }
}
