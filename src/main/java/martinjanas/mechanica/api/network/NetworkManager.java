package martinjanas.mechanica.api.network;

import martinjanas.mechanica.Mechanica;
import martinjanas.mechanica.api.network.impl.BaseNetwork;
import martinjanas.mechanica.api.packet.JoinNetworkPacket;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class NetworkManager
{
    private static NetworkManager instance = null;
    private HashMap<String, BaseNetwork<?>> networks = new HashMap<String, BaseNetwork<?>>();

    public static NetworkManager Get()
    {
        if (instance == null)
            instance = new NetworkManager();

        return instance;
    }

    public void OnServerTick(Level level)
    {
        for (var network : networks.values())
            network.OnServerTick(level);
    }

    public String GetNetworkName(int index)
    {
        if (index < 0 || index >= networks.size())
            return null;
        return (String) networks.keySet().toArray()[index];
    }

    public HashMap<String, BaseNetwork<?>> GetNetworks()
    {
        return networks;
    }

    public void UpdateNetworksFromData(List<NetworkData> data_list, boolean is_client, Level level)
    {
        if (is_client)
        {
            for (NetworkData data : data_list)
            {
                BaseNetwork<BaseMachineBlockEntity> net = switch (data.network_type())
                {
                    case ENERGY -> new EnergyNetwork(data.network_name());
                    // Add more types as needed
                    case FLUID -> new EnergyNetwork(data.network_name());
                };

                for (var block_pos : data.device_positions())
                {
                    BlockEntity device = level.getBlockEntity(block_pos);
                    if (device instanceof BaseMachineBlockEntity machine)
                        net.Join(machine.GetUUID(), machine);
                }

                networks.put(data.network_name(), net);
            }
        }
    }

    public BaseNetwork<?> GetNetworkByDevice(UUID device_id)
    {
        return networks.values().stream()
                .filter(net -> net.HasDevice(device_id))
                .findFirst()
                .orElse(null);
    }

    public BaseMachineBlockEntity GetDeviceByUUID(UUID id)
    {
        for (var network : networks.values())
        {
            @SuppressWarnings("unchecked")
            BaseNetwork<BaseMachineBlockEntity> net = (BaseNetwork<BaseMachineBlockEntity>)network;

            Map<UUID, BaseMachineBlockEntity> devicesByUUID = net.GetDevicesByUUID();
            System.out.println("Checking network " + net.GetName() + ", devices: " + devicesByUUID.keySet());

            BaseMachineBlockEntity device = net.GetDevicesByUUID().get(id);
            if (device != null)
                return device;
        }
        return null;
    }

    public <T extends BaseNetwork<?>> boolean Register(String network_name, Supplier<? extends T> factory)
    {
        if (networks.containsKey(network_name))
            return false;

        T network = factory.get();
        networks.put(network_name, network);

        return true;
    }

    public <T extends BaseMachineBlockEntity> boolean Join(String network_name, T device)
    {
        if (!networks.containsKey(network_name))
            return false;

        BaseNetwork<T> network = (BaseNetwork<T>) networks.get(network_name);
        if (network != null)
        {
            network.Join(device.GetUUID(), device);
            System.out.println("Block added to network: " + network_name + " - " + BuiltInRegistries.BLOCK.getKey(device.getBlockState().getBlock()).toString());

            return true;
        }

        return false;
    }

    public <T extends BaseMachineBlockEntity> boolean Disconnect(String network_name, T device)
    {
        if (!networks.containsKey(network_name))
            return false;

        BaseNetwork<T> network = (BaseNetwork<T>) networks.get(network_name);
        if (network != null)
        {
            network.Disconnect(device);
            System.out.println("Block removed from network: " + BuiltInRegistries.BLOCK.getKey(device.getBlockState().getBlock()).toString());

            return true;
        }

        return false;
    }
}
