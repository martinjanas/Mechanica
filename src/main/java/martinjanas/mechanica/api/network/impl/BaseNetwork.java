package martinjanas.mechanica.api.network.impl;

import martinjanas.mechanica.block_entities.impl.BaseMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseNetwork<T extends BaseMachineBlockEntity>
{
    private final String network_name;
    public HashMap<UUID, T> devices = new HashMap<>();

    protected BaseNetwork(String network_name)
    {
        this.network_name = network_name;
    }

    public abstract List<UUID> GetDeviceUUIDs();

    public String GetName()
    {
        return network_name;
    }

    public Map<UUID, T> GetDevicesByUUID()
    {
        return devices.values().stream().collect(Collectors.toMap(BaseMachineBlockEntity::GetUUID, d -> d));
    }

    public List<BlockPos> GetDevicePositions()
    {
        return devices.values().stream()
                .map(BaseMachineBlockEntity::getBlockPos)
                .toList();
    }

    public void Join(UUID uuid, T device)
    {
        devices.put(uuid, device);
    }

    public void Disconnect(UUID uuid)
    {
        devices.remove(uuid);
    }

    public final HashMap<UUID, T> GetDevices()
    {
        return devices;
    }

    public void UpdateDevices(HashMap<UUID, T> new_devices)
    {
        devices.clear();
        devices.putAll(new_devices);
    }

    public boolean HasDevice(UUID id)
    {
        for (var device : devices.values())
        {
            if (device instanceof BaseMachineBlockEntity d && d.GetUUID().equals(id))
                return true;
        }

        return false;
    }

    public abstract void OnServerTick(Level level);
}
