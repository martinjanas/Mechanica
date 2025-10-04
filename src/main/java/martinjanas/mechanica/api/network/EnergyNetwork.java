package martinjanas.mechanica.api.network;

import martinjanas.mechanica.api.network.impl.BaseNetwork;
import martinjanas.mechanica.api.packet.EnergyUpdatePacket;
import martinjanas.mechanica.block_entities.BlockEntityGenerator;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import java.util.List;
import java.util.UUID;

public class EnergyNetwork extends BaseNetwork<BaseMachineBlockEntity>
{
    int rf_per_tick = 0;

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
                generator.GetEnergyStorage().extractEnergy(generator.RF_PER_TICK, false);
                generator.setChanged();
                rf_per_tick = generator.RF_PER_TICK;

                ServerLevel sl = (ServerLevel)level;
                int energy = generator.GetEnergyStorage().getEnergyStored();
                PacketDistributor.sendToPlayersTrackingChunk(sl, new ChunkPos(generator.getBlockPos()), new EnergyUpdatePacket(generator.getBlockPos(), energy));
            }
            else
            {
                device.GetEnergyStorage().receiveEnergy(rf_per_tick, false);
                device.setChanged();

                ServerLevel sl = (ServerLevel)level;
                int energy = device.GetEnergyStorage().getEnergyStored();
                PacketDistributor.sendToPlayersTrackingChunk(sl, new ChunkPos(device.getBlockPos()), new EnergyUpdatePacket(device.getBlockPos(), energy));
            }
        }

        //TODO: Implement correct generator -> energy acceptor energy interaction
    }
}
