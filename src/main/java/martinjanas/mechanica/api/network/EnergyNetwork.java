package martinjanas.mechanica.api.network;

import martinjanas.mechanica.api.network.impl.BaseNetwork;
import martinjanas.mechanica.api.packet.EnergyUpdatePacket;
import martinjanas.mechanica.block_entities.BlockEntityGenerator;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public class EnergyNetwork extends BaseNetwork<BaseMachineBlockEntity>
{
    long joules_per_tick = 0;

    public EnergyNetwork(String network_name)
    {
        super(network_name);
    }

    @Override
    public List<UUID> GetDeviceUUIDs()
    {
        return devices.values().stream()
                .map(BaseMachineBlockEntity::GetUUID)
                .toList();
    }

    @Override
    public void OnServerTick(Level level)
    {
        for (var device : devices.values())
        {
            if (device instanceof BlockEntityGenerator generator && devices.size() >= 2)
            {
                generator.GetEnergyStorage().Extract(generator.JOULES_PER_TICK);
                generator.setChanged();
                joules_per_tick = generator.JOULES_PER_TICK;

                ServerLevel sl = (ServerLevel)level;

                long energy = generator.GetEnergyStorage().GetStored();
                sl.getChunkSource().chunkMap.getPlayers(new ChunkPos(generator.getBlockPos()), false)
                        .forEach(player -> player.connection.send(new EnergyUpdatePacket(generator.getBlockPos(), energy)));
            }
            else
            {
                device.GetEnergyStorage().Insert(joules_per_tick);
                device.setChanged();

                ServerLevel sl = (ServerLevel)level;

                long energy = device.GetEnergyStorage().GetStored();
                sl.getChunkSource().chunkMap.getPlayers(new ChunkPos(device.getBlockPos()), false)
                        .forEach(player -> player.connection.send(new EnergyUpdatePacket(device.getBlockPos(), energy)));
            }
        }

        //TODO: Implement correct generator -> energy acceptor energy interaction
    }
}
