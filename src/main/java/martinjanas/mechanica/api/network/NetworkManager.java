package martinjanas.mechanica.api.network;

import martinjanas.mechanica.api.network.impl.BaseNetwork;
import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import java.util.HashMap;
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

    public void OnServerTick()
    {
        for (var network : networks.values())
            network.OnServerTick();
    }

    public String GetNetworkName(int index)
    {
        if (index < 0 || index >= networks.size())
            return null;
        return (String) networks.keySet().toArray()[index];
    }

    public final HashMap<String, BaseNetwork<?>> GetNetworks()
    {
        return networks;
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
            network.Join(device);
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
