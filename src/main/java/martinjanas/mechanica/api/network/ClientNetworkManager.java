package martinjanas.mechanica.api.network;

import martinjanas.mechanica.api.network.impl.BaseNetwork;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClientNetworkManager
{
    private static ClientNetworkManager instance = null;

    private final HashMap<String, BaseNetwork<?>> networks = new HashMap<>();

    public static ClientNetworkManager Get()
    {
        if (instance == null)
            instance = new ClientNetworkManager();
        return instance;
    }

    public Map<String, BaseNetwork<?>> GetNetworks()
    {
        return networks;
    }

    public void ClearNetworks()
    {
        networks.clear();
    }

    public void UpdateNetworksFromData(List<NetworkData> data_list, Level clientLevel)
    {
        networks.clear();

        for (NetworkData data : data_list)
        {
            BaseNetwork<BaseMachineBlockEntity> net = switch (data.network_type())
            {
                case ENERGY -> new EnergyNetwork(data.network_name());
                case FLUID -> new EnergyNetwork(data.network_name());
            };

            for (var block_pos : data.device_positions())
            {
                var device = clientLevel.getBlockEntity(block_pos);
                if (device instanceof BaseMachineBlockEntity machine)
                    net.Join(machine.GetUUID(), machine); // only GUI tracking
            }

            networks.put(data.network_name(), net);
        }
    }

    public BaseNetwork<?> GetNetworkByDevice(UUID device_id)
    {
        return networks.values().stream()
                .filter(net -> net.HasDevice(device_id))
                .findFirst()
                .orElse(null);
    }
}
