package martinjanas.mechanica.api.event;

import martinjanas.mechanica.Mechanica;
import martinjanas.mechanica.api.network.*;
import martinjanas.mechanica.api.packet.*;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import martinjanas.mechanica.client.widgets.NetworkSettingsWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import java.util.List;

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

        ServerPlayer sl = (ServerPlayer)event.getPlayer();

        BlockEntity block_entity = level.getBlockEntity(event.getPos());
        /*if (block_entity instanceof BlockEntityGenerator generator)
            generator.OnDestroyBlock(event);
        else if (block_entity instanceof BlockEntityEnergyAcceptor acceptor)
            acceptor.OnDestroyBlock(event);*/

        if (block_entity instanceof BaseMachineBlockEntity machine)
        {
            var networks = ServerNetworkManager.Get().GetNetworks();
            for (var network : networks.values())
            {
                if (!network.HasDevice(machine.GetUUID()))
                    continue;

                ServerNetworkManager.Get().Disconnect(network.GetName(), machine);
            }
        }
    }

    @SubscribeEvent
    public static void RegisterPackets(RegisterPayloadHandlersEvent event)
    {
        event.registrar("mechanica").playToServer(RegisterNetworkPacket.TYPE, RegisterNetworkPacket.CODEC, (pkt, context) ->
        {
            context.enqueueWork(() ->
            {
                ServerPlayer player = (ServerPlayer) context.player();
                if (player != null)
                {
                    boolean registered = ServerNetworkManager.Get().Register(pkt.name(), () -> new EnergyNetwork(pkt.name()));

                    if (registered)
                    {
                        List<NetworkData> data_list = ServerNetworkManager.Get().GetNetworks().values().stream().map(net -> new NetworkData(net.GetName(), NetworkType.ENERGY, net.GetDevicePositions())).toList();
                        player.connection.send(new SyncNetworksPacket(data_list));
                    }
                }
            });
        });

        event.registrar("mechanica").playToServer(JoinNetworkPacket.TYPE, JoinNetworkPacket.CODEC, (pkt, context) ->
        {
            context.enqueueWork(() ->
            {
                ServerPlayer player = (ServerPlayer) context.player();
                if (player != null)
                {
                    BlockPos pos = pkt.pos();
                    Level world = context.player().level();
                    BlockEntity be = world.getBlockEntity(pos);
                    if (be instanceof BaseMachineBlockEntity device)
                    {
                        ServerNetworkManager.Get().Join(pkt.name(), device);

                        List<NetworkData> data_list = ServerNetworkManager.Get().GetNetworks().values().stream().map(net -> new NetworkData(net.GetName(), NetworkType.ENERGY, net.GetDevicePositions())).toList();
                        player.connection.send(new SyncNetworksPacket(data_list));
                    }
                }
            });
        });

        event.registrar("mechanica").playToClient(SyncNetworksPacket.TYPE, SyncNetworksPacket.CODEC, (pkt, context) ->
        {
            context.enqueueWork(() ->
            {
                ClientNetworkManager.Get().UpdateNetworksFromData(pkt.networks(), context.player().level());

                if (NetworkSettingsWidget.INSTANCE != null)
                    NetworkSettingsWidget.INSTANCE.RefreshNetworkList();
            });
        });

        event.registrar("mechanica").playToClient(EnergyUpdatePacket.TYPE, EnergyUpdatePacket.CODEC, (pkt, context) ->
        {
            context.enqueueWork(() ->
            {
                BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pkt.pos());
                if (be instanceof BaseMachineBlockEntity machine)
                    machine.GetEnergyStorage().SetStored(pkt.energy());
            });
        });
    }

    @SubscribeEvent
    public static void OnServerTick(LevelTickEvent.Post event)
    {
        if (!(event.getLevel() instanceof ServerLevel level))
            return;

        if (event.getLevel() != null)
            ServerNetworkManager.Get().OnServerTick(level);
    }
}
