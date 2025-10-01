package martinjanas.mechanica.api.event;

import martinjanas.mechanica.Mechanica;
import martinjanas.mechanica.api.network.NetworkManager;
import martinjanas.mechanica.block_entities.BlockEntityEnergyAcceptor;
import martinjanas.mechanica.block_entities.BlockEntityGenerator;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

//No need to call NeoForge.EVENT_BUS.register(ModEventManager.class); with the usage of @EventBusSubscriber
@EventBusSubscriber(modid = Mechanica.MOD_ID)
public class EventManager
{
    @SuppressWarnings("unused")
    @SubscribeEvent
    private static void OnEntityDropEvent(LivingDropsEvent event)
    {
        //ItemSolidifiedXP.OnEntityDropEvent(event);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void OnPlayerRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        var level = event.getLevel();
        if (level.isClientSide())
            return;

        BlockEntity block_entity = level.getBlockEntity(event.getPos());
        if (block_entity instanceof BlockEntityGenerator generator)
            generator.OnRightClick(event);
        else if (block_entity instanceof BlockEntityEnergyAcceptor acceptor)
            acceptor.OnRightClick(event);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void OnPlayerLeftClick(PlayerInteractEvent.LeftClickBlock event)
    {
        var level = event.getLevel();
        if (level.isClientSide())
            return;

        BlockEntity block_entity = level.getBlockEntity(event.getPos());
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void OnPlayerDestroyBlock(BlockEvent.BreakEvent event)
    {
        var level = event.getLevel();
        if (level.isClientSide())
            return;

        BlockEntity block_entity = level.getBlockEntity(event.getPos());
        if (block_entity instanceof BlockEntityGenerator generator)
            generator.OnDestroyBlock(event);
        else if (block_entity instanceof BlockEntityEnergyAcceptor acceptor)
            acceptor.OnDestroyBlock(event);
    }

    @SubscribeEvent
    public static void OnServerTick(PlayerTickEvent.Post event)
    {
        var level = event.getEntity().level();
        if (level.isClientSide())
            return;

        NetworkManager.Get().OnServerTick();
    }
}
