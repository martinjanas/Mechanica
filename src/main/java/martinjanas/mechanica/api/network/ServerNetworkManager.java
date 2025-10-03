package martinjanas.mechanica.api.network;

import martinjanas.mechanica.api.network.impl.BaseNetwork;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class ServerNetworkManager
{
    private static ServerNetworkManager instance = null;

    private final HashMap<String, BaseNetwork<?>> networks = new HashMap<>();

    public static ServerNetworkManager Get()
    {
        if (instance == null)
            instance = new ServerNetworkManager();
        return instance;
    }

    // Called every server tick
    public void OnServerTick(ServerLevel level)
    {
        for (var network : networks.values())
            network.OnServerTick(level);
    }

    public Map<String, BaseNetwork<?>> GetNetworks()
    {
        return networks;
    }

    public <T extends BaseNetwork<?>> boolean Register(String network_name, Supplier<? extends T> factory)
    {
        if (networks.containsKey(network_name))
            return false;

        networks.put(network_name, factory.get());
        return true;
    }

    public <T extends BaseMachineBlockEntity> boolean Join(String network_name, T device)
    {
        if (!networks.containsKey(network_name))
            return false;

        @SuppressWarnings("unchecked")
        BaseNetwork<T> network = (BaseNetwork<T>) networks.get(network_name);
        network.Join(device.GetUUID(), device);
        return true;
    }

    public <T extends BaseMachineBlockEntity> boolean Disconnect(String network_name, T device)
    {
        if (!networks.containsKey(network_name))
            return false;

        @SuppressWarnings("unchecked")
        BaseNetwork<T> network = (BaseNetwork<T>) networks.get(network_name);
        network.Disconnect(device.GetUUID());
        return true;
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
            BaseNetwork<BaseMachineBlockEntity> net = (BaseNetwork<BaseMachineBlockEntity>) network;
            BaseMachineBlockEntity device = net.GetDevicesByUUID().get(id);
            if (device != null)
                return device;
        }
        return null;
    }

    public void UpdateNetworksFromData(List<NetworkData> data_list, Level level)
    {
        for (NetworkData data : data_list)
        {
            BaseNetwork<BaseMachineBlockEntity> net = switch (data.network_type())
            {
                case ENERGY -> new EnergyNetwork(data.network_name());
                case FLUID -> new EnergyNetwork(data.network_name());
            };

            for (var block_pos : data.device_positions())
            {
                var device = level.getBlockEntity(block_pos);
                if (device instanceof BaseMachineBlockEntity machine)
                    net.Join(machine.GetUUID(), machine);
            }

            networks.put(data.network_name(), net);
        }
    }
}
