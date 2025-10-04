package martinjanas.mechanica.api.network;

import martinjanas.mechanica.Mechanica;
import martinjanas.mechanica.api.network.impl.BaseNetwork;
import martinjanas.mechanica.api.packet.EnergyUpdatePacket;
import martinjanas.mechanica.block_entities.BlockEntityEnergyAcceptor;
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
    boolean flag = false;

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
        int generator_count = 0;
        int acceptor_count = 0;

        for (var device : devices.values())
        {
            if (device instanceof BlockEntityGenerator)
                generator_count++;
            else if (device instanceof BlockEntityEnergyAcceptor)
                acceptor_count++;
        }

        if (generator_count == 0 || acceptor_count == 0)
            return;

        int total_generated_rf = generator_count * BlockEntityGenerator.RF_PER_TICK;
        List<BaseMachineBlockEntity> non_full_acceptors = devices.values().stream()
                .filter(dev -> dev instanceof BlockEntityEnergyAcceptor && !dev.GetEnergyStorage().IsFull())
                .map(dev -> (BaseMachineBlockEntity) dev)
                .toList();

        if (non_full_acceptors.isEmpty())
            return;

        int rf_per_acceptor = total_generated_rf / non_full_acceptors.size();
        for (var acceptor : non_full_acceptors)
        {
            acceptor.GetEnergyStorage().receiveEnergy(rf_per_acceptor, false);
            acceptor.setChanged();
        }

        int energy_used = rf_per_acceptor * non_full_acceptors.size();
        int energy_per_generator = energy_used / generator_count;
        for (var device : devices.values())
        {
            if (device instanceof BlockEntityGenerator generator)
            {
                generator.GetEnergyStorage().extractEnergy(energy_per_generator, false);
                generator.setChanged();
            }

            //ServerLevel sl = (ServerLevel) level;
            //int energy = device.GetEnergyStorage().getEnergyStored();
            //PacketDistributor.sendToPlayersTrackingChunk(sl, new ChunkPos(device.getBlockPos()), new EnergyUpdatePacket(device.getBlockPos(), energy));
        }
    }
}
