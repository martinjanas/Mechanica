package martinjanas.mechanica.registries;

import martinjanas.mechanica.Mechanica;
import martinjanas.mechanica.api.energy.EnergyBuffer;
import martinjanas.mechanica.api.energy.IEnergyBlock;
import martinjanas.mechanica.block_entities.BlockEntityGenerator;
import martinjanas.mechanica.blocks.BlockEnergyAcceptor;
import martinjanas.mechanica.blocks.BlockGenerator;
import martinjanas.mechanica.registries.impl.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;

public class CapabilityRegistry implements ModRegistry
{
    public static final BlockCapability<EnergyBuffer, Void> ENERGY = BlockCapability.createVoid(ResourceLocation.fromNamespaceAndPath("mechanica", "energy"), EnergyBuffer.class);

    @Override
    public void register(IEventBus bus)
    {
        bus.addListener(this::OnRegisterCapabilities);
    }

    private void OnRegisterCapabilities(RegisterCapabilitiesEvent event)
    {
        event.registerBlockEntity(
                ENERGY,
                BlockEntityRegistry.generator.get(),
                new ICapabilityProvider<BlockEntity, Void, EnergyBuffer>()
                {
                    @Override
                    public EnergyBuffer getCapability(BlockEntity be, Void context) {
                        if (be instanceof IEnergyBlock energyBlock) {
                            return energyBlock.GetEnergyBuffer();
                        }
                        return null;
                    }
                }
        );

        event.registerBlockEntity(
                ENERGY,
                BlockEntityRegistry.energy_acceptor.get(),
                new ICapabilityProvider<BlockEntity, Void, EnergyBuffer>()
                {
                    @Override
                    public EnergyBuffer getCapability(BlockEntity be, Void context) {
                        if (be instanceof IEnergyBlock energyBlock) {
                            return energyBlock.GetEnergyBuffer();
                        }
                        return null;
                    }
                }
        );
    }
}
