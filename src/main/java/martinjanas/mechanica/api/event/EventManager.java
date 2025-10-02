package martinjanas.mechanica.api.event;

import martinjanas.mechanica.Mechanica;
import martinjanas.mechanica.api.network.EnergyNetwork;
import martinjanas.mechanica.api.network.NetworkManager;
import martinjanas.mechanica.api.packet.JoinNetworkPacket;
import martinjanas.mechanica.api.packet.RegisterNetworkPacket;
import martinjanas.mechanica.block_entities.BlockEntityEnergyAcceptor;
import martinjanas.mechanica.block_entities.BlockEntityGenerator;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

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
        /*if (block_entity instanceof BlockEntityGenerator generator)
            generator.OnRightClick(event);
        else if (block_entity instanceof BlockEntityEnergyAcceptor acceptor)
            acceptor.OnRightClick(event);*/
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
        /*if (block_entity instanceof BlockEntityGenerator generator)
            generator.OnDestroyBlock(event);
        else if (block_entity instanceof BlockEntityEnergyAcceptor acceptor)
            acceptor.OnDestroyBlock(event);*/
    }

    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event)
    {
        event.registrar("mechanica")
                .playToServer(
                        RegisterNetworkPacket.TYPE,
                        RegisterNetworkPacket.CODEC,
                        (pkt, context) -> {
                            context.enqueueWork(() -> {
                                NetworkManager.Get().Register(pkt.name(), EnergyNetwork::new);
                            });
                        }
                );

        event.registrar("mechanica").playToServer(
                JoinNetworkPacket.TYPE,
                JoinNetworkPacket.CODEC,
                (pkt, context) -> {
                    context.enqueueWork(() -> {
                        ServerPlayer player = (ServerPlayer) context.player();
                        Level level = player.level();

                        BlockEntity be = level.getBlockEntity(pkt.pos());
                        if (be instanceof BaseMachineBlockEntity machine) {
                            NetworkManager.Get().Join(pkt.name(), machine);
                        }
                    });
                }
        );
    }

    @SubscribeEvent
    public static void OnServerTick(LevelTickEvent.Post event)
    {
        if (!(event.getLevel() instanceof ServerLevel))
            return;

        NetworkManager.Get().OnServerTick();
    }
}
